-- Add is_anonymous column to prayer_requests table
ALTER TABLE prayer_requests 
ADD COLUMN IF NOT EXISTS is_anonymous BOOLEAN DEFAULT FALSE;
