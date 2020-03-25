using System;
using System.Collections.Generic;

namespace ServerAPI.Models
{
    public partial class Room
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public double? RatioX { get; set; }
        public double? RatioY { get; set; }
        public string LocationId { get; set; }
        public bool? SpecialRoom { get; set; }

        public virtual Location Location { get; set; }
    }
}
