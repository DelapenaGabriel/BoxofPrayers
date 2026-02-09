-- Migration to add repost support
-- Adds original_post_id column to posts table to enable reposts

BEGIN TRANSACTION;

-- Add original_post_id column to posts table
ALTER TABLE posts 
ADD COLUMN original_post_id INT REFERENCES posts(id) ON DELETE CASCADE;

-- Create index for performance when querying reposts
CREATE INDEX idx_posts_original_post_id ON posts(original_post_id);

COMMIT TRANSACTION;
