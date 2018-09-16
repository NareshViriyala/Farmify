DROP PROCEDURE IF EXISTS dbo.usp_verify_phone_otp
GO
CREATE PROCEDURE dbo.usp_verify_phone_otp(
	   @json NVARCHAR(500)
	 , @output BIT OUTPUT)
AS
BEGIN
	SELECT @output = 0
	IF EXISTS (SELECT 1 FROM dbo.tbl_phone_validation (NOLOCK) WHERE Phone = JSON_VALUE(@json, '$.Phone') AND Otp = JSON_VALUE(@json, '$.Otp'))
	BEGIN
	 SELECT @output = 1
	END
END