DROP PROCEDURE IF EXISTS dbo.usp_put_farmexpensereport
GO
CREATE PROCEDURE dbo.usp_put_farmexpensereport(
	   @json VARCHAR(MAX)
	 , @output INT OUTPUT)
AS
BEGIN
	DECLARE @farm_id INT, @farm_expense_id INT, @agent_id INT
	DECLARE @crop_type VARCHAR(100), @land_acers VARCHAR(10), @profit_share VARCHAR(10), @expense_data NVARCHAR(MAX)
	DECLARE @start_date DATE, @end_date DATE

	SET @output = 0

	SELECT @agent_id = CASE WHEN [key] = 'agent_id' THEN [value] ELSE @agent_id END
		 , @farm_id = CASE WHEN [key] = 'farm_id' THEN [value] ELSE @farm_id END
		 , @farm_expense_id = CASE WHEN [key] = 'farm_expense_id' THEN [value] ELSE @farm_expense_id END
		 , @crop_type = CASE WHEN [key] = 'crop_type' THEN [value] ELSE @crop_type END
		 , @land_acers = CASE WHEN [key] = 'land_acers' THEN [value] ELSE @land_acers END
		 , @profit_share = CASE WHEN [key] = 'profit_share' THEN [value] ELSE @profit_share END
		 , @start_date = CASE WHEN [key] = 'start_date' THEN [value] ELSE @start_date END
		 , @end_date = CASE WHEN [key] = 'end_date' THEN [value] ELSE @end_date END
		 , @expense_data = CASE WHEN [key] = 'expenses' THEN [value] ELSE @expense_data END
	 FROM OPENJSON(@json)

	MERGE dbo.tbl_expense_header h 
	USING (SELECT @farm_id AS farm_id
				, @farm_expense_id AS Id
				, @crop_type AS CropType
				, @land_acers AS LandAcers
				, @profit_share AS ProfitShare
				, @start_date AS StartDate
				, @end_date AS EndDate) src
	   ON h.farm_id = src.farm_id
	 WHEN MATCHED AND h.Id = src.Id THEN UPDATE 
	  SET h.CropType = src.CropType
		, h.LandAcers = src.LandAcers
		, h.ProfitShare = src.ProfitShare
		, h.StartDate = src.StartDate
		, h.EndDate = src.EndDate
		, h.LastModifiedBy = @agent_id
		, h.LastModified = GETDATE()
	 WHEN NOT MATCHED BY TARGET AND src.Id = 0
  	 THEN INSERT(farm_id, CropType, LandAcers, ProfitShare, StartDate, EndDate, IsActive, LastModifiedBy, LastModified)
   VALUES (src.farm_id, src.CropType, src.LandAcers, src.ProfitShare, StartDate, EndDate, 1, @agent_id, GETDATE());

   SELECT @farm_expense_id = CASE WHEN @farm_expense_id = 0 THEN @@IDENTITY ELSE @farm_expense_id END

	MERGE dbo.tbl_expense e
	USING (SELECT Id AS Id
			    , Decscription AS Decscription
			    , Amount AS Amount
			    , RequestDate AS RequestDate
			    , @agent_id AS agent_id
				, @farm_expense_id AS farm_expense_id
			 FROM OPENJSON(@expense_data)
			 WITH (Id INT '$.Id'
			    , Decscription VARCHAR(100) '$.Decscription'
			    , Amount FLOAT '$.Amount'
			    , RequestDate DATE '$.RequestDate')) src
	   ON e.farm_expense_id = src.farm_expense_id
	  AND e.Id = src.Id
	 WHEN MATCHED THEN UPDATE 
	  SET e.Decscription = src.Decscription
		, e.Amount = src.Amount
		, e.RequestDate = src.RequestDate
		, e.LastModified = GETDATE()
		, e.LastModifiedBy = src.agent_id
	 WHEN NOT MATCHED AND src.Id = 0
	 THEN INSERT(farm_expense_id, Decscription, Amount, RequestDate, LastModifiedBy, LastModified)
   VALUES (@farm_expense_id, src.Decscription, src.Amount, src.RequestDate, @agent_id, GETDATE())
     WHEN NOT MATCHED BY SOURCE AND e.Id > 0 AND e.farm_expense_id = @farm_expense_id
	 THEN DELETE;

	 SET @output = 1
END
/*
EXEC dbo.usp_get_farmexpensereport 1
*/