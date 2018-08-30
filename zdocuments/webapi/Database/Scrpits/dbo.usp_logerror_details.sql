DROP PROCEDURE IF EXISTS dbo.usp_logerror_details
GO
CREATE PROCEDURE dbo.usp_logerror_details(@json NVARCHAR(MAX))
AS
BEGIN
	INSERT INTO dbo.tbl_errorlog(UserId, Loginid, CodeFile, MethodName, Errordesc)
	SELECT [user_id]
		 , login_id
		 , code_file
		 , method_name
		 , error_desc
	  FROM OPENJSON(@json, '$.error_log')
	  WITH ([user_id] INT, login_id INT, code_file NVARCHAR(1000), method_name NVARCHAR(1000), error_desc NVARCHAR(1000)) AS ErrorLog
END

/*
DECLARE @json NVARCHAR(4000) = '{"error_log":[{"user_id":1,"login_id":1,"code_file":"myfile.cs","method_name":"methodname","error_desc":"this is a test error 1"},
										     {"user_id":1,"login_id":2,"code_file":"mylog.cs","method_name":"methodname","error_desc":"this is a test error 2"},
											 {"user_id":1,"login_id":1,"code_file":"myfile.cs","method_name":"methodname","error_desc":"this is a test error 3"}]'
SELECT user_id
	 , login_id
	 , code_file
	 , method_name
	 , error_desc
  FROM OPENJSON(@json, '$.error_log')
  WITH (user_id INT, login_id INT, code_file NVARCHAR(1000), method_name NVARCHAR(1000), error_desc NVARCHAR(1000)) AS ErrorLog
*/