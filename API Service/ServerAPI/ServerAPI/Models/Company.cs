using System;
using System.Collections.Generic;

namespace ServerAPI.Models
{
    public partial class Company
    {
        public Company()
        {
            Building = new HashSet<Building>();
        }

        public string Id { get; set; }
        public string Name { get; set; }

        public virtual ICollection<Building> Building { get; set; }
    }
}
