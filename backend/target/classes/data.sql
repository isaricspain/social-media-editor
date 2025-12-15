-- Insert test user for development/testing
-- Only runs if the user doesn't already exist
INSERT INTO users (username, email, password)
SELECT 'testuser', 'test@example.com', '$2a$10$X0cpO3mUrRSSGOZBq/bZd.xEwGnI8EQr8sxaPJiCr8dIDu9w7PO5e'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'testuser'
);

-- Password is 'password123' (BCrypt encoded)