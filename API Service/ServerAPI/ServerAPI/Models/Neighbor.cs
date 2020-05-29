using System;
using System.Collections.Generic;

namespace ServerAPI.Models
{
    public partial class Neighbor
    {
        public int Id { get; set; }
        public string LocationId { get; set; }
        public string LocationBesideId { get; set; }
        public string Orientation { get; set; }
        public double Distance { get; set; }
        public bool? Active { get; set; }

        public virtual Location Location { get; set; }
        public virtual Location LocationBeside { get; set; }
    }
}
