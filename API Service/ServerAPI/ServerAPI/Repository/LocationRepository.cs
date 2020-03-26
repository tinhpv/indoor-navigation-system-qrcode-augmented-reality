using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Newtonsoft.Json;
using QRCodeEncoderLibrary;
using ServerAPI.IRepository;
using ServerAPI.Models;
using ServerAPI.Models.DefaultModel;
using LocationBeside = ServerAPI.Models.DefaultModel.LocationBeside;
using LocationBesideDb = ServerAPI.Models.LocationBeside;
using RoomDb = ServerAPI.Models.Room;
using RoomModel = ServerAPI.Models.DefaultModel.Room;
using Floor = ServerAPI.Models.DefaultModel.Floor;
using Building = ServerAPI.Models.DefaultModel.Building;
using System.Net;
using System.Globalization;

namespace ServerAPI.Repository
{
    public class LocationRepository : ILocationRepository
    {
        

        private readonly Database.AppContext context;
        private IWebHostEnvironment hostingEnvironment;

        public LocationRepository(IWebHostEnvironment _hostingEnvironment, Database.AppContext _appContext)
        {
            hostingEnvironment = _hostingEnvironment;
            context = _appContext;
        }

        public Building GetLocations(string buildingId)
        {

            var building = context.Building.Where(x => x.Id == buildingId).FirstOrDefault();

            if (building != null)
            {
                // add info
                var buildingModel = new Building();



                buildingModel.Name = building.Name.ToString();
                buildingModel.Id = building.Id;
                buildingModel.Description = building.Description.ToString();

                // add version + day expired
                buildingModel.Version = building.Version;
                // check day expired
                //DateTime now = DateTime.Now;
                DateTime dayExpired = DateTime.Parse(building.DayExpired.ToString());
                //if ((dayExpired.Date - now.Date).Days > 0)
                //{
                //    buildingModel.HadExpired = false;
                //}
                //else
                //{
                //    buildingModel.HadExpired = true;
                //}
                buildingModel.DayExpired = dayExpired.ToString("MM/dd/yyyy", CultureInfo.InvariantCulture);



                var result = new List<Floor>();
                foreach (var floor in context.Floor.Where(x => x.BuildingId == building.Id).ToList())
                {
                    var floorModel = new Floor();
                    floorModel.Id = floor.Id;
                    floorModel.Name = floor.Name;
                    floorModel.LinkMap = floor.LinkMap;

                    foreach (var item in context.Location.Where(x => x.FloorId == floor.Id).OrderBy(x => x.Id).ToList())
                    {
                        var location = new LocationModel();

                        location.Id = item.Id;
                        location.Name = item.Name;
                        location.RatioX = item.RatioX;
                        location.RatioY = item.RatioY;
                        location.LinkQR = item.LinkQrcode;

                        var listLocationBeside = new List<LocationBeside>();
                        foreach (var itemLocation in context.LocationBeside.Where(x => x.LocationId == item.Id).OrderBy(x => x.Id).ToList())
                        {
                            listLocationBeside.Add(new LocationBeside(
                                context.Location.Where(x => x.Id == itemLocation.LocationBesideId).FirstOrDefault().Id,
                                context.Location.Where(x => x.Id == itemLocation.LocationBesideId).FirstOrDefault().Name.ToString(),
                                //context.Orientation.Where(x => x.Id == itemLocation.OrientitationId).FirstOrDefault().Name.ToString(),
                                itemLocation.OrientitationId,
                                itemLocation.Distance
                                ));
                        }
                        location.ListLocationBeside = listLocationBeside;

                        var listRoom = new List<RoomModel>();
                        foreach (var room in context.Room.Where(x => x.LocationId == location.Id).ToList())
                        {
                            listRoom.Add(new RoomModel
                            {
                                Id = room.Id,
                                Name = room.Name,
                                RatioX = room.RatioX,
                                RatioY = room.RatioY,
                                SpecialRoom = room.SpecialRoom
                            });
                        }
                        location.ListRoom = listRoom;

                        // add location
                        floorModel.addLocation(location);
                    }

                    result.Add(floorModel);
                }

                buildingModel.ListFloor = result;

                return buildingModel;
            }
            return null;
        }

