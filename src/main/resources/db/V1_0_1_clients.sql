--liquibase formatted sql
--changeset belyaev.maxim:1.0.1

create table if not exists client (
    id bigserial primary key,
    first_name text constraint fnamechk check(char_length(first_name) <= 255),
    last_name text constraint lnamechk check(char_length(last_name) <= 255),
    phone text constraint phonechk check(char_length(phone) <= 12),
    city text constraint citychk check(char_length(city) <= 255),
    street text constraint streetchk check(char_length(street) <= 255),
    house text constraint housechk check(char_length(house) <= 64),
    flat text constraint flatchk check(char_length(flat) <= 64)
);
