DELETE FROM tickers;

INSERT INTO tickers (symbol, name, description, aggressive_level, created_at, modified) VALUES
-- Low Aggression
('MSFT', 'Microsoft Corp', 'Software and cloud services', 'Low', NOW(), NOW()),
('JNJ', 'Johnson & Johnson', 'Healthcare and pharmaceuticals', 'Low', NOW(), NOW()),
('PG', 'Procter & Gamble', 'Consumer goods conglomerate', 'Low', NOW(), NOW()),

-- Medium Aggression
('AAPL', 'Apple Inc', 'Consumer electronics giant', 'Medium', NOW(), NOW()),
('GOOGL', 'Alphabet Inc', 'Search engine and technology', 'Medium', NOW(), NOW()),
('NVDA', 'NVIDIA Corp', 'Graphics and AI hardware', 'Medium', NOW(), NOW()),

-- High Aggression
('TSLA', 'Tesla Inc', 'Electric vehicles and energy', 'High', NOW(), NOW()),
('NFLX', 'Netflix Inc', 'Streaming entertainment', 'High', NOW(), NOW()),
('AMD', 'Advanced Micro Devices', 'High-performance computing', 'High', NOW(), NOW()),

-- ETF Category
('SPY', 'SPDR S&P 500 ETF', 'Tracks the S&P 500 index', 'ETF', NOW(), NOW()),
('QQQ', 'Invesco QQQ Trust', 'Tracks the Nasdaq-100 index', 'ETF', NOW(), NOW()),
('VTI', 'Vanguard Total Stock Market ETF', 'Broad U.S. equity exposure', 'ETF', NOW(), NOW());
