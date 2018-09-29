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
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.usp_get_farmer_details",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@json",SqlDbType.NVarChar, 500).Value = JsonConvert.SerializeObject(jsonString);
                        sqlCommand.Parameters.Add("@output", SqlDbType.NVarChar, -1);
                        sqlCommand.Parameters["@output"].Direction = ParameterDirection.Output;
                        sqlCommand.Parameters.Add("@status", SqlDbType.Bit);
                        sqlCommand.Parameters["@status"].Direction = ParameterDirection.Output;
                        sqlConnection.Open();
                        sqlCommand.ExecuteReader();
                        sqlConnection.Close();
                        string output = sqlCommand.Parameters["@output"].Value.ToString();
                        if((bool)sqlCommand.Parameters["@status"].Value)
                        {
                            return Ok(new {
                                status = true,
                                result = JsonConvert.DeserializeObject<FarmerDataItem>(output)
                            });
                        }
                        else
                        {
                            //int res;
                            if(int.TryParse(output, out int res))
                            {
                                new SendOTP(_appSettings).CallOTPAPI(jsonString.Phone, output);
                                output = "Otp sent";
                            }
                            return Ok(new {
                                    status = false,
                                    result = output
                            });
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("{id int}")]
        [Route("[action]")]
        public IActionResult getFarmerDocuments(int id)
        {
            try
            {
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.usp_get_farmer_documents",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@id",SqlDbType.Int).Value = id;
                        sqlCommand.Parameters.Add("@output", SqlDbType.NVarChar, -1);
                        sqlCommand.Parameters["@output"].Direction = ParameterDirection.Output;
                        sqlConnection.Open();
                        sqlCommand.ExecuteReader();
                        sqlConnection.Close();
                        string output = sqlCommand.Parameters["@output"].Value.ToString();
                        return Ok(new {
                            result = JsonConvert.DeserializeObject<FarmerDocuments>(sqlCommand.Parameters["@output"].Value.ToString())
                        });
                    }
                }
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
                JObject output = new JObject();
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
                        //output = (bool)sqlCommand.Parameters["@output"].Value;
                        output.Add("output", (bool)sqlCommand.Parameters["@output"].Value);
                    }
                }
                return Ok(output);
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

    public class FarmerDocuments{
        public JObject documents {get; set;}
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
        public JArray agronomic_data {get; set;} 
        public JObject commerce_data {get; set;} 
        public JArray partner_data {get; set;}
        public JObject image_data {get; set;}
    }
}