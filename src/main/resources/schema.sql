DROP TABLE IF EXISTs genres CASCADE;
CREATE TABLE genres (
	id int  PRIMARY KEY,
	name varchar
);
DROP TABLE IF EXISTs mpa CASCADE;
CREATE TABLE  mpa (
	id int PRIMARY KEY,
	name varchar
);
DROP TABLE IF EXISTs films CASCADE;
CREATE TABLE  films (
    film_id int PRIMARY KEY AUTO_INCREMENT,
    name varchar,
    description varchar,
    release_date date,
    duration int,
    mpa_id int REFERENCES mpa (id)
);
DROP TABLE IF EXISTs film_genres CASCADE;
CREATE TABLE film_genres (
    film_id int REFERENCES films (film_id),
    genre_id int REFERENCES genres (id),
    film_genres_id int PRIMARY KEY AUTO_INCREMENT
);

DROP TABLE IF EXISTs users CASCADE;
CREATE TABLE  users (
    user_id int PRIMARY KEY AUTO_INCREMENT,
    email varchar,
    login varchar,
    name varchar,
    birthday date
);
DROP TABLE IF EXISTs likes CASCADE;
CREATE TABLE  likes (
    film_id int REFERENCES films (film_id),
    user_id int REFERENCES users (user_id),
    like_id int PRIMARY KEY AUTO_INCREMENT
);
DROP TABLE IF EXISTs friends CASCADE;
CREATE TABLE  friends (
    user_id int REFERENCES users (user_id),
    friend_id int REFERENCES users (user_id),
    status boolean DEFAULT FALSE,
    friendships_id int PRIMARY KEY AUTO_INCREMENT
);