        public string UpdateDataBuilding(IFormFile file)
        {
            var reader = new StreamReader(file.OpenReadStream());
            string json = reader.ReadToEnd();
            JsonInputModel model = JsonConvert.DeserializeObject<JsonInputModel>(json);

            string buildingId = model.BuildingId;
            string buildingName = model.BuildingName;
            string description = model.Description;

            // cập nhập data mới
            var buildingNew = context.Building.Where(x => x.Id == buildingId).FirstOrDefault();
            if (buildingNew != null)
            {
                buildingNew.Name = buildingName;
                buildingNew.Description = description;

                int? version = buildingNew.Version;
                buildingNew.Version = version + 1;

                context.SaveChanges();
            }

            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // remove old data
            var listFloor = context.Floor.Where(x => x.BuildingId == buildingId).ToList();

            var listLocation = new List<Location>();

            foreach (var floor in listFloor)
            {
                listLocation.AddRange(context.Location.Where(x => x.FloorId == floor.Id).ToList());
            }

            var listLocationBeside = new List<LocationBesideDb>();

            foreach (var location in listLocation)
            {
                listLocationBeside.AddRange(context.LocationBeside.Where(x => x.LocationId == location.Id).ToList());
            }

            var listRoom = new List<RoomDb>();

            foreach (var location in listLocation)
            {
                listRoom.AddRange(context.Room.Where(x => x.LocationId == location.Id).ToList());
            }

            try
            {
                // delete list Room
                context.Room.RemoveRange(listRoom);
                // delete list Location beside
                context.LocationBeside.RemoveRange(listLocationBeside);
                // delete list Location
                context.Location.RemoveRange(listLocation);
                // delete list Floor
                context.Floor.RemoveRange(listFloor);

                context.SaveChanges();

                // delete Map
                foreach (var floor in listFloor)
                {
                    deleteMap(floor.Id);
                }

                // delete QR Code
                foreach (var location in listLocation)
                {
                    deleteQRCode(location.Id);
                }

                // clear data
                listFloor.Clear();
                listLocation.Clear();
                listLocationBeside.Clear();
                listRoom.Clear();
            }
            catch (Exception e)
            {
                return e.Data.ToString();
            }

            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // add new data

            // add floor
            foreach (var floor in model.ListFloor)
            {
                context.Floor.Add(new Models.Floor
                {
                    BuildingId = buildingId,
                    Id = buildingId + "_f_" + floor.Id,
                    Name = floor.Name,
                    // save map
                    LinkMap = saveMap(floor.LinkMap, buildingId + "_f_" + floor.Id)
                });
                context.SaveChanges();
            }

            //var listLocationBeside = new List<LocationBesideDb>();



            // add location
            foreach (var floor in model.ListFloor)
            {
                foreach (var location in floor.ListLocation)
                {
                    context.Location.Add(new Location
                    {
                        //Id = buildingId  + "_l_" + location.Id,
                        Id = buildingId + "_l_" + location.Id,
                        Name = location.Name,
                        RatioX = location.RatioX,
                        RatioY = location.RatioY,
                        FloorId = buildingId + "_f_" + floor.Id,
                        LinkQrcode = generateQRCode("ID: " + buildingId + "_l_" + location.Id + " | Name: " + location.Name, buildingId + "_l_" + location.Id)
                    });
                    context.SaveChanges();

                    // add room
                    foreach (var room in location.ListRoom)
                    {
                        string roomId = buildingId + "_f_" + floor.Id + "_r_" + room.Id;
                        context.Room.Add(new RoomDb
                        {
                            Id = roomId,
                            Name = room.Name,
                            RatioX = room.RatioX,
                            RatioY = room.RatioY,
                            LocationId = buildingId + "_l_" + location.Id,
                            SpecialRoom = room.SpecialRoom
                        });
                        context.SaveChanges();
                    }


                    foreach (var neighbor in location.ListLocationBeside)
                    {
                        listLocationBeside.Add(new LocationBesideDb
                        {
                            //Id = 0,
                            LocationId = buildingId + "_l_" + location.Id,
                            LocationBesideId = buildingId + "_l_" + neighbor.Id,
                            OrientitationId = neighbor.Orientation,
                            Distance = neighbor.Distance
                        });
                    }
                }


            }
            // add location beside

            foreach (var item in listLocationBeside)
            {
                context.LocationBeside.Add(item);
                context.SaveChanges();
            }

            //context.LocationBeside.AddRange(listLocationBeside);

            //context.SaveChanges();

            listLocationBeside.Clear();

            return "OK";
        }


