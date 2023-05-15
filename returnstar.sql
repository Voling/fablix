DELIMITER //
create procedure getcount(in starname varchar(100), out thecount integer)
BEGIN
select count(*) into thecount from stars where name = starname;
END//
DELIMITER ;
