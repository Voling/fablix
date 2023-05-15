DELIMITER //

CREATE FUNCTION add_star(starname VARCHAR(100),birthyear integer) RETURNS VARCHAR(10) no sql
BEGIN
  DECLARE unique_id VARCHAR(10);
  SET unique_id = CONCAT(SUBSTRING(MD5(RAND()), 1, 5), SUBSTRING(MD5(NOW()), 1, 5));
  INSERT INTO stars(id, name, birthYear) VALUES (unique_id, starname,birthyear);
  RETURN unique_id;
END //

DELIMITER ;
