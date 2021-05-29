ALTER TABLE users
ADD COLUMN last_login timestamp DEFAULT now();