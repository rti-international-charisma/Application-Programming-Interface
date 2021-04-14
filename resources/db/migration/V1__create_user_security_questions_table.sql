CREATE TABLE IF NOT EXISTS securityquestions (
	sec_q_id INT GENERATED ALWAYS AS IDENTITY,
	question varchar(255),
	 PRIMARY KEY(sec_q_id)
);

INSERT INTO securityquestions (question)
VALUES('What was your favourite sport in high school?'),
('What is the title and artist of your favourite song?'),
('What is your grandmother''s first name?'),
('What is the name of the boy or girl that you first kissed?'),
('What was your childhood nickname?');

CREATE TABLE IF NOT EXISTS users (
	id INT GENERATED ALWAYS AS IDENTITY,
	username varchar(25),
	password varchar(255),
	sec_answer varchar(50),
	sec_q_id INT,
	PRIMARY KEY(id),
	CONSTRAINT fk_security_question
      FOREIGN KEY(sec_q_id)
	  REFERENCES securityquestions(sec_q_id)
	  ON DELETE SET NULL
);