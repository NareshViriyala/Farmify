DROP PROCEDURE IF EXISTS dbo.usp_get_farmer_details
GO
CREATE PROCEDURE dbo.usp_get_farmer_details(
	   @json NVARCHAR(500)
	 , @output NVARCHAR(MAX) OUTPUT)
AS
BEGIN
	SELECT @output = (SELECT * FROM dbo.tbl_mstr_farmer (NOLOCK) WHERE Aadhar = JSON_VALUE(@json, '$.Aadhar') OR Phone = JSON_VALUE(@json, '$.Phone') FOR JSON PATH, ROOT('result')) 
	
	IF @output IS NOT NULL-- aadhar exists so insert the record
	BEGIN
			DECLARE @farmer_id VARCHAR(10)
			DECLARE @individual_data NVARCHAR(MAX), @bank_data NVARCHAR(MAX), @social_data NVARCHAR(MAX), @commerce_data NVARCHAR(MAX), @partner_data NVARCHAR(MAX), @image_data NVARCHAR(MAX)

			SELECT @individual_data = JSON_QUERY(@output, '$.result[0]')
			SELECT @farmer_id = JSON_VALUE(@individual_data, '$.Id')
			
			SELECT @bank_data = (SELECT * FROM dbo.tbl_farmer_bank (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH, ROOT('result'))
			SELECT @bank_data = JSON_QUERY(@bank_data, '$.result[0]')
			
			SELECT @social_data = (SELECT * FROM dbo.tbl_farmer_social (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH, ROOT('result'))
			SELECT @social_data = JSON_QUERY(@social_data, '$.result[0]')
			
			SELECT @commerce_data = (SELECT * FROM dbo.tbl_farmer_commerce (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH, ROOT('result'))
			SELECT @commerce_data = JSON_QUERY(@commerce_data, '$.result[0]')
			
			SELECT @partner_data = (SELECT * FROM dbo.tbl_farmer_partner (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH, ROOT('result'))
			SELECT @partner_data = JSON_QUERY(@partner_data, '$.result[0]')
			
			SELECT @output ='{"agent_id":0, 
							"individual_data":'+ISNULL(@individual_data,'')+',
							"bank_data":'+ISNULL(@bank_data,'')+',
							"social_data":'+ISNULL(@social_data,'')+',
							"commerce_data":'+ISNULL(@commerce_data,'')+',
							"partner_data":'+ISNULL(@partner_data,'')+'
						   }'
	END
	ELSE
	BEGIN
		DECLARE @Otp INT = LEFT(SUBSTRING (RTRIM(RAND()) + SUBSTRING(RTRIM(RAND()),3,11), 3,11),4)

		MERGE dbo.tbl_phone_validation src
		USING (SELECT JSON_VALUE(@json, '$.Phone') AS Phone, @Otp AS Otp) dest
		   ON src.Phone = dest.Phone
		 WHEN MATCHED THEN
	   UPDATE SET src.Otp = dest.Otp
	     WHEN NOT MATCHED BY TARGET THEN
	   INSERT (Phone, Otp)
	   VALUES (dest.Phone, dest.Otp);

		SET @output = 'Otp sent'
	END
END

/*
DECLARE @json NVARCHAR(500) = '{"Aadhar":"4284 0047 0799", "Phone":"9985265352"}'
DECLARE @desc NVARCHAR(MAX) 
EXEC dbo.usp_get_farmer_details @json, @desc = @desc OUTPUT
SELECT @desc AS Output

Select * FROM tbl_phone_validation
*/