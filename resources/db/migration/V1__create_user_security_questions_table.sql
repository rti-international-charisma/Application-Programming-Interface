CREATE TABLE IF NOT EXISTS securityquestions (
	sec_q_id INT GENERATED ALWAYS AS IDENTITY,
	question varchar(255),
	 PRIMARY KEY(sec_q_id)
);

INSERT INTO securityquestions (question)
VALUES('What is your mother''s maiden name?'),
('What is the name of your first pet?');

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
);