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
    [AllowAnonymous]
    [Route("[controller]")]
    public class SearchController : Controller
    {

        private readonly AppSettings _appSettings;

        public SearchController(IOptions<AppSettings> appSettings)
        {
            _appSettings = appSettings.Value;
        }

        // GET api/values/5
        [HttpGet("{id string}")]
        public IActionResult Get(string id)
        {
            try
            {
                var jsonResult = string.Empty;
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.GetSearchType",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@Input",SqlDbType.NVarChar).Value = id;
                        sqlCommand.Parameters.Add("@Output", SqlDbType.NVarChar, 64);
                        sqlCommand.Parameters["@Output"].Direction = ParameterDirection.Output;

                        sqlConnection.Open();
                        sqlCommand.ExecuteNonQuery();
                        sqlConnection.Close();
                        jsonResult = sqlCommand.Parameters["@Output"].Value.ToString();
                    }
                }
                return Ok(jsonResult);
            }
            catch(Exception ex)
            {
                return BadRequest(error: ex.Message);
            }
        }

        // POST api/values
        [HttpPost]
        public void Post([FromBody]string value)
        {
        }

        // PUT api/values/5
        [HttpPut("{id}")]
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE api/values/5
        [HttpDelete("{id}")]
        public void Delete(int id)
        {
        }
    }
}
