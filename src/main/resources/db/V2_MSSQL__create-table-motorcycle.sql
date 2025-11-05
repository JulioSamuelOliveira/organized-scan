IF OBJECT_ID('dbo.motorcycle', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.motorcycle (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        plate NVARCHAR(20) NOT NULL,
        model NVARCHAR(100) NOT NULL
    );
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'idx_motorcycle_plate'
      AND object_id = OBJECT_ID('dbo.motorcycle')
)
BEGIN
    CREATE UNIQUE INDEX idx_motorcycle_plate ON dbo.motorcycle(plate);
END
GO
