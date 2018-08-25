using System.ComponentModel.DataAnnotations;

namespace webapi.Entities
{
    public class User
    {
        public int Id { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string Email { get; set; }
        //[Required]
        //[StringLength(50, ErrorMessage = "PHONE_MIN_LENGTH", MinimumLength = 10)]
        public string Phone { get; set; }
        public byte[] PasswordHash { get; set; }
        public byte[] PasswordSalt { get; set; }
    }
}