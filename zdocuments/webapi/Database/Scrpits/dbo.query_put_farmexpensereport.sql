DECLARE @expense_data NVARCHAR(MAX) = '{"agent_id":"1", "farm_id":"11","farm_expense_id":"0","crop_type":"Chilli","land_acers":"2","profit_share":"9",
														 "expenses":[{"Id":"0","Decscription":"seeds","Amount":"200","RequestDate":"2018-10-24"}
																	,{"Id":"0","Decscription":"pesticides","Amount":"500","RequestDate":"2018-11-14"}
																	,{"Id":"0","Decscription":"insecticide","Amount":"5001","RequestDate":"2018-11-14"}]}'

SET @expense_data  = '{
   "agent_id":1,
   "farm_id":12,
   "farm_expense_id":0,
   "crop_type":"Corn",
   "land_acers":2,
   "profit_share":18,
   "start_date":"2018-10-27",
   "end_date":"2018-12-31",
   "expenses":[
      {
         "Id":0,
         "Decscription":"ploughing",
         "Amount":2000,
         "RequestDate":"2018-10-16"
      },
      {
         "Id":0,
         "Decscription":"seeds",
         "Amount":5000,
         "RequestDate":"2018-10-25"
      },
      {
         "Id":0,
         "Decscription":"labour",
         "Amount":1200,
         "RequestDate":"2018-11-07"
      },
      {
         "Id":0,
         "Decscription":"pesticides",
         "Amount":30000,
         "RequestDate":"2018-12-13"
      },
      {
         "Id":0,
         "Decscription":"harvesting",
         "Amount":5000,
         "RequestDate":"2018-12-20"
      },
      {
         "Id":0,
         "Decscription":"transportation",
         "Amount":2000,
         "RequestDate":"2018-12-29"
      }
   ]
}'
DECLARE @output INT
EXEC dbo.usp_put_farmexpensereport @expense_data, @output = @output OUTPUT
SELECT @output

--SELECT  @expense_data = CASE WHEN [key] = 'expenses' THEN [value] ELSE @expense_data END
--	 FROM OPENJSON(@expense_data)

--SELECT Id AS Id
--			    , Decscription AS Decscription
--			    , Amount AS Amount
--			    , RequestDate AS RequestDate
--			    , 1 AS agent_id
--				, 1 AS farm_expense_id
--			 FROM OPENJSON(@expense_data)
--			 WITH (Id INT '$.Id'
--			    , Decscription VARCHAR(100) '$.Decscription'
--			    , Amount FLOAT '$.Amount'
--			    , RequestDate DATE '$.RequestDate')

SELECT * FROM dbo.tbl_expense_header
SELECT * FROM dbo.tbl_expense

