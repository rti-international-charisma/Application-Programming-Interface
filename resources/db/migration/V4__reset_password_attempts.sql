ALTER TABLE users
ADD COLUMN reset_password_attempts_left Int DEFAULT (5);

