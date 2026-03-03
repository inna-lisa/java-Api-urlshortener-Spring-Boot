CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY NOT NULL,
    username VARCHAR(2000) UNIQUE NOT NULL,
    password VARCHAR(2000) NOT NULL
);

CREATE TABLE IF NOT EXISTS link (
    short_url VARCHAR(15) PRIMARY KEY,
    url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    expires_at TIMESTAMP,
    open_count INT DEFAULT 0,
    user_id INTEGER NOT NULL,
    CONSTRAINT fk_link_users FOREIGN KEY (user_id) REFERENCES users(id)
);
