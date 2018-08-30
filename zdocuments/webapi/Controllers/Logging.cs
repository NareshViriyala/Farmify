using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using Microsoft.Extensions.Options;
using webapi.Helpers;
using System.Data;
using Newtonsoft.Json;
using System.Data.SqlClient;
using System.Dynamic;

namespace webapi.Controllers
{
    [Authorize]
    [Route("[controller]")]
    public class LoggingController : Controller
    {
        private readonly AppSettings _appSettings;

        public LoggingController(IOptions<AppSettings> appSettings)
        {
            _appSettings = appSettings.Value;
        }

        //[HttpPost("{logsignindata}")]
        [HttpPost]
        [Route("[action]")]
        public IActionResult logSigninDetails([FromBody]SigninData jsonString)
        {
            try
            {
                var signin_id = "";
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.usp_logsignin_details",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@json",SqlDbType.NVarChar, -1).Value = JsonConvert.SerializeObject(jsonString);
                        sqlCommand.Parameters.Add("@output", SqlDbType.Int);
                        sqlCommand.Parameters["@output"].Direction = ParameterDirection.Output;
                        sqlConnection.Open();
                        sqlCommand.ExecuteReader();
                        sqlConnection.Close();
                        signin_id = sqlCommand.Parameters["@output"].Value.ToString();
                    }
                }
                return Ok(new {log_id = Int32.Parse(signin_id)});
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // [HttpPost("{logerrordata}")]
        [HttpPost]
        [Route("[action]")]
        public IActionResult logErrorDetails([FromBody]ErrorDataList jsonString)
        {
            try
            {
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.usp_logerror_details",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@json",SqlDbType.NVarChar, -1).Value = JsonConvert.SerializeObject(jsonString);
                        sqlConnection.Open();
                        sqlCommand.ExecuteReader();
                        sqlConnection.Close();
                    }
                }
                return Ok();
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
    }

    public class SigninData{
        public int user_id {get; set;}
        public string device_id {get; set;}
        public int device_type {get; set;} //1 - Android, 2 - IPhone, 3 - Windows
        public float latitude {get; set;}
        public float longitude {get; set;}
    }

    public class ErrorDataList{
        public List<ErrorData> error_log {get; set;}
    }

    public class ErrorData{
        public int user_id {get; set;}
        public int login_id {get; set;}
        public string code_file {get; set;} //1 - Android, 2 - IPhone, 3 - Windows
        public string method_name {get; set;}
        public string error_desc {get; set;}
    }
}