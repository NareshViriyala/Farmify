DROP PROCEDURE IF EXISTS dbo.usp_get_farmexpensereport
GO
CREATE PROCEDURE dbo.usp_get_farmexpensereport(@farm_id INT)
AS
BEGIN
	SELECT ISNULL(h.LastModifiedBy, 0) AS agent_id
		 , h.farm_id
		 , h.Id AS farm_expense_id
		 , h.CropType
		 , h.LandAcers
		 , h.ProfitShare
		 , h.StartDate
		 , h.EndDate
		 , (SELECT e.Id, e.Decscription, e.Amount, e.RequestDate
			  FROM dbo.tbl_expense e (NOLOCK)
			 WHERE e.farm_expense_id = h.Id
			   FOR JSON PATH) AS Expenses
	  FROM dbo.tbl_expense_header h 
	 WHERE h.farm_id = @farm_id
	   AND h.IsActive = 1
END
/*
EXEC dbo.usp_get_farmexpensereport 11
*/