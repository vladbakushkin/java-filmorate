create table if not exists MPA
(
    ID     INTEGER auto_increment,
    RATING CHARACTER VARYING,
    constraint MPA_PK
        primary key (ID)
);
create table if not exists FILM
(
    ID           INTEGER auto_increment,
    NAME         CHARACTER VARYING not null,
    DESCRIPTION  CHARACTER VARYING(255),
    RATING_ID    INTEGER,
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    constraint FILM_PK
        primary key (ID),
    constraint FILM_MPA_ID_FK
        foreign key (RATING_ID) references MPA
);
create table if not exists GENRE
(
    ID   INTEGER auto_increment,
    NAME CHARACTER VARYING,
    constraint GENRE_PK
        primary key (ID)
);
create table if not exists FILM_GENRE
(
    ID       INTEGER auto_increment,
    FILM_ID  INTEGER,
    GENRE_ID INTEGER,
    constraint FILM_GENRE_PK
        primary key (ID),
    constraint FILM_GENRE_FILM_ID_FK
        foreign key (FILM_ID) references FILM,
    constraint FILM_GENRE_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRE
);
create table if not exists USER_ACCOUNT
(
    ID       INTEGER auto_increment,
    EMAIL    CHARACTER VARYING not null,
    LOGIN    CHARACTER VARYING not null,
    NAME     CHARACTER VARYING,
    BIRTHDAY DATE,
    constraint USER_ACCOUNT_PK
        primary key (ID)
);
create table if not exists FILM_LIKE
(
    ID      INTEGER auto_increment,
    FILM_ID INTEGER,
    USER_ID INTEGER,
    constraint FILM_LIKE_PK
        primary key (ID),
    constraint FILM_LIKE_FILM_ID_FK
        foreign key (FILM_ID) references FILM,
    constraint FILM_LIKE_USER_ACCOUNT_ID_FK
        foreign key (USER_ID) references USER_ACCOUNT
);
create table if not exists FRIENDSHIP
(
    ID                    INTEGER auto_increment,
    USER_ID               INTEGER,
    FRIEND_ID             INTEGER,
    FRIEND_STATUS_CONFIRM BOOLEAN,
    constraint FRIENDSHIP_PK
        primary key (ID),
    constraint FRIENDSHIP_USER_ACCOUNT_ID_FK
        foreign key (USER_ID) references USER_ACCOUNT,
    constraint FRIENDSHIP_USER_ACCOUNT_ID_FK_2
        foreign key (FRIEND_ID) references USER_ACCOUNT
);

