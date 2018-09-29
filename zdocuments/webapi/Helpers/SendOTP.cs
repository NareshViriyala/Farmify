using System;
using System.Net.Http;
using Microsoft.Extensions.Options;

namespace webapi.Helpers
{
    public class SendOTP
    {
       public AppSettings _appSettings;
       public SendOTP(AppSettings appSettings)
       {
            _appSettings = appSettings;
       }

        

        public bool CallOTPAPI(string Phone, string OTP)
        {
            bool output = false;
            try
            {
                HttpClient optapi = new HttpClient();
                string urlParameters = _appSettings.OtpDefaultParam;
                urlParameters = urlParameters+"&mobiles=+91"+Phone;
                urlParameters = urlParameters+"&authkey="+_appSettings.OtpAuthKey;
                urlParameters = urlParameters+"&message="+_appSettings.OtpMessage+OTP;
                optapi.BaseAddress = new Uri(_appSettings.OtpApi);  
                HttpResponseMessage response = optapi.GetAsync(urlParameters).Result;
                optapi.Dispose();
                output = true;
            }
            catch (System.Exception)
            {
                output = false;
            }
            return output;
        }
    }
}