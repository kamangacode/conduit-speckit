create table users (
    id uuid primary key,
    email varchar(320) not null unique,
    username varchar(255) not null unique,
    password_hash varchar(500) not null,
    bio text,
    image varchar(2048)
);
