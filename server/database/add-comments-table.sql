CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    prayer_request_id INT REFERENCES prayer_requests(id) ON DELETE CASCADE,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
