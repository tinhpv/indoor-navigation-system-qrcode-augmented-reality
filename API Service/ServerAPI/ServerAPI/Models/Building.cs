using System;
using System.Collections.Generic;

namespace ServerAPI.Models
{
    public partial class Building
    {
        public Building()
        {
            Floor = new HashSet<Floor>();
        }

        public string Id { get; set; }
        public string Name { get; set; }
        public string CompanyId { get; set; }
        public DateTime? DayExpired { get; set; }
        public int? Version { get; set; }
        public string Description { get; set; }
        public bool? Active { get; set; }

        public virtual Company Company { get; set; }
        public virtual ICollection<Floor> Floor { get; set; }
    }
}
