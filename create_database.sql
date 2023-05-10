create table bot
(
    id          int auto_increment
        primary key,
    user_id     int            not null,
    title       varchar(100)   null,
    description varchar(300)   null,
    content     varchar(10000) null,
    create_time datetime       null,
    modify_time datetime       null,
    constraint bot_id_uindex
        unique (id)
);

create table record
(
    id          int auto_increment
        primary key,
    a_id        int           null,
    a_sx        int           null,
    a_sy        int           null,
    b_id        int           null,
    b_sx        int           null,
    b_sy        int           null,
    a_steps     varchar(1000) null,
    b_steps     varchar(1000) null,
    map         varchar(1000) null,
    create_time datetime      null,
    loser       varchar(10)   null,
    constraint record_id_uindex
        unique (id)
);

create table user
(
    id       int auto_increment
        primary key,
    username varchar(100)     null,
    password varchar(100)     null,
    rating   int default 1500 null,
    photo    varchar(1000)    null
);

