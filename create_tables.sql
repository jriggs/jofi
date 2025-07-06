-- Stock Tickers Table Creation Script
-- Database: Stock Management System
-- Table: stock_tickers

-- Drop table if it exists (for recreation purposes)
DROP TABLE IF EXISTS stock_tickers;

-- Create stock_tickers table
CREATE TABLE stock_tickers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    symbol VARCHAR(10) NOT NULL UNIQUE,
    description TEXT,
    aggressive_level TINYINT NOT NULL CHECK (aggressive_level BETWEEN 1 AND 10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_symbol ON stock_tickers(symbol);
CREATE INDEX idx_name ON stock_tickers(name);
CREATE INDEX idx_aggressive_level ON stock_tickers(aggressive_level);

-- Add comments to the table and columns
ALTER TABLE stock_tickers COMMENT = 'Table storing stock ticker information with risk assessment';
ALTER TABLE stock_tickers MODIFY COLUMN id INT AUTO_INCREMENT COMMENT 'Auto-incrementing primary key';
ALTER TABLE stock_tickers MODIFY COLUMN name VARCHAR(255) NOT NULL COMMENT 'Company or stock name';
ALTER TABLE stock_tickers MODIFY COLUMN symbol VARCHAR(10) NOT NULL UNIQUE COMMENT 'Stock ticker symbol (e.g., AAPL, GOOGL)';
ALTER TABLE stock_tickers MODIFY COLUMN description TEXT COMMENT 'Detailed description of the stock/company';
ALTER TABLE stock_tickers MODIFY COLUMN aggressive_level TINYINT NOT NULL COMMENT 'Risk/aggressiveness level (1-10, where 1 is conservative, 10 is highly aggressive)';
ALTER TABLE stock_tickers MODIFY COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp';
ALTER TABLE stock_tickers MODIFY COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Record last update timestamp';

-- Sample data insertion (optional)
INSERT INTO stock_tickers (name, symbol, description, aggressive_level) VALUES
('Apple Inc.', 'AAPL', 'Technology company specializing in consumer electronics and software', 5),
('Microsoft Corporation', 'MSFT', 'Technology corporation focused on software, cloud computing, and productivity', 4),
('Tesla Inc.', 'TSLA', 'Electric vehicle and clean energy company', 8),
('Amazon.com Inc.', 'AMZN', 'E-commerce and cloud computing giant', 6),
('Berkshire Hathaway Inc.', 'BRK.A', 'Multinational conglomerate holding company', 2);

-- Verify table creation
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    EXTRA
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'stock_tickers' 
ORDER BY ORDINAL_POSITION;