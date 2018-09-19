DROP PROCEDURE IF EXISTS dbo.usp_put_farmer_details
GO
CREATE PROCEDURE dbo.usp_put_farmer_details(
	   @json NVARCHAR(MAX)
	 , @output INT OUTPUT
	 , @desc NVARCHAR(MAX) OUTPUT)
AS
BEGIN
	SET NOCOUNT ON
	DECLARE @agent_id VARCHAR(10), @farmer_id VARCHAR(10)
	DECLARE @individual_data NVARCHAR(MAX), @bank_data NVARCHAR(MAX), @social_data NVARCHAR(MAX), @agronomic_data NVARCHAR(MAX), @commerce_data NVARCHAR(MAX), @partner_data NVARCHAR(MAX), @image_data VARCHAR(MAX)

	SELECT @agent_id = CASE WHEN [key] = 'agent_id' THEN [value] ELSE @agent_id END
		 , @individual_data = CASE WHEN [key] = 'individual_data' THEN [value] ELSE @individual_data END
		 , @bank_data = CASE WHEN [key] = 'bank_data' THEN [value] ELSE @bank_data END
		 , @social_data = CASE WHEN [key] = 'social_data' THEN [value] ELSE @social_data END
		 , @agronomic_data = CASE WHEN [key] = 'agronomic_data' THEN [value] ELSE @agronomic_data END
		 , @commerce_data = CASE WHEN [key] = 'commerce_data' THEN [value] ELSE @commerce_data END
		 , @partner_data = CASE WHEN [key] = 'partner_data' THEN [value] ELSE @partner_data END
		 , @image_data = CASE WHEN [key] = 'image_data' THEN [value] ELSE @image_data END
	 FROM OPENJSON(@json)

	 SELECT @farmer_id = ISNULL(JSON_VALUE(@individual_data, '$.Id'),0)

	 --if the farmer_id = 0, then its a new entry, else an update to existsing data
	 IF @farmer_id = 0 
	 BEGIN
		--cross check if the aadhar number already exists in the system
		SELECT @desc = (SELECT * FROM dbo.tbl_mstr_farmer (NOLOCK) WHERE Aadhar = JSON_VALUE(@individual_data, '$.Aadhar') FOR JSON PATH, ROOT('result')) 
		
		IF @desc IS NULL-- aadhar doesn't exists so insert the record
		BEGIN
		    -- Insert individual information for the given farmer_id
			INSERT INTO dbo.tbl_mstr_farmer
			SELECT JSON_VALUE(@individual_data, '$.Phone')
				 , REPLACE(JSON_VALUE(@individual_data, '$.Aadhar'),' ','')
				 , JSON_VALUE(@individual_data, '$.FirstName')
				 , JSON_VALUE(@individual_data, '$.LastName')
				 , JSON_VALUE(@individual_data, '$.Surname')
				 , JSON_VALUE(@individual_data, '$.DOB')
				 , JSON_VALUE(@individual_data, '$.Caste')
				 , JSON_VALUE(@individual_data, '$.Gender')
				 , JSON_VALUE(@individual_data, '$.Address1')
				 , JSON_VALUE(@individual_data, '$.Address2')
				 , JSON_VALUE(@individual_data, '$.State')
				 , JSON_VALUE(@individual_data, '$.District')
				 , JSON_VALUE(@individual_data, '$.VillageTown')
				 , JSON_VALUE(@individual_data, '$.Pincode')
				 , GETDATE()
				 , GETDATE()
				 , @agent_id
		    SELECT @farmer_id = @@IDENTITY

			-- Insert bank information for the given farmer_id
			INSERT INTO dbo.tbl_farmer_bank
			SELECT @farmer_id
				 , JSON_VALUE(@bank_data, '$.AccountName')
				 , JSON_VALUE(@bank_data, '$.AccountNumber')
				 , JSON_VALUE(@bank_data, '$.ConfirmAccountNumber')
				 , JSON_VALUE(@bank_data, '$.AccountType')
				 , JSON_VALUE(@bank_data, '$.BankName')
				 , JSON_VALUE(@bank_data, '$.BranchName')
				 , JSON_VALUE(@bank_data, '$.IFSC')
				 , JSON_VALUE(@bank_data, '$.State')
				 , JSON_VALUE(@bank_data, '$.District')
				 , GETDATE()
				 , GETDATE()
				 , @agent_id

			-- Insert social information for the given farmer_id
			INSERT INTO dbo.tbl_farmer_social
			SELECT @farmer_id
				 , JSON_QUERY(@social_data, '$.SocialMediaInformation')
				 , JSON_VALUE(@social_data, '$.FacebookID')
				 , JSON_VALUE(@social_data, '$.WhatsappID')
				 , JSON_QUERY(@social_data, '$.Languages')
				 , JSON_QUERY(@social_data, '$.SourceInformation')
				 , JSON_VALUE(@social_data, '$.SourceOfInfoOther')
				 , JSON_VALUE(@social_data, '$.RationCard')
				 , JSON_VALUE(@social_data, '$.PanCard')
				 , JSON_QUERY(@social_data, '$.ReferenceInformation')
				 , GETDATE()
				 , GETDATE()
				 , @agent_id


			-- Insert agronomic information for the given farmer_id
			INSERT INTO dbo.tbl_farmer_agronomic
			SELECT @farmer_id
			   , FarmerType
			   , FarmerCategory 
			   , CropType
			   , CropTypeOther
			   , SoilType
			   , SoilTypeOther
			   , WaterSource
			   , LandAcers
			   , SoilTesting
			   , FarmExp
			   , CropInsurance
			   , CropHistory
			   , GETDATE()
			   , GETDATE()
			   , @agent_id
			FROM OPENJSON(@agronomic_data)
			WITH (FarmerType VARCHAR(20) '$.FarmerType'
			   , FarmerCategory VARCHAR(20) '$.FarmerCategory'
			   , CropType NVARCHAR(MAX) AS JSON
			   , CropTypeOther VARCHAR(20) '$.CropTypeOther'
			   , SoilType VARCHAR(200) '$.SoilType'
			   , SoilTypeOther VARCHAR(50) '$.SoilTypeOther'
			   , WaterSource VARCHAR(200) '$.WaterSource'
			   , LandAcers VARCHAR(200) '$.LandAcers'
			   , SoilTesting BIT '$.SoilTesting'
			   , FarmExp VARCHAR(200) '$.FarmExp'
			   , CropInsurance VARCHAR(200) '$.CropInsurance'
			   , CropHistory NVARCHAR(MAX) AS JSON)


			-- Insert commerce information for the given farmer_id
			INSERT INTO dbo.tbl_farmer_commerce
			SELECT @farmer_id
				 , CAST(JSON_VALUE(@commerce_data, '$.AnnualIncome') AS INT)
				 , CAST(JSON_VALUE(@commerce_data, '$.CropIncome') AS INT)
				 , JSON_QUERY(@commerce_data, '$.FarmExpenseSource')
				 , JSON_QUERY(@commerce_data, '$.CreditInformation')
				 , JSON_QUERY(@commerce_data, '$.AssetInformation')
				 , JSON_QUERY(@commerce_data, '$.OsiInformation')
				 , GETDATE()
				 , GETDATE()
				 , @agent_id

			-- Insert partner information for the given farmer_id
			INSERT INTO dbo.tbl_farmer_partner
			SELECT @farmer_id
				 , PartnerName
				 , PartnerPhone
				 , PartnerType
				 , GETDATE()
				 , GETDATE()
				 , @agent_id
			 FROM OPENJSON(@partner_data)
			 WITH (PartnerName VARCHAR(20) '$.PartnerName'
			    , PartnerPhone VARCHAR(20) '$.PartnerPhone'
			    , PartnerType VARCHAR(200) '$.PartnerType')


			-- Insert image information for the given farmer_id
			INSERT INTO dbo.tbl_farmer_images
			SELECT @farmer_id
				 , JSON_QUERY(@image_data, '$.Farmer')
				 , JSON_VALUE(@image_data, '$.Aadharcard')
				 , JSON_VALUE(@image_data, '$.Bankbook')
				 , JSON_QUERY(@image_data, '$.Rationcard')
				 , JSON_QUERY(@image_data, '$.Pancard')
				 , JSON_VALUE(@image_data, '$.Additional')
				 , GETDATE()
				 , GETDATE()
				 , @agent_id

			SELECT @output = @farmer_id, @desc = 'Insert'
		END
		ELSE
		BEGIN --Aadhar already exists, so return the farmer information
			SELECT @individual_data = JSON_QUERY(@desc, '$.result[0]')
			SELECT @farmer_id = JSON_VALUE(@individual_data, '$.Id')

			DECLARE @status BIT
			DECLARE @input NVARCHAR(100) = '{"Aadhar":"'+JSON_VALUE(@individual_data, '$.Aadhar')+'", "Phone":""}'
			EXEC dbo.usp_get_farmer_details @input, @status = @status OUTPUT, @output = @desc OUTPUT

			--SELECT @bank_data = (SELECT * FROM dbo.tbl_farmer_bank (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH, ROOT('result'))
			--SELECT @bank_data = JSON_QUERY(@bank_data, '$.result[0]')

			--SELECT @social_data = (SELECT FacebookID
			--							, WhatsappID
			--							, JSON_QUERY(SocialMediaInformation) AS SocialMediaInformation
			--							, JSON_QUERY(Languages) AS Languages 
			--							, JSON_QUERY(SourceInformation) AS SourceInformation 
			--							, SourceOfInfoOther
			--							, RationCard
			--							, PanCard
			--							, JSON_QUERY(ReferenceInformation) AS ReferenceInformation 
			--						 FROM dbo.tbl_farmer_social (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH, ROOT('result'))
			--SELECT @social_data = JSON_QUERY(@social_data, '$.result[0]')

			--SELECT @agronomic_data = (SELECT Id, FarmerType, FarmerCategory, JSON_QUERY(CropType) AS CropType, CropTypeOther, SoilType, SoilTypeOther, WaterSource
			--							   , LandAcers, SoilTesting, FarmExp, CropInsurance
			--						  FROM dbo.tbl_farmer_agronomic (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH)

			--SELECT @commerce_data = (SELECT AnnualIncome
			--							  , CropIncome
			--							  , JSON_QUERY(FarmExpenseSource) AS FarmExpenseSource
			--							  , JSON_QUERY(CreditInformation) AS CreditInformation 
			--							  , JSON_QUERY(AssetInformation) AS AssetInformation
			--							  , JSON_QUERY(OsiInformation) AS OsiInformation
			--						   FROM dbo.tbl_farmer_commerce (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH, ROOT('result'))
			--SELECT @commerce_data = JSON_QUERY(@commerce_data, '$.result[0]')

			--SELECT @partner_data = (SELECT Id, PartnerName, PartnerPhone, PartnerType 
			--						  FROM dbo.tbl_farmer_partner (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH)

			--SELECT @image_data = (SELECT JSON_QUERY(Farmer) AS Farmer
			--							  , JSON_QUERY(Aadharcard) AS Aadharcard 
			--							  , JSON_QUERY(Bankbook) AS Bankbook
			--							  , JSON_QUERY(Rationcard) AS Rationcard
			--							  , JSON_QUERY(Pancard) AS Pancard
			--							  , JSON_QUERY(Additional) AS Additional
			--						   FROM dbo.tbl_farmer_images (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH, ROOT('result'))
			--SELECT @image_data = JSON_QUERY(@image_data, '$.result[0]')

			--SELECT @output = @farmer_id, @desc = '{"agent_id":'+CAST(@agent_id AS NVARCHAR)+',"individual_data":'+ISNULL(@individual_data,'')+',"bank_data":'+ISNULL(@bank_data,'')+',"social_data":'+ISNULL(@social_data,'')+', "agronomic_data":'+@agronomic_data+', "commerce_data":'+ISNULL(@commerce_data,'')+',"partner_data":'+ISNULL(@partner_data,'')+', "image_data":'+ISNULL(@image_data, '')+'}'
			
			SELECT @output = @farmer_id
			RETURN
		END
	 END
	 ELSE --Update existing data
	 BEGIN 
		-- Update individual information for the given farmer_id
		UPDATE dbo.tbl_mstr_farmer 
		   SET Phone = JSON_VALUE(@individual_data, '$.Phone')
		     , FirstName = JSON_VALUE(@individual_data, '$.FirstName')
			 , LastName = JSON_VALUE(@individual_data, '$.LastName')
			 , Surname = JSON_VALUE(@individual_data, '$.Surname')
			 , DOB = JSON_VALUE(@individual_data, '$.DOB')
			 , Caste = JSON_VALUE(@individual_data, '$.Caste')
			 , Gender = JSON_VALUE(@individual_data, '$.Gender')
			 , Address1 = JSON_VALUE(@individual_data, '$.Address1')
			 , Address2 = JSON_VALUE(@individual_data, '$.Address2')
			 , State = JSON_VALUE(@individual_data, '$.State')
			 , District = JSON_VALUE(@individual_data, '$.District')
			 , VillageTown = JSON_VALUE(@individual_data, '$.VillageTown')
			 , Pincode = JSON_VALUE(@individual_data, '$.Pincode')
			 , LastModified = GETDATE()
			 , LastModifiedBy = @agent_id
		 WHERE Id = @farmer_id

		 SELECT  @desc =  CASE WHEN @@ROWCOUNT > 0 THEN 'Update' ELSE 'Update fail, farmer does not exists' END
		
		--Update bank information for the given farmer_id
		UPDATE dbo.tbl_farmer_bank
		   SET AccountName = JSON_VALUE(@bank_data, '$.AccountName')
			 , AccountNumber = JSON_VALUE(@bank_data, '$.AccountNumber')
			 , ConfirmAccountNumber = JSON_VALUE(@bank_data, '$.ConfirmAccountNumber')
			 , AccountType = JSON_VALUE(@bank_data, '$.AccountType')
			 , BankName = JSON_VALUE(@bank_data, '$.BankName')
			 , BranchName = JSON_VALUE(@bank_data, '$.BranchName')
			 , IFSC = JSON_VALUE(@bank_data, '$.IFSC')
			 , State = JSON_VALUE(@bank_data, '$.State')
			 , District = JSON_VALUE(@bank_data, '$.District')
			 , LastModified = GETDATE()
			 , LastModifiedBy = @agent_id
		 WHERE farmer_id = @farmer_id

		--Update social information for the given farmer_id
		UPDATE dbo.tbl_farmer_social
		   SET SocialMediaInformation = JSON_QUERY(@social_data, '$.SocialMediaInformation')
			 , FacebookID = JSON_VALUE(@social_data, '$.FacebookID')
		     , WhatsappID = JSON_VALUE(@social_data, '$.WhatsappID')
			 , Languages = JSON_QUERY(@social_data, '$.Languages')
			 , SourceInformation = JSON_QUERY(@social_data, '$.SourceInformation')
			 , SourceOfInfoOther = JSON_VALUE(@social_data, '$.SourceOfInfoOther')
			 , RationCard = JSON_VALUE(@social_data, '$.RationCard')
			 , PanCard = JSON_VALUE(@social_data, '$.PanCard')
			 , ReferenceInformation = JSON_QUERY(@social_data, '$.ReferenceInformation')
			 , LastModified = GETDATE()
			 , LastModifiedBy = @agent_id
		 WHERE farmer_id = @farmer_id

		 --Update agronomic information for the given farmer_id
		 DELETE FROM dbo.tbl_farmer_agronomic WHERE farmer_id = @farmer_id
		 INSERT INTO dbo.tbl_farmer_agronomic
		 SELECT  @farmer_id
			   , FarmerType
			   , FarmerCategory 
			   , CropType
			   , CropTypeOther
			   , SoilType
			   , SoilTypeOther
			   , WaterSource
			   , LandAcers
			   , SoilTesting
			   , FarmExp
			   , CropInsurance
			   , CropHistory
			   , GETDATE()
			   , GETDATE()
			   , @agent_id
			FROM OPENJSON(@agronomic_data)
			WITH (FarmerType VARCHAR(20) '$.FarmerType'
			   , FarmerCategory VARCHAR(20) '$.FarmerCategory'
			   , CropType NVARCHAR(MAX) AS JSON
			   , CropTypeOther VARCHAR(20) '$.CropTypeOther'
			   , SoilType VARCHAR(200) '$.SoilType'
			   , SoilTypeOther VARCHAR(50) '$.SoilTypeOther'
			   , WaterSource VARCHAR(200) '$.WaterSource'
			   , LandAcers VARCHAR(200) '$.LandAcers'
			   , SoilTesting BIT '$.SoilTesting'
			   , FarmExp VARCHAR(200) '$.FarmExp'
			   , CropInsurance VARCHAR(200) '$.CropInsurance'
			   , CropHistory NVARCHAR(MAX) AS JSON)



		--Update commerce information for the given farmer_id
		UPDATE dbo.tbl_farmer_commerce
		   SET AnnualIncome = CAST(JSON_VALUE(@commerce_data, '$.AnnualIncome') AS INT)
			 , CropIncome = CAST(JSON_VALUE(@commerce_data, '$.CropIncome') AS INT)
			 , FarmExpenseSource = JSON_QUERY(@commerce_data, '$.FarmExpenseSource')
			 , CreditInformation = JSON_QUERY(@commerce_data, '$.CreditInformation')
			 , AssetInformation = JSON_QUERY(@commerce_data, '$.AssetInformation')
			 , OsiInformation = JSON_QUERY(@commerce_data, '$.OsiInformation')
			 , LastModified = GETDATE()
			 , LastModifiedBy = @agent_id
		 WHERE farmer_id = @farmer_id

		--Update partner information for the given farmer_id
		DELETE FROM dbo.tbl_farmer_partner WHERE farmer_id = @farmer_id

		INSERT INTO dbo.tbl_farmer_partner
			SELECT @farmer_id
				 , PartnerName
				 , PartnerPhone
				 , PartnerType
				 , GETDATE()
				 , GETDATE()
				 , @agent_id
			 FROM OPENJSON(@partner_data)
			 WITH (PartnerName VARCHAR(20) '$.PartnerName'
			    , PartnerPhone VARCHAR(20) '$.PartnerPhone'
			    , PartnerType VARCHAR(200) '$.PartnerType')


		--Update images information for the given farmer_id
		UPDATE dbo.tbl_farmer_images
		   SET Farmer = JSON_QUERY(@image_data, '$.Farmer')
			 , Aadharcard = JSON_QUERY(@image_data, '$.Aadharcard')
			 , Bankbook = JSON_QUERY(@image_data, '$.Bankbook')
			 , Rationcard = JSON_QUERY(@image_data, '$.Rationcard')
			 , Pancard = JSON_QUERY(@image_data, '$.Pancard')
			 , Additional = JSON_QUERY(@image_data, '$.Additional')
			 , LastModified = GETDATE()
			 , LastModifiedBy = @agent_id
		 WHERE farmer_id = @farmer_id

		 SELECT @output = ISNULL(@farmer_id, 0)
	 END

END
