-- Insert test user for development/testing
-- Only runs if the user doesn't already exist
INSERT INTO users (username, email, password)
SELECT 'testuser', 'test@example.com', '$2a$12$qi.J4ZdiIsU/Q1wBxlb/G.p8x1wU3m/P1THHkDLztr4JATY4ALISa'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'testuser'
);

-- Password is 'password123' (BCrypt encoded)