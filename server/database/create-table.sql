START TRANSACTION;

DROP TABLE IF EXISTS user_badges CASCADE;
DROP TABLE IF EXISTS badges CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS prayers CASCADE;
DROP TABLE IF EXISTS prayer_requests CASCADE;
DROP TABLE IF EXISTS prayer_activity CASCADE;
DROP TABLE IF EXISTS users CASCADE;


CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR (100) NOT NULL,
    display_name VARCHAR (100) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    profile_image VARCHAR(255)
);

CREATE TABLE prayer_requests (
  id SERIAL PRIMARY KEY,
  requester_id INT REFERENCES users(id) ON DELETE SET NULL,
  name VARCHAR(100) NOT NULL,
  content TEXT NOT NULL,
  category VARCHAR(50) DEFAULT 'General',
  created_at TIMESTAMP DEFAULT NOW(),
  is_visible BOOLEAN DEFAULT FALSE,
  is_answered BOOLEAN DEFAULT FALSE,
  answer_content TEXT
);

CREATE TABLE prayers (
  id SERIAL PRIMARY KEY,
  prayer_request_id INT REFERENCES prayer_requests(id) ON DELETE CASCADE NOT NULL,
  user_id INT REFERENCES users(id) ON DELETE CASCADE,
  prayed_at TIMESTAMP DEFAULT NOW(),
  UNIQUE (prayer_request_id, user_id)
);

CREATE TABLE prayer_activity (
  day DATE PRIMARY KEY,
  prayer_count INT NOT NULL DEFAULT 0
);

CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    prayer_request_id INT REFERENCES prayer_requests(id) ON DELETE CASCADE,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE badges (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    icon_url VARCHAR(255),
    criteria VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_badges (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    badge_id INT REFERENCES badges(id) ON DELETE CASCADE,
    awarded_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (user_id, badge_id)
);

-- Insert default badges
INSERT INTO badges (name, description, criteria, icon_url) VALUES 
('First Prayer', 'Awarded for praying for the first time.', '1_prayer', '/badges/prayer.png'),
('Prayer Warrior', 'Awarded for praying 10 times.', '10_prayers', '/badges/prayer.png'),
('Encourager', 'Awarded for creating 5 prayer requests.', '5_requests', '/badges/request.png'),
('Faithful Servant', 'Awarded for 25 prayers.', '25_prayers', '/badges/prayer.png'),
('Devoted Heart', 'Awarded for 50 prayers.', '50_prayers', '/badges/prayer.png'),
('Spirit of Intercession', 'Awarded for 100 prayers.', '100_prayers', '/badges/prayer.png'),
('Guardian of Faith', 'Awarded for 250 prayers.', '250_prayers', '/badges/prayer.png'),
('Pillar of Light', 'Awarded for 500 prayers.', '500_prayers', '/badges/prayer.png'),
('Eternal Watchman', 'Awarded for 1000 prayers.', '1000_prayers', '/badges/prayer.png'),
('Early Riser', 'Awarded for a 3-day prayer streak.', '3_day_streak', '/badges/streak.png'),
('Week of Grace', 'Awarded for a 7-day prayer streak.', '7_day_streak', '/badges/streak.png'),
('Fortnight of Faith', 'Awarded for a 14-day prayer streak.', '14_day_streak', '/badges/streak.png'),
('Month of Miracles', 'Awarded for a 30-day prayer streak.', '30_day_streak', '/badges/streak.png'),
('Season of Prayer', 'Awarded for a 90-day prayer streak.', '90_day_streak', '/badges/streak.png'),
('Year of Devotion', 'Awarded for a 365-day prayer streak.', '365_day_streak', '/badges/streak.png'),
('Open Heart', 'Awarded for creating 10 prayer requests.', '10_requests', '/badges/request.png'),
('Burden Sharer', 'Awarded for creating 25 prayer requests.', '25_requests', '/badges/request.png'),
('Voice of Hope', 'Awarded for creating 50 prayer requests.', '50_requests', '/badges/request.png'),
('Morning Light', 'Awarded for praying between 5am and 8am.', 'morning_prayer', '/badges/time.png'),
('Night Watch', 'Awarded for praying between 11pm and 3am.', 'night_prayer', '/badges/time.png'),
('Weekend Warrior', 'Awarded for praying on a weekend.', 'weekend_prayer', '/badges/time.png'),
('First Step', 'Awarded for creating your first prayer request.', '1_request', '/badges/request.png'),
('Community Builder', 'Awarded for creating 100 prayer requests.', '100_requests', '/badges/request.png'),
('Prayer Champion', 'Awarded for 5000 prayers.', '5000_prayers', '/badges/prayer.png');

COMMIT TRANSACTION;