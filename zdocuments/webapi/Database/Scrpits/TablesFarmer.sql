DROP TABLE IF EXISTS dbo.tbl_mstr_farmer; 
GO
CREATE TABLE dbo.tbl_mstr_farmer (
	   Id INT IDENTITY(1,1) PRIMARY KEY
	 , Phone VARCHAR(20)
	 , Aadhar VARCHAR(15)
	 , FirstName VARCHAR(100)
	 , LastName VARCHAR(100)
	 , Surname VARCHAR(100)
	 , Caste VARCHAR(10) -- 1 General, 2 OBC, 3	Other, 4 SC,5 ST
	 , Gender VARCHAR(10) --1 Male, 0 Female
	 , Address1 NVARCHAR(200)
	 , Address2 NVARCHAR(200)
	 , State VARCHAR(100)
	 , District VARCHAR(100)
	 , VillageTown VARCHAR(100)
	 , Pincode VARCHAR(10)
	 , CreatedDate DATETIME DEFAULT(GETDATE())
	 , LastModified DATETIME
	 , LastModifiedBy INT)
GO
DROP TABLE IF EXISTS dbo.tbl_farmer_bank; 
GO
CREATE TABLE dbo.tbl_farmer_bank (
	   Id INT IDENTITY(1,1) PRIMARY KEY
	 , farmer_id INT
	 , AccountName NVARCHAR(200)
	 , AccountNumber NVARCHAR(20)
	 , AccountType VARCHAR(20) -- 1 -Current, 2 - Other, 3 - Savings
	 , BankName NVARCHAR(100)
	 , BranchName NVARCHAR(100)
	 , IFSC NVARCHAR(100)
	 , State NVARCHAR(100)
	 , District NVARCHAR(100)
	 , CreatedDate DATETIME DEFAULT(GETDATE())
	 , LastModified DATETIME
	 , LastModifiedBy INT)
GO
DROP TABLE IF EXISTS dbo.tbl_farmer_social; 
GO
CREATE TABLE dbo.tbl_farmer_social (
	   Id INT IDENTITY(1,1) PRIMARY KEY
	 , farmer_id INT
	 , FacebookID NVARCHAR(200)
	 , WhatsappID NVARCHAR(20)
	 , Languages NVARCHAR(200)
	 , SourceInformation NVARCHAR(200)
	 , SourceOfInfoOther NVARCHAR(200)
	 , RationCard NVARCHAR(20)
	 , PanCard NVARCHAR(15)
	 , Reference NVARCHAR(1000)
	 , CreatedDate DATETIME DEFAULT(GETDATE())
	 , LastModified DATETIME
	 , LastModifiedBy INT)
GO
DROP TABLE IF EXISTS dbo.tbl_farmer_commerce; 
GO
CREATE TABLE dbo.tbl_farmer_commerce (
	   Id INT IDENTITY(1,1) PRIMARY KEY
	 , farmer_id INT
	 , AnnualIncome INT
	 , CropIncome INT
	 , FarmExpenseSource NVARCHAR(500)
	 , CreditInformation NVARCHAR(1000)
	 , AssetInformation NVARCHAR(1000)
	 , OsiInformation NVARCHAR(1000)
	 , CreatedDate DATETIME DEFAULT(GETDATE())
	 , LastModified DATETIME
	 , LastModifiedBy INT)
GO
DROP TABLE IF EXISTS dbo.tbl_farmer_partner; 
GO
CREATE TABLE dbo.tbl_farmer_partner (
	   Id INT IDENTITY(1,1) PRIMARY KEY
	 , farmer_id INT
	 , PartnerData NVARCHAR(MAX)
	 , CreatedDate DATETIME DEFAULT(GETDATE())
	 , LastModified DATETIME
	 , LastModifiedBy INT)
GO
DROP TABLE IF EXISTS dbo.tbl_farmer_images; 
GO
CREATE TABLE dbo.tbl_farmer_images (
	   Id INT IDENTITY(1,1) PRIMARY KEY
	 , farmer_id INT
	 , ImageType NVARCHAR(50)
	 , ImageData NVARCHAR(MAX)
	 , CreatedDate DATETIME DEFAULT(GETDATE())
	 , LastModified DATETIME
	 , LastModifiedBy INT)
GO
DROP TABLE IF EXISTS dbo.tbl_phone_validation; 
GO
CREATE TABLE dbo.tbl_phone_validation (
	   Id INT IDENTITY(1,1) PRIMARY KEY
	 , phone NVARCHAR(20)
	 , Otp NVARCHAR(50)
	 , CreatedDate DATETIME DEFAULT(GETDATE())
	 , LastModifiedBy INT)