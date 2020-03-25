using System;
using System.Collections.Generic;

namespace ServerAPI.Models
{
    public partial class Location
    {
        public Location()
        {
            LocationBesideLocation = new HashSet<LocationBeside>();
            LocationBesideLocationBesideNavigation = new HashSet<LocationBeside>();
            Room = new HashSet<Room>();
        }

        public string Id { get; set; }
        public string Name { get; set; }
        public double? RatioX { get; set; }
        public double? RatioY { get; set; }
        public string FloorId { get; set; }
        public string LinkQrcode { get; set; }

        public virtual Floor Floor { get; set; }
        public virtual ICollection<LocationBeside> LocationBesideLocation { get; set; }
        public virtual ICollection<LocationBeside> LocationBesideLocationBesideNavigation { get; set; }
        public virtual ICollection<Room> Room { get; set; }
    }
}
