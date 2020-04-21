using System;
using System.Collections.Generic;

namespace ServerAPI.Models.DefaultModel
{
    public class LocationModel
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public double RatioX { get; set; }
        public double RatioY { get; set; }
        public string LinkQR { get; set; }
        public string QrAnchorId { get; set; }
        public string SpaceAnchorId { get; set; }
        public List<LocationBeside> ListLocationBeside { get; set; }
        public List<Room> ListRoom { get; set; }

        public LocationModel()
        {
        }
    }

    
}
