# Filmorate - кинопоиск для своих

Социальная сеть, которая поможет выбрать кино на основе того, какие фильмы вы и ваши друзья смотрите и какие оценки им
ставите.

## Технологии, применяемые в проекте

- Spring boot
- Maven
- PostgreSQL

## Диаграмма базы данных

### ER model

![diagram](resources/FILMORATE.png)

<details> <summary>Описание базы данных в текстовом виде</summary>  

**FILM**

Содержит информацию о фильмах.

Таблица состоит из полей:

- `id` — **primary key** — идентификатор фильма;
- `name` — название фильма (не может быть пустым);
- `description` — описание фильма;
- `mpa_id` — **foreign key** (отсылает к таблице `mpa`) — идентификатор жанра;
- `release_date` — год выхода;
- `duration` — продолжительность фильма в минутах;

**MPA**

Содержит информацию о рейтингах ассоциации кинокомпаний (англ._ Motion Picture Association,_ сокращённо _МРА_).

Таблица состоит из полей:

- `id` — **primary key** — идентификатор рейтинга;
- `rating` — возрастной рейтинг, например:
    - `PG` — детям рекомендуется смотреть такой фильм с родителями;
    - `PG-13` — детям до 13 лет смотреть такой фильм нежелательно.

**FILM_GENRE**

Связывает фильмы с жанрами.

Таблица состоит из полей:

- `film_id` — **primary key** (отсылает к таблице `film`) — идентификатор фильма;
- `genre_id` — **primary key** (отсылает к таблице `genre`) — идентификатор жанра.

**GENRE**

Содержит информацию о жанрах кино.

Таблица состоит из полей:

`id` — **primary key** — идентификатор жанра;
- `name` — название жанра, например:
  - `Комедия` — комедия;
  - `Драма` — драма.

**FILM_LIKES**

Связывает фильмы с пользователями, которые их оценили.

Таблица состоит из полей:

- `film_id` — **primary key** (отсылает к таблице `film`) — идентификатор фильма;
- `user_id` — **primary key** (отсылает к таблице `user`) — идентификатор пользователя;

**USER_ACCOUNT**

Содержит информацию о пользователях.

Таблица состоит из полей:

- `id` — **primary key** — идентификатор пользователя;
- `email` — электронная почта пользователя;
- `login` — логин пользователя (не может быть пустым);
- `name` — имя пользователя;
- `birthday` — дата рождения пользователя;

**FRIENDSHIP**

Связывает пользователей, которые являются друзьями, и указывает, подтверждена ли эта дружба.

Таблица состоит из полей:

- `id` — **primary key** — уникальный идентификатор;
- `user_id` — **foreign key** (отсылает к таблице `user`) — идентификатор пользователя;
- `friend_id` — **foreign key** (отсылает к таблице `user`) — идентификатор друга пользователя;
- `friend_status_confirm` — статус подтверждения дружбы, например:
    - `true` — `friend_id` подтвердил дружбу с пользователем `user_id`;
    - `false` — `friend_id` не подтвердил дружбу с пользователем `user_id`;

</details>  

### Примеры запросов

<details> <summary>Получение списка всех фильмов</summary>  

```sql  
SELECT *  
FROM film  
```  

</details>  

<details> <summary>Получение списка всех пользователей</summary>  

```sql  
SELECT *  
FROM user_account  
```  

</details>  

<details> <summary>Получение списка `?` наиболее популярных фильмов</summary>  

```sql  
SELECT f.name, COUNT(fl.user_id) AS total_likes  
FROM film AS f 
JOIN FILM_LIKES AS fl ON f.id = fl.film_id   
GROUP BY f.name  
ORDER BY total_likes DESC
LIMIT ? -- подставить количество фильмов для вывода  
```  

</details>  

<details> <summary>Получение списка общих друзей с другим пользователем</summary>  

```sql  
SELECT u.id as friend_id, u.email, u.login, u.name, u.birthday
FROM user_account u
JOIN (SELECT friend_id FROM friendship WHERE user_id = ?) fs1
JOIN (SELECT friend_id FROM friendship WHERE user_id = ?) fs2
ON fs1.friend_id = fs2.friend_id
WHERE u.id = fs1.friend_id;
```  

</details>