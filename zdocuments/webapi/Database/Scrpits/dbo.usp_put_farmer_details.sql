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
	DECLARE @individual_data NVARCHAR(MAX), @bank_data NVARCHAR(MAX), @social_data NVARCHAR(MAX), @agronomic_data NVARCHAR(MAX), @commerce_data NVARCHAR(MAX), @partner_data NVARCHAR(MAX), @image_data NVARCHAR(MAX)

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
				 , JSON_QUERY(@social_data, '$.Reference')
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
			   , GETDATE()
			   , GETDATE()
			   , @agent_id
			FROM OPENJSON(@agronomic_data)
			WITH (FarmerType VARCHAR(20) '$.FarmerType'
			   , FarmerCategory VARCHAR(20) '$.FarmerCategory'
			   , CropType VARCHAR(200) '$.CropType'
			   , CropTypeOther VARCHAR(20) '$.CropTypeOther'
			   , SoilType VARCHAR(200) '$.SoilType'
			   , SoilTypeOther VARCHAR(50) '$.SoilTypeOther'
			   , WaterSource VARCHAR(200) '$.WaterSource'
			   , LandAcers VARCHAR(200) '$.LandAcers'
			   , SoilTesting BIT '$.SoilTesting'
			   , FarmExp VARCHAR(200) '$.FarmExp'
			   , CropInsurance VARCHAR(200) '$.CropInsurance')


			-- Insert commerce information for the given farmer_id
			INSERT INTO dbo.tbl_farmer_commerce
			SELECT @farmer_id
				 , CASt(JSON_VALUE(@commerce_data, '$.AnnualIncome') AS INT)
				 , CASt(JSON_VALUE(@commerce_data, '$.CropIncome') AS INT)
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

			SELECT @output = @farmer_id, @desc = 'Insert'
		END
		ELSE
		BEGIN --Aadhar already exists, so return the farmer information
			SELECT @individual_data = JSON_QUERY(@desc, '$.result[0]')
			SELECT @farmer_id = JSON_VALUE(@individual_data, '$.Id')

			SELECT @bank_data = (SELECT * FROM dbo.tbl_farmer_bank (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH, ROOT('result'))
			SELECT @bank_data = JSON_QUERY(@bank_data, '$.result[0]')

			SELECT @social_data = (SELECT FacebookID
										, WhatsappID
										, JSON_QUERY(Languages) AS Languages 
										, JSON_QUERY(SourceInformation) AS SourceInfomation 
										, SourceOfInfoOther
										, RationCard
										, PanCard
										, JSON_QUERY(Reference) AS Reference 
									 FROM dbo.tbl_farmer_social (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH, ROOT('result'))
			SELECT @social_data = JSON_QUERY(@social_data, '$.result[0]')

			SELECT @agronomic_data = (SELECT Id, FarmerType, FarmerCategory, CropType, CropTypeOther, SoilType, SoilTypeOther, WaterSource
										   , LandAcers, SoilTesting, FarmExp, CropInsurance
									  FROM dbo.tbl_farmer_agronomic (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH)

			SELECT @commerce_data = (SELECT AnnualIncome
										  , CropIncome
										  , JSON_QUERY(FarmExpenseSource) AS FarmExpenseSource
										  , JSON_QUERY(CreditInformation) AS CreditInformation 
										  , JSON_QUERY(AssetInformation) AS AssetInformation
										  , JSON_QUERY(OsiInformation) AS OsiInformation
									   FROM dbo.tbl_farmer_commerce (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH, ROOT('result'))
			SELECT @commerce_data = JSON_QUERY(@commerce_data, '$.result[0]')

			SELECT @partner_data = (SELECT Id, PartnerName, PartnerPhone, PartnerType 
									  FROM dbo.tbl_farmer_partner (NOLOCK) WHERE farmer_id = @farmer_id FOR JSON PATH)

			SELECT @output = @farmer_id, @desc = '{"agent_id":'+CAST(@agent_id AS NVARCHAR)+',"individual_data":'+ISNULL(@individual_data,'')+',"bank_data":'+ISNULL(@bank_data,'')+',"social_data":'+ISNULL(@social_data,'')+', "agronomic_data":'+@agronomic_data+', "commerce_data":'+ISNULL(@commerce_data,'')+',"partner_data":'+ISNULL(@partner_data,'')+'}'
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
			 , Reference = JSON_QUERY(@social_data, '$.Reference')
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
			   , GETDATE()
			   , GETDATE()
			   , @agent_id
			FROM OPENJSON(@agronomic_data)
			WITH (FarmerType VARCHAR(20) '$.FarmerType'
			   , FarmerCategory VARCHAR(20) '$.FarmerCategory'
			   , CropType VARCHAR(200) '$.CropType'
			   , CropTypeOther VARCHAR(20) '$.CropTypeOther'
			   , SoilType VARCHAR(200) '$.SoilType'
			   , SoilTypeOther VARCHAR(50) '$.SoilTypeOther'
			   , WaterSource VARCHAR(200) '$.WaterSource'
			   , LandAcers VARCHAR(200) '$.LandAcers'
			   , SoilTesting BIT '$.SoilTesting'
			   , FarmExp VARCHAR(200) '$.FarmExp'
			   , CropInsurance VARCHAR(200) '$.CropInsurance')



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

		 SELECT @output = ISNULL(@farmer_id, 0)
	 END

END

/*
DECLARE @json NVARCHAR(MAX) = '{
   "agent_id":1,
   "farmer_id":0,
   "individual_data":{
      "Aadhar":"4284 0047 0799",
      "Surname":"Viriyala",
      "FirstName":"Naresh",
      "LastName":"Kumar",
      "DOB":"07\/01\/1985",
      "Gender":"Male",
      "Address1":"Block 34, Flat No 10-04",
      "Address2":"Malaysian Town Ship Kukatpally",
      "State":"Telangana",
      "District":"Hyderabad",
      "VillageTown":"Tirumalagiri",
      "Pincode":"500072",
      "Cast":"OBC",
      "Phone":"9985265352",
      "Otp":"8965"
   },
   "bank_data":{
      "AccountName":"Naresh",
      "AccountNumber":"123456789",
      "ConfirmAccountNumber":"123456789",
      "AccountType":"Savings",
      "BankName":"icici",
      "BranchName":"jntu",
      "IFSC":"asdf",
      "District":"Hyderabad",
      "State":"Telangana"
   },
   "social_data":{
      "ReferenceInformation":[
         {
            "Id":1,
            "Name":"Naresh",
            "Phone":"9985265382"
         },
         {
            "Id":2,
            "Name":"zfhjk",
            "Phone":"9985265352"
         },
         {
            "Id":3,
            "Name":"ghkk",
            "Phone":"9985265352"
         },
         {
            "Id":4,
            "Name":"xgJzj",
            "Phone":"905232333"
         }
      ],
      "FacebookID":"naresh1253@gmail.com",
      "WhatsappID":"998525352",
      "SocialMediaInformation":[
         "Facebook",
         "Whatsapp"
      ],
      "SourceOfInfoOther":"magazine",
      "SourceInformation":[
         "Paper",
         "TV-Radio",
         "Other"
      ],
      "RationCard":"ratcard",
      "PanCard":"aerpv7542p",
      "Languages":[
         "Hindi",
         "Other"
      ]
   },
   "commerce_data":{
      "AnnualIncome":"50000",
      "CropIncome":"20000",
      "FarmExpenseSource":[
         "Bank"
      ],
      "CreditInformation":[
         {
            "Id":1,
            "Source":"Naresh",
            "Date":"09\/09\/2018",
            "Amount":"50000",
            "Interest":"2",
            "Paid":true,
            "PendingAmount":""
         },
         {
            "Id":2,
            "Source":"Bharat",
            "Date":"02\/09\/2018",
            "Amount":"30000",
            "Interest":"2",
            "Paid":true,
            "PendingAmount":""
         },
         {
            "Id":3,
            "Source":"Laharika",
            "Date":"06\/09\/2018",
            "Amount":"10000",
            "Interest":"2",
            "Paid":false,
            "PendingAmount":"5000"
         }
      ],
      "AssetInformation":[
         {
            "Id":1,
            "AssetName":"House",
            "AssetValue":"20000"
         }
      ],
      "OsiInformation":[
         {
            "Id":1,
            "OsiName":"rent",
            "OsiValue":"500"
         }
      ]
   },
   "partner_data":{
      "PartnerData":[
         {
            "Id":1,
            "PartnerName":"Naresh",
            "PartnerPhone":"9985265352",
            "PartnerType":"Pump operator"
         },
         {
            "Id":2,
            "PartnerName":"testing",
            "PartnerPhone":"7032803804",
            "PartnerType":"Produce buyer"
         }
      ]
   }
}'
DECLARE @output INT
DECLARE @desc NVARCHAR(100)
EXEC dbo.usp_put_farmer_details @json, @output = @output OUTPUT, @desc = @desc OUTPUT
SELECT @output AS output, @desc AS desc1

*/