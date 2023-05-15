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
