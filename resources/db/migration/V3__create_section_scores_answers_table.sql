CREATE TABLE IF NOT EXISTS sectionscores (
	id INT GENERATED ALWAYS AS IDENTITY,
	user_id INT,
	section_id varchar(255),
	section_type varchar(255),
	PRIMARY KEY(id),
	CONSTRAINT fk_user
      FOREIGN KEY(user_id)
	  REFERENCES users(id)
	  ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS answers (
	id INT GENERATED ALWAYS AS IDENTITY,
	section_id int,
	question_id varchar(255),
	score int,
	PRIMARY KEY(id),
	CONSTRAINT fk_user_section
      FOREIGN KEY(section_id)
	  REFERENCES sectionscores(id)
	  ON DELETE CASCADE
);