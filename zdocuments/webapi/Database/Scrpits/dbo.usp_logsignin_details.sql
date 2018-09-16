DROP PROCEDURE IF EXISTS dbo.usp_logsignin_details
GO
CREATE PROCEDURE dbo.usp_logsignin_details(@json NVARCHAR(4000), @output INT OUTPUT)
AS
BEGIN

	INSERT INTO dbo.tbl_login_history(UserId, DeviceID, DeviceType, DeviceVersion, AppVersion, Latitude, Longitude)
	SELECT JSON_VALUE(@json, '$.Id')
	     , JSON_VALUE(@json, '$.device_id')
		 , JSON_VALUE(@json, '$.device_type')
		 , JSON_VALUE(@json, '$.device_version')
		 , JSON_VALUE(@json, '$.app_version')
		 , JSON_VALUE(@json, '$.latitude')
		 , JSON_VALUE(@json, '$.longitude')

	SET @output = @@IDENTITY
END

/*
DECLARE @json NVARCHAR(4000) = '{"user_id":1,"device_id":"abcde","device_type":1,"latitude":123.345678,"longitude":567.123456}'
--SELECT JSON_VALUE(@json, '$.user_id'), JSON_VALUE(@json, '$.device_id'), JSON_VALUE(@json, '$.device_type'), JSON_VALUE(@json, '$.latitude'), JSON_VALUE(@json, '$.longitude')
EXEC dbo.usp_logsignin_details @json


*/