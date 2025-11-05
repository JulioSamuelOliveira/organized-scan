IF OBJECT_ID('dbo.portal', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.portal (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        [type] NVARCHAR(50) NOT NULL
    );
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'idx_portal_type'
      AND object_id = OBJECT_ID('dbo.portal')
)
BEGIN
    CREATE INDEX idx_portal_type ON dbo.portal([type]);
END
GO
