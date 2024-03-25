create table if not exists MPA
(
    ID     INTEGER auto_increment,
    NAME CHARACTER VARYING(255) not null,
    constraint MPA
        primary key (ID)
);
create table if not exists FILM
(
    ID           INTEGER auto_increment,
    NAME         CHARACTER VARYING(255) not null,
    DESCRIPTION  CHARACTER VARYING(255),
    MPA_ID INTEGER,
    RELEASE_DATE DATE                   not null,
    DURATION     INTEGER                not null,
    constraint FILM_PK
        primary key (ID),
    constraint FILM_MPA_ID_FK
        foreign key (MPA_ID) references MPA
);
create table if not exists GENRE
(
    ID   INTEGER auto_increment,
    NAME CHARACTER VARYING(255) not null,
    constraint GENRE
        primary key (ID)
);
create table if not exists FILM_GENRE
(
    FILM_ID  INTEGER not null
        references FILM
            on update cascade on delete cascade,
    GENRE_ID INTEGER not null
        references GENRE
            on update cascade,
    constraint FILM_GENRE_PKEY
        primary key (FILM_ID, GENRE_ID)
);
create table if not exists USER_ACCOUNT
(
    ID       INTEGER auto_increment,
    EMAIL CHARACTER VARYING(255) not null,
    LOGIN CHARACTER VARYING(255) not null,
    NAME  CHARACTER VARYING(255),
    BIRTHDAY DATE,
    constraint USER_ACCOUNT
        primary key (ID)
);
create table if not exists FILM_LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint FILM_LIKES_PK
        primary key (FILM_ID, USER_ID),
    constraint FILM_LIKES_FILM_ID_FK
        foreign key (FILM_ID) references FILM,
    constraint FILM_LIKE_USER_ACCOUNT_ID_FK
        foreign key (USER_ID) references USER_ACCOUNT
);
create table if not exists FRIENDSHIP
(
    USER_ID               INTEGER not null,
    FRIEND_ID             INTEGER not null,
    FRIEND_STATUS_CONFIRM BOOLEAN not null,
    constraint FRIENDSHIP_USER_ACCOUNT_ID_FK
        foreign key (USER_ID) references USER_ACCOUNT,
    constraint FRIENDSHIP_USER_ACCOUNT_ID_FK_2
        foreign key (FRIEND_ID) references USER_ACCOUNT
);

