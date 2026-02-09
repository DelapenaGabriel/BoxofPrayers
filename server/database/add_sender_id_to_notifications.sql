-- Add sender_id to notifications table
ALTER TABLE notifications ADD COLUMN sender_id INT;
ALTER TABLE notifications ADD CONSTRAINT fk_notifications_sender FOREIGN KEY (sender_id) REFERENCES users(id);
