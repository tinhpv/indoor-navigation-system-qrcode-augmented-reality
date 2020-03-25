using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using ServerAPI.IRepository;
using ServerAPI.Models.DefaultModel;

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

        public string UploadMap(IFormFile file)
        {
            var key = file.FileName;

            //string fileName = "";
            string folderName = "MapTest";
            string webRootPath = hostingEnvironment.WebRootPath;
            string newPath = Path.Combine(webRootPath, folderName);
            if (!Directory.Exists(newPath))
            {
                Directory.CreateDirectory(newPath);
            }
            if (file.Length > 0)
            {
                //fileName = ContentDispositionHeaderValue.Parse(file.ContentDisposition).FileName.Trim('"');
                //fileName = "Image_AssetId_" + id + ".png";
                string fullPath = Path.Combine(newPath, key);
                using (var stream = new FileStream(fullPath, FileMode.Create))
                {
                    file.CopyTo(stream);
                }

                //string name = "Image_AssetId_1.png"

                //var asset = context.Asset.Where(x => x.Id == id).FirstOrDefault();
                return "http://13.229.117.90:7070/MapTest/" + key;
                //context.SaveChanges();
            }
            return "Nothing";
        }
    }
}
