--liquibase formatted sql
--changeset belyaev.maxim:1.0.2

create table if not exists review (
    id bigserial primary key,
    type text constraint typechk check (char_length(type) <= 63 ),
    date timestamptz not null,
    product_rating int not null,
    text text constraint  textchk check ( char_length(text) <= 4095 ),
    review_like int not null,
    review_dislike int not null,
    product_id bigint references product(id),
    client_id bigint references client(id)
);

create table if not exists review_attachment (
    id bigserial primary key,
    type text constraint typechk check ( char_length(type) <= 63 ),
    attachment text constraint  attachmentchk check ( char_length(attachment) <= 1023 ),
    review_id bigint references review(id)
);

create table if not exists review_comment(
    review_id bigint references review(id),
    comment_id bigint references review(id)
);