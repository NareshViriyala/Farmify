DROP PROCEDURE IF EXISTS dbo.usp_get_farms
GO
CREATE PROCEDURE dbo.usp_get_farms(
	   @phone NVARCHAR(50))
AS
BEGIN
	SELECT a.Id
		 , FarmerType
		 , FarmerCategory
		 , JSON_QUERY(CropType) AS CropType
		 , CropTypeOther
		 , SoilType
		 , SoilTypeOther
		 , WaterSource
		 , JSON_QUERY(CropHistory) AS CropHistory
		 , LandAcers
		 , SoilTesting
		 , FarmExp
		 , CropInsurance
		 , NULL AS FarmMap
	  FROM dbo.tbl_mstr_farmer f (NOLOCK)
	  LEFT JOIN dbo.tbl_farmer_agronomic a (NOLOCK)
		ON f.Id = a.farmer_id
	 WHERE f.Phone = @phone
END

/*
EXEC dbo.usp_get_farms '9985265352'
*/