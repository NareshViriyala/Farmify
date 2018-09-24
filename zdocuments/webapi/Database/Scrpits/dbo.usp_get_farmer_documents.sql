DROP PROCEDURE IF EXISTS dbo.usp_get_farmer_documents
GO
CREATE PROCEDURE dbo.usp_get_farmer_documents(
	   @id INT
	 , @output NVARCHAR(MAX) OUTPUT)
AS
BEGIN
   SELECT @output = (SELECT JSON_QUERY(Farmer) AS Farmer
		, JSON_QUERY(Aadharcard) AS Aadharcard 
		, JSON_QUERY(Bankbook) AS Bankbook
		, JSON_QUERY(Rationcard) AS Rationcard
		, JSON_QUERY(Pancard) AS Pancard
		, JSON_QUERY(Additional) AS Additional
	 FROM dbo.tbl_farmer_images (NOLOCK) WHERE farmer_id = @id FOR JSON PATH, ROOT('result'))

   SELECT @output = JSON_QUERY(@output, '$.result[0]')

   SELECT @output ='{"documents":'+ISNULL(@output,'')+'}'
END

/*
DECLARE @output NVARCHAR(MAX)
EXEC dbo.usp_get_farmer_documents 1, @output = @output OUTPUT
SELECT @output
*/