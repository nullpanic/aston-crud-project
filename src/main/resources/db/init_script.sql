-- create database crud_db
--     with owner admin;

create sequence roles_id_seq;

alter sequence roles_id_seq owner to admin;

create sequence user_messages_id_seq;

alter sequence user_messages_id_seq owner to admin;

create table users
(
    id   bigserial
        constraint users_pk
            primary key
        unique,
    name varchar(128) not null
);

alter table users
    owner to admin;

create table roles
(
    id   bigint default nextval('roles_id_seq'::regclass) not null
        constraint roles_pk
            primary key
        unique,
    name varchar(128)                                     not null
);

alter table roles
    owner to admin;

create table user_roles
(
    user_id bigint not null
        constraint user_id___fk
            references users
            on delete cascade,
    role_id bigint not null
        constraint role_id___fk
            references roles
            on delete cascade,
    constraint user_roles_pk
        primary key (user_id, role_id)
);

alter table user_roles
    owner to admin;

