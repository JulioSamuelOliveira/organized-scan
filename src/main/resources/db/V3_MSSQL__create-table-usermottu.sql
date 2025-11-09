IF OBJECT_ID('dbo.usermottu', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.usermottu (
        id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        username NVARCHAR(100) NOT NULL,
        email NVARCHAR(256) NOT NULL
    );
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'idx_usermottu_username'
      AND object_id = OBJECT_ID('dbo.usermottu')
)
BEGIN
    CREATE INDEX idx_usermottu_username ON dbo.usermottu(username);
END
GO
