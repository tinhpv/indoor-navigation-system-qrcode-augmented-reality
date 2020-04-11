using System;
using System.Collections.Generic;

namespace ServerAPI.Models.DefaultModel
{
    public class JsonInputModel
    {
        public string BuildingId { get; set; }
        public string BuildingName { get; set; }
        public string Description { get; set; }
        public List<Floor> ListFloor { get; set; }

        public JsonInputModel()
        {
        }
    }

    public class Floor
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public string LinkMap { get; set; }

        public List<LocationModel> ListLocation { get; set; }

        public void addLocation(LocationModel location)
        {
            if (this.ListLocation == null)
            {
                this.ListLocation = new List<LocationModel>();
            }

            this.ListLocation.Add(location);
        }
    }

    public class LocationBeside
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public string Orientation { get; set; }
        public double? Distance { get; set; }

        public LocationBeside()
        {
        }

        public LocationBeside(string id, string name, string orientation, double? distance)
        {
            Id = id;
            Name = name;
            Orientation = orientation;
            Distance = distance;
        }
    }

    public class Room
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public double? RatioX { get; set; }
        public double? RatioY { get; set; }
        public bool? SpecialRoom { get; set; }
        public string SpaceAnchorId { get; set; }
    }

    public class Building
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public int? Version { get; set; }
        public string DayExpired { get; set; }

        // dựa vào ngày hết hạn, kiểm tra xem có được tải hay không
        // nếu hết hạn thì = true và KHÔNG được tải
        // còn hạn thì = false và được tải
        //public bool HadExpired { get; set; }

        public bool? Active { get; set; }

        public List<Floor> ListFloor { get; set; }
    }

    public class Company
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public List<Building> ListBuilding { get; set; }
    }
}
