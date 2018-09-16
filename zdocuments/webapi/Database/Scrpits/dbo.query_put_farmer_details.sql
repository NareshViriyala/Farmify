DECLARE @json NVARCHAR(MAX) = '{
   "agent_id":1,
   "individual_data":{
   	  "Id":1,
      "Aadhar":"428400470799",
      "Surname":"Akula",
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
      "Caste":"OBC",
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
DECLARE @desc NVARCHAR(MAX)
EXEC dbo.usp_put_farmer_details @json, @output = @output OUTPUT, @desc = @desc OUTPUT
SELECT @output AS output, @desc AS desc1

SELECT * FROM tbl_mstr_farmer
SELECT * FROM tbl_farmer_bank
SELECT * FROM tbl_farmer_social
SELECT * FROM tbl_farmer_commerce
SELECT * FROM tbl_farmer_partner






