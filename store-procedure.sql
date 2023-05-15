DELIMITER //

CREATE FUNCTION add_star(starname VARCHAR(100),birthyear integer) RETURNS VARCHAR(10) no sql
BEGIN
  DECLARE unique_id VARCHAR(10);
  SET unique_id = CONCAT(SUBSTRING(MD5(RAND()), 1, 5), SUBSTRING(MD5(NOW()), 1, 5));
  INSERT INTO stars(id, name, birthYear) VALUES (unique_id, starname,birthyear);
  RETURN unique_id;
END //

DELIMITER ;
DELIMITER //

CREATE PROCEDURE add_genre(genrename VARCHAR(32),out theid int)
BEGIN
  INSERT INTO genres(name) VALUES (genrename);
  SELECT LAST_INSERT_ID() into theid;
END //

DELIMITER ;
DELIMITER //

CREATE PROCEDURE add_movie(in movieid varchar(10) ,in movietitle varchar(100), in movieyear integer, in moviedirector varchar(100),in starname varchar(100), in genrename varchar(32))
BEGIN
declare price integer;
declare genreid integer;
declare rating integer;
declare starcount integer;
declare genrecount integer;
set @starid = '';
set price  = ROUND(RAND() * 9 + 1,2);
set rating = 1 + 9*RAND();
insert into movies(id,title,year,director,price) values(movieid, movietitle,movieyear,moviedirector,price);
select count(*) into starcount from stars where name = starname;
if starcount = 0 then
set @starid = (select add_star(starname,2001));
else
select id into @starid from stars where name = starname;
end if;
insert into stars_in_movies(starId,movieId)  values(@starid, movieid);
select count(*) into genrecount from genres where name = genrename;
if genrecount = 0 then
call add_genre(genrename,@genreid);
set genreid = @genreid;
else
select id into genreid from genres where name = genrename;
end if;
insert into genres_in_movies(genreId,movieId) values(genreid,movieid);
insert into ratings(movieId,rating,numVotes) values(movieid,rating,2);
END //

DELIMITER ;
