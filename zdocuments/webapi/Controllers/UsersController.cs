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
                    Expires = DateTime.UtcNow.AddHours(2),
                    SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(key), SecurityAlgorithms.HmacSha256Signature)
                };

                var token = tokenHandler.CreateToken(tokenDescriptor);
                var tokenString = tokenHandler.WriteToken(token);

                //return basic user info (without password) and token to store client side
                return Ok(new {
                    Id = user.Id,
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
                HttpClient optapi = new HttpClient();
                string urlParameters = _appSettings.OtpDefaultParam;
                urlParameters = urlParameters+"&mobiles=+91"+user.Phone;
                urlParameters = urlParameters+"&authkey="+_appSettings.OtpAuthKey;
                urlParameters = urlParameters+"&message="+_appSettings.OtpMessage+user.OTP;
                optapi.BaseAddress = new Uri(_appSettings.OtpApi);  
                HttpResponseMessage response = optapi.GetAsync(urlParameters).Result;
                optapi.Dispose();
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

        [HttpPut]
        public IActionResult Update([FromBody]UserDto userDto)
        {
            //map dto to entity and set id
            var user = _mapper.Map<User>(userDto);
            //user.Id = id;

            try{
                _userService.Update(user, userDto.Password);
                return Ok();
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