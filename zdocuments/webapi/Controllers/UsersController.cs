using AutoMapper;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using webapi.Dtos;
using webapi.Helpers;
using webapi.Services;
using System.IdentityModel.Tokens.Jwt;
using Microsoft.IdentityModel.Tokens;
using System.Security.Claims;
using System;
using webapi.Entities;
using System.Collections.Generic;
using System.Net.Http;
using System.Data.SqlClient;
using System.Data;
using Newtonsoft.Json;

namespace webapi.Controllers
{
    [Authorize]
    [Route("[controller]")]
    public class UsersController : Controller
    {
        private IUserService _userService;
        private IMapper _mapper;
        private readonly AppSettings _appSettings;

        public UsersController(
            IUserService userService,
            IMapper mapper,
            IOptions<AppSettings> appSettings)
        {
            _userService = userService;
            _mapper = mapper;
            _appSettings = appSettings.Value;
        }

        [AllowAnonymous]
        [HttpPost("authenticate")]
        public IActionResult Authenticate([FromBody]UserDto userDto)
        {
            try{
                var user = _userService.Authenticate(userDto.Phone, userDto.Password);
                if(user == null)
                    return Unauthorized();

                var tokenHandler = new JwtSecurityTokenHandler();
                var key = System.Text.Encoding.ASCII.GetBytes(_appSettings.Secret);
                var tokenDescriptor = new SecurityTokenDescriptor
                {
                    Subject = new ClaimsIdentity(new Claim[]
                    {
                        new Claim(ClaimTypes.Name, userDto.Id.ToString())
                    }),
                    Expires = DateTime.UtcNow.AddHours(24),
                    SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(key), SecurityAlgorithms.HmacSha256Signature)
                };

                var token = tokenHandler.CreateToken(tokenDescriptor);
                var tokenString = tokenHandler.WriteToken(token);
                int signinId = 0;
                userDto.Id = user.Id;
                
                using(var sqlConnection = new SqlConnection(_appSettings.ConnectionString))
                {
                    using(var sqlCommand = new SqlCommand("dbo.usp_logsignin_details",sqlConnection))
                    {
                        sqlCommand.CommandType = CommandType.StoredProcedure;
                        sqlCommand.Parameters.Add("@json",SqlDbType.NVarChar, -1).Value = JsonConvert.SerializeObject(userDto);
                        sqlCommand.Parameters.Add("@output", SqlDbType.Int);
                        sqlCommand.Parameters["@output"].Direction = ParameterDirection.Output;
                        sqlConnection.Open();
                        sqlCommand.ExecuteReader();
                        sqlConnection.Close();
                        signinId = Int32.Parse(sqlCommand.Parameters["@output"].Value.ToString());
                    }
                }

                //return basic user info (without password) and token to store client side
                return Ok(new {
                    UserId = user.Id,
                    SigninId = signinId,
                    Phone = user.Phone,
                    FirstName = user.FirstName,
                    LastName = user.LastName,
                    Email = user.Email,
                    UserType = user.UserType,
                    Token = tokenString
                });
            }
            catch (AppException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (Exception e){
                string dummystr = e.Message;
                dummystr = dummystr + "this is to supress warning while build";
                return Unauthorized();
            }
        }

        [AllowAnonymous]
        [HttpPost("register")]
        public IActionResult Register([FromBody]UserDto userDto)
        {
            //map dto to entity
            userDto.OTP = new Random().Next(1000, 9999);
            var user = _mapper.Map<User>(userDto);

            try{
                _userService.Create(user, userDto.Password);
                new SendOTP(_appSettings).CallOTPAPI(userDto.Phone, userDto.OTP.ToString());
                return Ok("Pending OTP confirmation");
            }
            catch (AppException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (Exception e){
                return BadRequest(e.Message);
            }
        }

        [AllowAnonymous]
        [HttpPost("otp")]
        public IActionResult Otp([FromBody]UserDto userDto)
        {
            var user = _mapper.Map<User>(userDto);

            try{
                _userService.ValidateOTP(user);
                return Ok("Resigtration successful");
            }
            catch (AppException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (Exception e){
                return BadRequest(e.Message);
            }
        }

        // [HttpGet]
        // public IActionResult GetAll()
        // {
        //     var users =  _userService.GetAll();
        //     var userDtos = _mapper.Map<IList<UserDto>>(users);
        //     return Ok(userDtos);
        // }

        // [HttpGet("{id int}")]
        // public IActionResult GetById(int id)
        // {
        //     var user =  _userService.GetByID(id);
        //     var userDto = _mapper.Map<UserDto>(user);
        //     return Ok(userDto);
        // }

        [AllowAnonymous]
        [HttpPost("forgotpassword")]
        public IActionResult ForgotPassword([FromBody]UserDto userDto)
        {
           
            userDto.OTP = new Random().Next(1000, 9999);
            var user = _mapper.Map<User>(userDto);
           
            try{
                _userService.FindUser(user);
                new SendOTP(_appSettings).CallOTPAPI(userDto.Phone, userDto.OTP.ToString());
                return Ok("Otp sent");
            }
            catch(AppException ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [AllowAnonymous]
        [HttpPost("updatepassword")]
        public IActionResult UpdatePassword([FromBody]UserDto userDto)
        {
            //map dto to entity and set id
            var user = _mapper.Map<User>(userDto);
            //user.Id = id;

            try{
                _userService.Update(user, userDto.Password);
                return Ok("Password updated successfully");
            }
            catch(AppException ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // [HttpDelete("{id int}")]
        // public IActionResult Delete(int id)
        // {
        //     _userService.Delete(id);
        //     return Ok();
        // }
    }
}