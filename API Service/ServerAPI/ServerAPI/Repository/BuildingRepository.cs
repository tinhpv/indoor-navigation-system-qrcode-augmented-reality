using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Newtonsoft.Json;
using ServerAPI.IRepository;
using ServerAPI.Models.DefaultModel;
using LocationBeside = ServerAPI.Models.DefaultModel.LocationBeside;
using LocationBesideDb = ServerAPI.Models.LocationBeside;
using RoomDb = ServerAPI.Models.Room;
using RoomModel = ServerAPI.Models.DefaultModel.Room;
using Floor = ServerAPI.Models.DefaultModel.Floor;
using Building = ServerAPI.Models.DefaultModel.Building;
using System.Net;
using ServerAPI.Models;
using QRCodeEncoderLibrary;
using Company = ServerAPI.Models.DefaultModel.Company;

namespace ServerAPI.Repository
{
    public class BuildingRepository : IBuildingRepository
    {
        public static readonly int ACTIVE = 111;
        public static readonly int DEACTIVE = 101;

        private readonly Database.AppContext context;
        private IWebHostEnvironment hostingEnvironment;

        public BuildingRepository(IWebHostEnvironment _hostingEnvironment, Database.AppContext _appContext)
        {
            hostingEnvironment = _hostingEnvironment;
            context = _appContext;
        }

        public List<Company> GetAllBuildingsActive()
        {
            var result = new List<Company>();

            foreach (var company in context.Company.ToList())
            {
                string id = company.Id;
                string name = company.Name;
                var listBuilding = new List<Building>();

                foreach (var building in context.Building.Where(x => x.CompanyId == company.Id).ToList())
                {

                    // add version + day expired
                    //buildingModel.Version = building.Version;
                    // check day expired
                    bool hadExpired;
                    DateTime now = DateTime.Now;
                    DateTime dayExpired = DateTime.Parse(building.DayExpired.ToString());
                    if ((dayExpired.Date - now.Date).Days > 0)
                    {
                        hadExpired = false;
                    }
                    else
                    {
                        hadExpired = true;
                    }

                    // chuyển Status của building hết hạn về DeACTIVE
                    if (hadExpired)
                    {
                        if (building.Active == true)
                        {
                            changeBuildingStatus(building.Id, DEACTIVE);
                        }
                    }
                }

                // chỉ trả về những building có Status là ACTIVE
                var resultBuilding = context.Building.Where(x => x.Active == true && x.CompanyId == company.Id).ToList();
                foreach (var building in resultBuilding)
                {
                    DateTime dayExpired = DateTime.Parse(building.DayExpired.ToString());

                    listBuilding.Add(new Building
                    {
                        Id = building.Id,
                        Name = building.Name,
                        Description = building.Description,
                        Version = building.Version,
                        DayExpired = dayExpired.ToString("MM/dd/yyyy", CultureInfo.InvariantCulture),
                        Active = building.Active
                    });
                }



                result.Add(new Company
                {
                    Id = id,
                    Name = name,
                    ListBuilding = listBuilding
                });
            }


            return result;
        }

        public List<Company> GetAllBuildings()
        {
            var result = new List<Company>();

            foreach (var company in context.Company.ToList())
            {
                string id = company.Id;
                string name = company.Name;
                var listBuilding = new List<Building>();

                foreach (var building in context.Building.Where(x => x.CompanyId == company.Id).ToList())
                {
                    DateTime dayExpired = DateTime.Parse(building.DayExpired.ToString());
                    listBuilding.Add(new Building
                    {
                        Id = building.Id,
                        Name = building.Name,
                        Description = building.Description,
                        Version = building.Version,
                        DayExpired = dayExpired.ToString("MM/dd/yyyy", CultureInfo.InvariantCulture),
                        Active = building.Active
                    });
                }

                result.Add(new Company
                {
                    Id = id,
                    Name = name,
                    ListBuilding = listBuilding
                });
            }


            return result;
        }



