
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

