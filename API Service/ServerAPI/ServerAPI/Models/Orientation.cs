using System;
using System.Collections.Generic;

namespace ServerAPI.Models
{
    public partial class Orientation
    {
        public Orientation()
        {
            LocationBeside = new HashSet<LocationBeside>();
        }

        public int Id { get; set; }
        public string Name { get; set; }

        public virtual ICollection<LocationBeside> LocationBeside { get; set; }
    }
}