        public string UploadFloorMap(IFormFileCollection files, string buildingId)
        {
            foreach (var file in files)
            {
                var fileName = file.Name;

                var floorId = buildingId + "_f_" + fileName;

                var floor = context.Floor.Where(x => x.Id == floorId).FirstOrDefault();

                if (floor != null)
                {
                    // xóa map cũ nếu có
                    deleteMap(floorId);

                    floor.LinkMap = saveMapFromFile(file, floorId);
                    context.SaveChanges();
                }
            }

            var floors = context.Floor.Where(x => x.BuildingId == buildingId).ToList();

            var model = new List<Floor>();
            foreach (var floor in floors)
            {
                model.Add(new Floor
                {
                    Id = floor.Id,
                    Name = floor.Name,
                    LinkMap = floor.LinkMap
                });
            }

            return JsonConvert.SerializeObject(model);
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
                buildingModel.Active = building.Active;

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
                                itemLocation.Orientitation,
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
                    LinkMap = saveMapFromUrl(floor.LinkMap, buildingId + "_f_" + floor.Id)
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
                            Orientitation = neighbor.Orientation,
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


        public string CreateNewBuilding(Company company)
        {
            var building = company.ListBuilding[0];

            var buildingTmp = context.Building.Where(x => x.Id == building.Id).FirstOrDefault();
            if (buildingTmp == null)
            {
                int version = 0;
                if (building.ListFloor != null)
                {
                    if (building.ListFloor.Count() > 0)
                    {
                        version = 1;
                    }
                }
                context.Building.Add(new Models.Building
                {
                    Id = building.Id,
                    Name = building.Name,
                    Description = building.Description,
                    CompanyId = company.Id,
                    DayExpired = Convert.ToDateTime(building.DayExpired),
                    Version = version,
                    Active = building.Active
                });

                // add new building
                context.SaveChanges();

                var listFloor = building.ListFloor;
                var buildingId = building.Id;

                if (listFloor != null)
                {
                    if (listFloor.Count() > 0)
                    {
                        // add floor
                        foreach (var floor in listFloor)
                        {
                            context.Floor.Add(new Models.Floor
                            {
                                BuildingId = buildingId,
                                Id = buildingId + "_f_" + floor.Id,
                                Name = floor.Name,
                                // save map
                                // chưa có Link Map
                            });
                            context.SaveChanges();
                        }

                        //var listLocationBeside = new List<LocationBesideDb>();


                        var listLocationBeside = new List<LocationBesideDb>();


                        // add location
                        foreach (var floor in listFloor)
                        {
                            if (floor.ListLocation != null)
                            {
                                if (floor.ListLocation.Count() > 0)
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


                                        if (location.ListRoom != null)
                                        {
                                            if (location.ListRoom.Count() > 0)
                                            {
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
                                            }
                                        }


                                        if (location.ListLocationBeside != null)
                                        {
                                            if (location.ListLocationBeside.Count() > 0)
                                            {
                                                foreach (var neighbor in location.ListLocationBeside)
                                                {
                                                    listLocationBeside.Add(new LocationBesideDb
                                                    {
                                                        //Id = 0,
                                                        LocationId = buildingId + "_l_" + location.Id,
                                                        LocationBesideId = buildingId + "_l_" + neighbor.Id,
                                                        Orientitation = neighbor.Orientation,
                                                        Distance = neighbor.Distance
                                                    });
                                                }
                                            }
                                        }



                                    }
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
                    }
                }



                return "OK";
            }

            return "Building Id is duplicated";
        }


        public string UpdateBuilding(Building building)
        {
            //var building = company.ListBuilding[0];

            var buildingInfo = context.Building.Where(x => x.Id == building.Id).FirstOrDefault();

            if (buildingInfo != null)
            {
                buildingInfo.Name = building.Name;
                buildingInfo.Description = building.Description;
                //buildingInfo.CompanyId = company.Id;
                buildingInfo.DayExpired = Convert.ToDateTime(building.DayExpired);
                buildingInfo.Active = building.Active;

                int? version = buildingInfo.Version;
                buildingInfo.Version = version + 1;

                if (building.ListFloor != null)
                {
                    if (building.ListFloor.Count() > 0)
                    {
                        // add building data
                        deleteOldDataAndAddNewData(building.Id, building.ListFloor);
                    }
                }

                // save
                context.SaveChanges();
            }

            return "OK";

        }


        public string CreateNewCompany(Company company)
        {
            var comp = context.Company.Where(x => x.Id == company.Id).FirstOrDefault();

            if (comp == null)
            {
                context.Company.Add(new Models.Company
                {
                    Id = company.Id,
                    Name = company.Name
                });

                context.SaveChanges();

                return "OK";
            }

            return "Company ID is duplicated";
        }

        public string UpdateCompany(Company company)
        {
            var comp = context.Company.Where(x => x.Id == company.Id).FirstOrDefault();
            if (comp != null)
            {
                comp.Name = company.Name;

                context.SaveChanges();

                return "OK";
            }

            return "Company ID is not available";
        }

        public string CreateNewFloor(string buildingId, string floorId, string floorName, IFormFile file)
        {
            //var fileName = file.Name;

            floorId = buildingId + "_f_" + floorId;

            var floor = context.Floor.Where(x => x.Id == floorId).FirstOrDefault();

            if (floor == null)
            {




                var linkMap = saveMapFromFile(file, floorId);

                context.Floor.Add(new Models.Floor
                {
                    Id = floorId,
                    Name = floorName,
                    LinkMap = linkMap,
                    BuildingId = buildingId
                });


                context.SaveChanges();

                return linkMap;
            }

            return "Floor ID is duplicated";
        }

        public string UpdateLocationQrAnchorId(string locationId, string qrAnchorId)
        {
            var location = context.Location.Where(x => x.Id == locationId).FirstOrDefault();

            if (location != null)
            {
                location.QranchorId = qrAnchorId;

                context.SaveChanges();

                return "1";
            }

            return "0";
        }

        public string UpdateLocationSpaceAnchorId(string locationId, string spaceAnchorId)
        {
            var location = context.Location.Where(x => x.Id == locationId).FirstOrDefault();

            if (location != null)
            {
                location.SpaceAnchorId = spaceAnchorId;

                context.SaveChanges();

                return "1";
            }

            return "0";
        }

        public string UpdateRoomSpaceAnchorId(string roomId, string spaceAnchorId)
        {
            var room = context.Room.Where(x => x.Id == roomId).FirstOrDefault();

            if (room != null)
            {
                room.SpaceAnchorId = spaceAnchorId;

                context.SaveChanges();

                return "1";
            }

            return "0";
        }




        // ------------------------------------------
        // -------------------------------------------

        private void deleteFloorMap(string buildingId, List<Floor> floors)
        {
            var listFloor = context.Floor.Where(x => x.BuildingId == buildingId).ToList();
            bool tmp = false;

            foreach (var floorDb in listFloor)
            {

                foreach (var floor in floors)
                {
                    if (floorDb.Id == floor.Id)
                    {
                        tmp = true;
                        break;
                    }
                }

                if (!tmp)
                {
                    deleteMap(floorDb.Id);
                }
                else
                {
                    tmp = false;
                }

            }
        }


        private void deleteOldDataAndAddNewData(string buildingId, List<Floor> floors)
        {
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
                deleteFloorMap(buildingId, floors);
                //foreach (var floor in listFloor)
                //{
                //    deleteMap(floor.Id);
                //}


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
                //return e.Data.ToString();
            }

            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // add new data

            // add floor
            foreach (var floor in floors)
            {
                context.Floor.Add(new Models.Floor
                {
                    BuildingId = buildingId,
                    Id = buildingId + "_f_" + floor.Id,
                    Name = floor.Name,
                    // save map
                    LinkMap = floor.LinkMap
                });
                context.SaveChanges();
            }

            //var listLocationBeside = new List<LocationBesideDb>();



            // add location
            foreach (var floor in floors)
            {
                if (floor.ListLocation != null)
                {
                    if (floor.ListLocation.Count() > 0)
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


                            if (location.ListRoom != null)
                            {
                                if (location.ListRoom.Count() > 0)
                                {
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
                                }
                            }


                            if (location.ListLocationBeside != null)
                            {
                                if (location.ListLocationBeside.Count() > 0)
                                {
                                    foreach (var neighbor in location.ListLocationBeside)
                                    {
                                        listLocationBeside.Add(new LocationBesideDb
                                        {
                                            //Id = 0,
                                            LocationId = buildingId + "_l_" + location.Id,
                                            LocationBesideId = buildingId + "_l_" + neighbor.Id,
                                            Orientitation = neighbor.Orientation,
                                            Distance = neighbor.Distance
                                        });
                                    }
                                }
                            }



                        }
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
        }


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

        private string saveMapFromFile(IFormFile file, string fileName)
        {
            var key = fileName + ".png";

            //string fileName = "";
            string folderName = "Map";
            string webRootPath = hostingEnvironment.WebRootPath;
            string newPath = Path.Combine(webRootPath, folderName);
            if (!Directory.Exists(newPath))
            {
                Directory.CreateDirectory(newPath);
            }
            if (file.Length > 0)
            {

                string fullPath = Path.Combine(newPath, key);
                using (var stream = new FileStream(fullPath, FileMode.Create))
                {
                    file.CopyTo(stream);
                }
                return "http://13.229.117.90:7070/Map/" + key;
                //context.SaveChanges();
            }
            return "Nothing";
        }

        private string saveMapFromUrl(string linkMap, string nameMap)
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

        private void changeBuildingStatus(string buildingId, int status)
        {
            var building = context.Building.Where(x => x.Id == buildingId).FirstOrDefault();
            if (building != null)
            {
                if (status == ACTIVE)
                {
                    building.Active = true;
                }
                else if (status == DEACTIVE)
                {
                    building.Active = false;
                }

                context.SaveChanges();
            }
        }


    }
}
