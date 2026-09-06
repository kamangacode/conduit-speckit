# Article error contract without deferred feed, favorite, or comment endpoints

POST {{host}}/api/users
{
  "user": {
    "username": "scoped_a_{{uid}}",
    "email": "scoped_a_{{uid}}@test.com",
    "password": "password123"
  }
}
HTTP 201
[Captures]
token_a: jsonpath "$.user.token"

POST {{host}}/api/users
{
  "user": {
    "username": "scoped_b_{{uid}}",
    "email": "scoped_b_{{uid}}@test.com",
    "password": "password123"
  }
}
HTTP 201
[Captures]
token_b: jsonpath "$.user.token"

POST {{host}}/api/articles
{
  "article": {
    "title": "No auth {{uid}}",
    "description": "test",
    "body": "test"
  }
}
HTTP 401
[Asserts]
jsonpath "$.errors.token[0]" == "is missing"

GET {{host}}/api/articles/unknown-scoped-{{uid}}
HTTP 404
[Asserts]
jsonpath "$.errors.article[0]" == "not found"

POST {{host}}/api/articles
Authorization: Token {{token_a}}
{
  "article": {
    "title": "",
    "description": "test",
    "body": "test"
  }
}
HTTP 422
[Asserts]
jsonpath "$.errors.title[0]" == "can't be blank"

POST {{host}}/api/articles
Authorization: Token {{token_a}}
{
  "article": {
    "title": "Scoped ownership {{uid}}",
    "description": "test",
    "body": "test"
  }
}
HTTP 201
[Captures]
slug: jsonpath "$.article.slug"

DELETE {{host}}/api/articles/{{slug}}
Authorization: Token {{token_b}}
HTTP 403
[Asserts]
jsonpath "$.errors.article[0]" == "forbidden"

POST {{host}}/api/articles
Authorization: Token {{token_a}}
{
  "article": {
    "title": "Duplicate {{uid}}",
    "description": "first",
    "body": "first"
  }
}
HTTP 201
[Captures]
slug_one: jsonpath "$.article.slug"

POST {{host}}/api/articles
Authorization: Token {{token_a}}
{
  "article": {
    "title": "Duplicate {{uid}}",
    "description": "second",
    "body": "second"
  }
}
HTTP 201
[Asserts]
jsonpath "$.article.slug" != "{{slug_one}}"