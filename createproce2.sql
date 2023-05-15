DELIMITER //

CREATE PROCEDURE add_genre(genrename VARCHAR(32),out theid int)
BEGIN
  INSERT INTO genres(name) VALUES (genrename);
  SELECT LAST_INSERT_ID() into theid;
END //

DELIMITER ;
