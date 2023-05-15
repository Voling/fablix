create table if not exists movies(
id varchar(10) PRIMARY KEY,
title varchar(100) NOT NULL,
year integer NOT NULL,
director varchar(100) NOT NULL



);

create table if not exists stars(
id varchar(10) PRIMARY KEY,
name varchar(100) NOT NULL,
birthYear integer

);

create table if not exists stars_in_movies(
starId varchar(10) NOT NULL,
movieId varchar(10) NOT NULL,
FOREIGN KEY (starId) REFERENCES stars(id),
FOREIGN KEY (movieId) REFERENCES movies(id)
);

create table if not exists genres(
id integer PRIMARY KEY AUTO_INCREMENT, 
name varchar(32) NOT NULL
);

create table if not exists genres_in_movies(
genreId integer NOT NULL,
movieId varchar(10) NOT NULL,
FOREIGN KEY (genreId) REFERENCES genres(id),
FOREIGN KEY (movieId) REFERENCES movies(id)
);

create table if not exists creditcards(
id varchar(20) PRIMARY KEY,
firstName varchar(50) NOT NULL,
lastName varchar(50) NOT NULL,
expiration date NOT NULL

);
create table if not exists customers(
id integer PRIMARY KEY AUTO_INCREMENT,
firstName varchar(50) NOT NULL,
lastName varchar(50) NOT NULL,
ccId varchar(20) NOT NULL,
address varchar(200) NOT NULL,
email varchar(50) NOT NULL,
password varchar(20) NOT NULL,
FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

create table if not exists sales(
id integer PRIMARY KEY AUTO_INCREMENT,
customerId integer NOT NULL,
movieId varchar(10) NOT NULL,
saleDate date NOT NULL,
FOREIGN KEY (customerId) REFERENCES customers(id),
FOREIGN KEY (movieId) REFERENCES movies(id)

);

create table if not exists ratings(
movieId varchar(10) NOT NULL,
rating float NOT NULL,
numVotes integer NOT NULL,
FOREIGN KEY (movieId) REFERENCES movies(id)
);
create table if not exists user(
email varchar(200) NOT NULL UNIQUE,
password varchar(200) NOT NULL
);
alter table movies add column price float;
update movies set price = ROUND(RAND() * 9 + 1,2);



