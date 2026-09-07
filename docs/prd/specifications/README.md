# Specifications RealWorld / Conduit (local)

> Verbatim Markdown copy of the official RealWorld specifications, retrieved from the [`gothinkster/realworld`](https://github.com/gothinkster/realworld) (`docs/src/content/docs/`) repository.
> Upstream source: https://realworld-docs.netlify.app/
> Recovery date: 2026-06-04.
>
> The PRD synthesized and adapted to our 5 rests is in [`../PRD-conduit.md`](../PRD-conduit.md). These files are the **reference raw spec**.

## Index

### Overview
- [`introconduition.md`](introconduition.md) - presentation of RealWorld / Conduit
- [`implementation-creation/introconduition.md`](implementation-creation/introconduition.md)
- [`implementation-creation/expectations.md`](implementation-creation/expectations.md) - what an implementation must respect
- [`implementation-creation/features.md`](implementation-creation/features.md) - feature list

### Backend
- [`backend/introconduition.md`](backend/introconduition.md)
- [`backend/openapi.yml`](backend/openapi.yml) - **official OpenAPI 3.1.0 contract** (RealWorld Conduit API v2.0.0), machine-readable source of the API
- [`backend/endpoints.md`](backend/endpoints.md) - **all API endpoints** (readable version)
- [`backend/api-response-format.md`](backend/api-response-format.md) - **response JSON formats**
- [`backend/error-handling.md`](backend/error-handling.md) - error codes and format
- [`backend/cors.md`](backend/cors.md)
- [`backend/tests.md`](backend/tests.md) - how to test a backend
- [`backend/hurl.md`](backend/hurl.md) - suite Hurl (API testing source of truth)
- [`backend/bruno.md`](backend/bruno.md) - collection Bruno (generated)
- [`backend/postman.md`](backend/postman.md) - (Postman legacy)

### Frontend
- [`frontend/api.md`](frontend/api.md) - how to consume the API
- [`frontend/routing.md`](frontend/routing.md) - page routes
- [`frontend/templates.md`](frontend/templates.md) - HTML templates / UI features (largest file)
- [`frontend/styles.md`](frontend/styles.md)
- [`frontend/tests.md`](frontend/tests.md)

### Mobile
- [`mobile-specs/introconduition.md`](mobile-specs/introconduition.md)

## Notes

- Internal links in these files (of type `/specifications/backend/...`) are **absolutely relative to the upstream site** and do not resolve locally. To navigate locally, use this index.
- The official OpenAPI contract is retrieved locally in [`backend/openapi.yml`](backend/openapi.yml) (comes from `specs/api/openapi.yml` from the upstream repository, not from `docs/`).
- To refresh: re-download from `gothinkster/realworld` (`docs/src/content/docs/`).
