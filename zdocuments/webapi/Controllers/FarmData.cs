using System;
using System.Collections.Generic;
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
    public class FarmData : Controller
    {
        private readonly AppSettings _appSettings;

        public FarmData(IOptions<AppSettings> appSettings)
        {
            _appSettings = appSettings.Value;
        }

        [HttpGet("{phone string}")]
        [Route("[action]")]
        public IActionResult getFarmData(string phone)
        {
            try
            {
                List<FarmDataItem> list = new List<FarmDataItem>();
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.usp_get_farms",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@phone",SqlDbType.NVarChar, 50).Value = phone;
                        sqlConnection.Open();
                        SqlDataReader reader = sqlCommand.ExecuteReader();
                        
                        if(reader.HasRows)
                        {
                            while(reader.Read())
                            {
                                FarmDataItem item = new FarmDataItem();
                                item.Id = Int32.Parse(reader["Id"].ToString());
                                item.farmer_type = reader["FarmerType"].ToString();
                                item.farmer_category = reader["FarmerCategory"].ToString();

                                string croptype = reader["CropType"].ToString();
                                croptype = croptype == ""?"[]":croptype;
                                item.crop_type = JArray.Parse(croptype);

                                item.crop_type_other = reader["CropTypeOther"].ToString();
                                item.soil_type = reader["SoilType"].ToString();
                                item.soil_type_other = reader["SoilTypeOther"].ToString();
                                item.water_source = reader["WaterSource"].ToString();

                                string crophistory = reader["CropHistory"].ToString();
                                crophistory = crophistory == ""?"[]":crophistory;
                                item.crop_history =  JArray.Parse(crophistory);

                                item.land_acers = reader["LandAcers"].ToString();
                                item.soil_testing = (bool)reader["SoilTesting"];
                                item.farm_exp = reader["FarmExp"].ToString();
                                item.crop_insurance = (bool)reader["CropInsurance"];
                                list.Add(item);
                            }
                            return Ok(new {result = list});
                        }
                        else
                        {
                            sqlConnection.Close();
                            return BadRequest(new {result = "no data found"});
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpGet("{Id int}")]
        [Route("[action]")]
        public IActionResult getExpenseReport(int Id)
        {
            try
            {
                FarmExpenseDetails data = new FarmExpenseDetails();
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.usp_get_farmexpensereport",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@farm_id",SqlDbType.Int).Value = Id;
                        sqlConnection.Open();
                        SqlDataReader reader = sqlCommand.ExecuteReader();
                        
                        if(reader.HasRows)
                        {
                            while(reader.Read())
                            {
                                data.agent_id = Int32.Parse(reader["agent_id"].ToString());
                                data.farm_id = Int32.Parse(reader["farm_id"].ToString());
                                data.farm_expense_id = Int32.Parse(reader["farm_expense_id"].ToString());
                                data.crop_type = reader["CropType"].ToString();
                                data.land_acers = float.Parse(reader["LandAcers"].ToString());
                                data.profit_share = float.Parse(reader["ProfitShare"].ToString());
                                data.start_date = DateTime.Parse(reader["StartDate"].ToString()).ToString("yyyy-MM-dd");
                                data.end_date = DateTime.Parse(reader["EndDate"].ToString()).ToString("yyyy-MM-dd");

                                string exp = reader["Expenses"].ToString();
                                exp = exp == ""?"[]":exp;
                                data.expenses = JArray.Parse(exp);
                            }
                            return Ok(new {result = data});
                        }
                        else
                        {
                            sqlConnection.Close();
                            return Ok(new {result = ""});
                        }
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
        public IActionResult putExpenseReport([FromBody]FarmExpenseDetails jsonString)
        {
            try
            {
                bool output = false;
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.usp_put_farmexpensereport",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@json",SqlDbType.NVarChar, -1).Value = JsonConvert.SerializeObject(jsonString);
                        sqlCommand.Parameters.Add("@output", SqlDbType.Bit);
                        sqlCommand.Parameters["@output"].Direction = ParameterDirection.Output;
                        sqlConnection.Open();
                        sqlCommand.ExecuteReader();
                        sqlConnection.Close();
                        output = (bool)sqlCommand.Parameters["@output"].Value;
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

    public class FarmDataItem{
        public int Id {get; set;}
        public string farmer_type {get; set;}
        public string farmer_category {get; set;} 
        public JArray crop_type {get; set;} 
        public string crop_type_other {get; set;} 
        public string soil_type {get; set;} 
        public string soil_type_other {get; set;} 
        public string water_source {get; set;} 
        public JArray crop_history {get; set;} 
        public string land_acers {get; set;} 
        public bool soil_testing {get; set;} 
        public string farm_exp {get; set;} 
        public bool crop_insurance {get; set;} 
    }

    public class FarmExpenseDetails{
        public int agent_id {get; set;}
        public int farm_id {get; set;}
        public int farm_expense_id {get; set;}
        public string crop_type {get; set;} 
        public float land_acers {get; set;} 
        public float profit_share {get; set;} 
        public string start_date {get; set;}
        public string end_date {get; set;}
        public JArray expenses {get; set;} 
    }
}