using webapi.Entities;
using Microsoft.EntityFrameworkCore;

namespace webapi.Helpers
{
    public class DataContext : DbContext
    {
        public DataContext(DbContextOptions<DataContext> options) : base(options){}
        public DbSet<User> Users {get; set;}
    }
}