--liquibase formatted sql
--changeset belyaev.maxim:1.0.0

create table if not exists vendor (
    id bigserial primary key,
    name varchar(255) not null,
    logo text
);

create table if not exists product (
    id bigserial primary key,
    title varchar(255) not null,
    photo text constraint photochk check(char_length(photo) <= 1024),
    price float8 not null,
    description text constraint descchk check(char_length(description) <= 2048),
    count int4 check(count >= 0),
    vendor_id   bigint,
    foreign key(vendor_id) references vendor(id)
);
