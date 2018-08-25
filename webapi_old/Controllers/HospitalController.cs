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
    public class HospitalController : Controller
    {
        private readonly AppSettings _appSettings;

        public HospitalController(IOptions<AppSettings> appSettings)
        {
            _appSettings = appSettings.Value;
        }

        [HttpGet("{id string, type string}")]
        public IActionResult Get(string id, string type )
        {
            try
            {
                var jsonResult = new List<dynamic>();
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.GetHospDetails",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@Input",SqlDbType.NVarChar).Value = id;
                        sqlCommand.Parameters.Add("@InputType",SqlDbType.NVarChar).Value = type;
                        sqlConnection.Open();
                        using (var dataReader = sqlCommand.ExecuteReader())
                        {
                            while (dataReader.Read())
                            {
                                var dataRow = new ExpandoObject() as IDictionary<string, object>;
                                for (var iFiled = 0; iFiled < dataReader.FieldCount; iFiled++)
                                    dataRow.Add(
                                        dataReader.GetName(iFiled),
                                        dataReader.IsDBNull(iFiled) ? null : dataReader[iFiled] // use null instead of {}
                                    );

                                jsonResult.Add((ExpandoObject)dataRow);
                            }
                        }
                        sqlConnection.Close();
                        // var reader = sqlCommand.ExecuteReader();
                        // var dt = new DataTable();
                        // dt.Load(reader);
                        //jsonResult = dt;
                    }
                }
                return Ok(jsonResult[0]);
                //return Ok(new {Result = jsonResult});
            }
            catch(Exception ex)
            {
                return BadRequest(error: ex.Message);
            }
        }
    }
}