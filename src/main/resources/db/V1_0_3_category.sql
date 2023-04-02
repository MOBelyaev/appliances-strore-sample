--liquibase formatted sql
--changeset belyaev.maxim:1.0.3

create table if not exists category (
    id uuid not null,
    parent_id uuid,
    name text constraint nameCategory check (char_length(name) <= 1024),
    logo text constraint logochk check (char_length(logo) <= 2048),
    primary key (id),
    foreign key (parent_id) references category(id)
);

