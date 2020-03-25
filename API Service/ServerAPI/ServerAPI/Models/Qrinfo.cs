using System;
using System.Collections.Generic;

namespace ServerAPI.Models
{
    public partial class Qrinfo
    {
        public Qrinfo()
        {
            Location = new HashSet<Location>();
        }

        public int Id { get; set; }
        public string Description { get; set; }
        public string Link { get; set; }

        public virtual ICollection<Location> Location { get; set; }
    }
}
