--liquibase formatted sql
--changeset belyaev.maxim:1.0.4

alter table product add column category_id uuid;

alter table product add foreign key(category_id) references category(id);