        public String CreateNewBuilding(Building building)
        {
            context.Building.Add(new Models.Building
            {
                Id = building.Id,
                Name = building.Name,
                Description = building.Description,
                CompanyId = "com_1",
                DayExpired = Convert.ToDateTime(building.DayExpired),
                Version = 0,
                Active = building.Active
            });

            // add new building
            context.SaveChanges();

            var listFloor = building.ListFloor;
            var buildingId = building.Id;

            // add floor
            foreach (var floor in listFloor)
            {
                context.Floor.Add(new Models.Floor
                {
                    BuildingId = buildingId,
                    Id = buildingId + "_f_" + floor.Id,
                    Name = floor.Name,
                    // save map
                    LinkMap = saveMap(floor.LinkMap, buildingId + "_f_" + floor.Id)
                });
                context.SaveChanges();
            }

            //var listLocationBeside = new List<LocationBesideDb>();


            var listLocationBeside = new List<LocationBesideDb>();


            // add location
            foreach (var floor in listFloor)
            {
                foreach (var location in floor.ListLocation)
                {
                    context.Location.Add(new Location
                    {
                        //Id = buildingId  + "_l_" + location.Id,
                        Id = buildingId + "_l_" + location.Id,
                        Name = location.Name,
                        RatioX = location.RatioX,
                        RatioY = location.RatioY,
                        FloorId = buildingId + "_f_" + floor.Id,
                        LinkQrcode = generateQRCode("ID: " + buildingId + "_l_" + location.Id + " | Name: " + location.Name, buildingId + "_l_" + location.Id)
                    });
                    context.SaveChanges();

                    // add room
                    foreach (var room in location.ListRoom)
                    {
                        string roomId = buildingId + "_f_" + floor.Id + "_r_" + room.Id;
                        context.Room.Add(new RoomDb
                        {
                            Id = roomId,
                            Name = room.Name,
                            RatioX = room.RatioX,
                            RatioY = room.RatioY,
                            LocationId = buildingId + "_l_" + location.Id,
                            SpecialRoom = room.SpecialRoom
                        });
                        context.SaveChanges();
                    }


                    foreach (var neighbor in location.ListLocationBeside)
                    {
                        listLocationBeside.Add(new LocationBesideDb
                        {
                            //Id = 0,
                            LocationId = buildingId + "_l_" + location.Id,
                            LocationBesideId = buildingId + "_l_" + neighbor.Id,
                            OrientitationId = neighbor.Orientation,
                            Distance = neighbor.Distance
                        });
                    }
                }


            }
            // add location beside

            foreach (var item in listLocationBeside)
            {
                context.LocationBeside.Add(item);
                context.SaveChanges();
            }

            //context.LocationBeside.AddRange(listLocationBeside);

            //context.SaveChanges();

            listLocationBeside.Clear();

            return "OK";
        }








        // ------------------------------------------





        private string generateQRCode(string content, string locationId)
        {
            string webRootPath = hostingEnvironment.WebRootPath;
            // create QREncoder object
            QREncoder Encoder = new QREncoder();




            // set optional parameters
            //Encoder.ErrorCorrection = ErrorCorrection.Q;
            Encoder.ErrorCorrection = ErrorCorrection.M;
            Encoder.ModuleSize = 10;
            Encoder.QuietZone = 40;

            // encode input text string 
            // Note: there are 4 Encode methods in total
            Encoder.Encode(content);

            // save the barcode to PNG file
            // This method DOES NOT use Bitmap class and is suitable for net-core and net-standard
            // It produces files significantly smaller than SaveQRCodeToFile.
            string qrPath = Path.Combine(webRootPath, "QRCode");
            if (!Directory.Exists(qrPath))
            {
                Directory.CreateDirectory(qrPath);
            }
            var qrCodeName = locationId + ".png";
            Encoder.SaveQRCodeToPngFile(Path.Combine(qrPath, qrCodeName));

            //var asset = context.Asset.Where(x => x.Id == id).FirstOrDefault();

            //asset.Image = "http://amsapinew.azurewebsites.net/AssetImage/" + fileName;
            return "http://13.229.117.90:7070/QRCode/" + qrCodeName;
        }

        private bool deleteQRCode(string locationId)
        {
            string webRootPath = hostingEnvironment.WebRootPath;
            string qrPath = Path.Combine(webRootPath, "QRCode");

            var qrCodeName = locationId + ".png";
            var file = Path.Combine(qrPath, qrCodeName);
            try
            {
                if (File.Exists(file))
                {
                    File.Delete(file);
                }
            }
            catch (Exception e)
            {
                return false;
            }
            return true;
        }

        private string saveMap(string linkMap, string nameMap)
        {
            using (var client = new WebClient())
            {
                string webRootPath = hostingEnvironment.WebRootPath;
                string mapPath = Path.Combine(webRootPath, "Map");
                if (!Directory.Exists(mapPath))
                {
                    Directory.CreateDirectory(mapPath);
                }
                var file = Path.Combine(mapPath, nameMap + ".png");

                client.DownloadFile(linkMap, file);

                return "http://13.229.117.90:7070/Map/" + nameMap + ".png";
            }
        }

        private bool deleteMap(string name)
        {
            string webRootPath = hostingEnvironment.WebRootPath;
            string mapPath = Path.Combine(webRootPath, "Map");

            var mapName = name + ".png";
            var file = Path.Combine(mapPath, mapName);
            try
            {
                if (File.Exists(file))
                {
                    File.Delete(file);
                }
            }
            catch (Exception e)
            {
                return false;
            }
            return true;
        }


    }
}
