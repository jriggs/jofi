DELETE FROM tickers;

INSERT INTO tickers (symbol, name, description, aggressive_level, created_at, modified) VALUES
('AAPL', 'Apple Inc', 'Consumer electronics giant', 'Medium', NOW(), NOW()),
('TSLA', 'Tesla Inc', 'Electric vehicles and energy', 'High', NOW(), NOW()),
('MSFT', 'Microsoft Corp', 'Software and cloud services', 'Low', NOW(), NOW()),
('GOOGL', 'Alphabet Inc', 'Search engine and technology', 'Medium', NOW(), NOW()),
('AMZN', 'Amazon.com Inc', 'E-commerce and cloud computing', 'High', NOW(), NOW());
