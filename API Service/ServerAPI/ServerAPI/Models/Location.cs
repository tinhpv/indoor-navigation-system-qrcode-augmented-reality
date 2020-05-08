using System;
using System.Collections.Generic;

namespace ServerAPI.Models
{
    public partial class Location
    {
        public Location()
        {
            NeighborLocation = new HashSet<Neighbor>();
            NeighborLocationBeside = new HashSet<Neighbor>();
            Room = new HashSet<Room>();
        }

        public string Id { get; set; }
        public string Name { get; set; }
        public double RatioX { get; set; }
        public double RatioY { get; set; }
        public string FloorId { get; set; }
        public string LinkQrcode { get; set; }
        public string QranchorId { get; set; }
        public string SpaceAnchorId { get; set; }

        public virtual Floor Floor { get; set; }
        public virtual ICollection<Neighbor> NeighborLocation { get; set; }
        public virtual ICollection<Neighbor> NeighborLocationBeside { get; set; }
        public virtual ICollection<Room> Room { get; set; }
    }
}
