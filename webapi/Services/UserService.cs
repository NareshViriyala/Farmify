using System;
using System.Collections.Generic;
using System.Linq;
using webapi.Entities;
using webapi.Helpers;

namespace webapi.Services
{
    public interface IUserService
    {
        User Authenticate(string username, string password) ;
        IEnumerable<User> GetAll();
        User GetByID(int id);
        User Create(User user, string password);
        void Update(User user, string password = null);
        void Delete(int id);
    }

    public class UserService : IUserService
    {
        private DataContext _context;
        public UserService(DataContext context)
        {
            _context = context;
        }

        public User Authenticate(string phone, string password)
        {
            if (string.IsNullOrEmpty(phone) || string.IsNullOrEmpty(password))
                return null;

            var user = _context.Users.SingleOrDefault(x => x.Phone == phone);
            
            //check if the username exists
            if(user == null)
                throw new AppException("Phone number not registered");

            //check if the password is correct
            if(!VerifyPasswordHash(password, user.PasswordHash, user.PasswordSalt))
                throw new AppException("Invalid phone number or password");

            //authentication successful
            return user;
        }

        public IEnumerable<User> GetAll()
        {
            return _context.Users;
        }

        public User GetByID(int id)
        {
            return _context.Users.Find(id);
        }

        public User Create(User user, string password)
        {
            //validation
            if (string.IsNullOrWhiteSpace(password))
                throw new AppException("Password is required");
        
            if(_context.Users.Any(x=> x.Phone == user.Phone))
                throw new AppException("Phone number :"+user.Phone+" is already taken");

            byte[] passwordHash, passwordSalt;
            CreatePasswordHash(password, out passwordHash, out passwordSalt);                

            user.PasswordHash = passwordHash;
            user.PasswordSalt = passwordSalt;

            _context.Users.Add(user);
            _context.SaveChanges();

            return user;
        }

        public void Update(User userParam, string password = null)
        {
            var user = _context.Users.Find(userParam.Id);

            //user does not exist
            if(user == null)
                throw new AppException("User not found");

            //if in the update request, the username has been changed
            if(userParam.Phone != userParam.Phone)
            {
                //if the updated username is already taken, then please provide the message
                if(_context.Users.Any(x => x.Phone == userParam.Phone))
                    throw new AppException("Phone number " + userParam.Phone + " is already taken");
            }

            //update user properties
            user.FirstName = userParam.FirstName;
            user.LastName = userParam.LastName;
            user.Phone = userParam.Phone;
            user.Email = userParam.Email;

            //update password if it was entered
            if(!string.IsNullOrWhiteSpace(password))
            {
                byte[] passwordHash, passwordSalt;
                CreatePasswordHash(password, out passwordHash, out passwordSalt);

                user.PasswordHash = passwordHash;
                user.PasswordSalt = passwordSalt;
            }    

            _context.Users.Update(user);
            _context.SaveChanges();
        }

        public void Delete(int id)
        {
            var user = _context.Users.Find(id);
            if(user != null)
            {
                _context.Users.Remove(user);
                _context.SaveChanges();
            }
        }

        //private helper method
        private static void CreatePasswordHash(string password, out byte[] passwordHash, out byte[] passwordSalt)
        {
            if(password == null) throw new ArgumentNullException("password");    
            if(string.IsNullOrWhiteSpace(password)) throw new ArgumentException("Value cannot be empty or whitespace only string.", "password");

            using(var hmac = new System.Security.Cryptography.HMACSHA512())
            {
                passwordSalt = hmac.Key;
                passwordHash = hmac.ComputeHash(System.Text.Encoding.UTF8.GetBytes(password));
            }
        }

        private static bool VerifyPasswordHash(string password, byte[] passwordHash, byte[] passwordSalt)
        {
            if(password == null) throw new ArgumentNullException("password");    
            if(string.IsNullOrWhiteSpace(password)) throw new ArgumentException("Value cannot be empty or whitespace only string.", "password");

            if(passwordHash.Length != 64) throw new ArgumentException("Invalid length of password hash (64 bytes expected).", "passwordHash");
            if(passwordSalt.Length != 128) throw new ArgumentException("Invalid length of password salt (128 bytes expected).", "passwordHash");

            using(var hmac = new System.Security.Cryptography.HMACSHA512(passwordSalt))
            {
                var computeHash = hmac.ComputeHash(System.Text.Encoding.UTF8.GetBytes(password));
                for(int i = 0; i < computeHash.Length; i++)
                {
                    if(computeHash[i] != passwordHash[i])
                        return false;
                }
                return true;
            }
        }
    }
}