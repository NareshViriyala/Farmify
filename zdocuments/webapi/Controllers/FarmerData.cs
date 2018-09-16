using System;
using System.Data;
using System.Data.SqlClient;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using webapi.Helpers;

namespace webapi.Controllers
{
    [Authorize]
    [Route("[controller]")]
    public class FarmerData : Controller
    {
        private readonly AppSettings _appSettings;

        public FarmerData(IOptions<AppSettings> appSettings)
        {
            _appSettings = appSettings.Value;
        }

        [HttpPost]
        [Route("[action]")]
        public IActionResult logFarmerDetails([FromBody]FarmerDataItem jsonString)
        {
            try
            {
                int output;
                string description;
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.usp_put_farmer_details",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@json",SqlDbType.NVarChar, -1).Value = JsonConvert.SerializeObject(jsonString);
                        sqlCommand.Parameters.Add("@output", SqlDbType.Int);
                        sqlCommand.Parameters["@output"].Direction = ParameterDirection.Output;
                        sqlCommand.Parameters.Add("@desc", SqlDbType.NVarChar, -1);
                        sqlCommand.Parameters["@desc"].Direction = ParameterDirection.Output;
                        sqlConnection.Open();
                        sqlCommand.ExecuteReader();
                        sqlConnection.Close();

                        output = Int32.Parse(sqlCommand.Parameters["@output"].Value.ToString());
                        description = sqlCommand.Parameters["@desc"].Value.ToString();
                    }
                }
                return Ok(new {
                    output = output,
                    // description = JsonConvert.DeserializeObject<FarmerDataItem>(description)
                    description = description
                });
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPost]
        [Route("[action]")]
        public IActionResult verifyAadhar([FromBody]AadharInfo jsonString)
        {
            try
            {
                string output = "";
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.usp_get_farmer_details",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@json",SqlDbType.NVarChar, 500).Value = JsonConvert.SerializeObject(jsonString);
                        sqlCommand.Parameters.Add("@output", SqlDbType.NVarChar, -1);
                        sqlCommand.Parameters["@output"].Direction = ParameterDirection.Output;
                        sqlConnection.Open();
                        sqlCommand.ExecuteReader();
                        sqlConnection.Close();
                        output = sqlCommand.Parameters["@output"].Value.ToString();
                    }
                }
                return Ok(new {output = output});
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPost]
        [Route("[action]")]
        public IActionResult validateOTP([FromBody]PhoneOtp jsonString)
        {
            try
            {
                bool output = false;
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.usp_verify_phone_otp",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@json",SqlDbType.NVarChar, 500).Value = JsonConvert.SerializeObject(jsonString);
                        sqlCommand.Parameters.Add("@output", SqlDbType.Bit);
                        sqlCommand.Parameters["@output"].Direction = ParameterDirection.Output;
                        sqlConnection.Open();
                        sqlCommand.ExecuteReader();
                        sqlConnection.Close();
                        output = (bool)sqlCommand.Parameters["@output"].Value;
                    }
                }
                return Ok(new {output = output});
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
    }

    public class AadharInfo{
        public string Aadhar {get; set;}

        public string Phone {get; set;}
    }

    public class PhoneOtp{
        public string Phone {get; set;}

        public string Otp {get; set;}
    }

    public class FarmerDataItem{
        public int agent_id {get; set;}
        public JObject individual_data {get; set;}
        public JObject bank_data {get; set;} 
        public JObject social_data {get; set;} 
        public JObject commerce_data {get; set;} 
        public JObject partner_data {get; set;}
        public JObject image_data {get; set;}
    }
}