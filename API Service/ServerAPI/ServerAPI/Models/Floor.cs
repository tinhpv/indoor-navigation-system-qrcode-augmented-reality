using System;
using System.Collections.Generic;

namespace ServerAPI.Models
{
    public partial class Floor
    {
        public Floor()
        {
            Location = new HashSet<Location>();
        }

        public string Id { get; set; }
        public string Name { get; set; }
        public string BuildingId { get; set; }
        public string LinkMap { get; set; }

        public virtual Building Building { get; set; }
        public virtual ICollection<Location> Location { get; set; }
    }
}
