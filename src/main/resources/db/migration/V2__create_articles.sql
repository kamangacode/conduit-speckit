create table articles (
    id uuid primary key,
    author_id uuid not null references users(id),
    slug varchar(255) not null unique,
    title varchar(255) not null,
    description varchar(1000) not null,
    body text not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table article_tags (
    article_id uuid not null references articles(id) on delete cascade,
    tag varchar(255) not null,
    primary key (article_id, tag)
);

create index article_tags_tag_idx on article_tags(tag);
create index articles_created_at_idx on articles(created_at desc);