using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using ServerAPI.IService;
using ServerAPI.Models.DefaultModel;

namespace ServerAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class INQRController : Controller
    {
        private IBuildingService iService;

        public INQRController(IBuildingService _iService)
        {
            iService = _iService;
        }

        [HttpGet("getAllBuildings")]
        public IActionResult GetAllBuildings()
        {
            var data = iService.GetAllBuildings();

            var model = new Model<List<Company>>();

            if (data != null)
            {
                //if (model.Data.Count > 0)
                //{
                model.Status = 1;
                model.Data = data;
                //}

                //model.DayExpired = "21/12/2020";
            }
            else
            {
                model.Status = 0;
                model.Data = null;
            }


            string json = JsonConvert.SerializeObject(model);
            return Ok(json);
        }

        [HttpGet("getAllBuildingsActive")]
        public IActionResult GetAllBuildingsActive()
        {
            var data = iService.GetAllBuildingsActive();

            var model = new Model<List<Company>>();

            if (data != null)
            {
                //if (model.Data.Count > 0)
                //{
                model.Status = 1;
                model.Data = data;
                //}

                //model.DayExpired = "21/12/2020";
            }
            else
            {
                model.Status = 0;
                model.Data = null;
            }


            string json = JsonConvert.SerializeObject(model);
            return Ok(json);
        }

        [HttpPost("uploadFloorMap")]
        public IActionResult UploadFloorMap([FromForm] string buildingId)
        {
            if (Request.Form.Files.Count > 0)
            {
                var files = Request.Form.Files;
                //var model = new JsonInputModel();
                //model.BuildingId = "2";
                return Ok(iService.UploadFloorMap(files, buildingId));
            }

            return Ok("Fail");
        }

        [HttpGet("getAllLocations")]
        public IActionResult Get([FromQuery] string buildingId)
        {
            var data = iService.GetLocations(buildingId);

            var model = new Model<Models.DefaultModel.Building>();

            if (data != null)
            {
                //if (model.Data.Count > 0)
                //{
                model.Status = 1;
                model.Data = data;
                //}

                //model.DayExpired = "21/12/2020";
            }
            else
            {
                model.Status = 0;
                model.Data = null;
            }


            string json = JsonConvert.SerializeObject(model);
            return Ok(json);
        }

        [HttpPost("updateData")]
        public IActionResult UpdateData()
        {
            //var model = iService.GetLocations();

            //if (model.Data.Count > 0)
            //{
            //    model.Status = 1;
            //    //model.DayExpired = "21/12/2020";
            //}
            //else
            //{
            //    model.Status = 0;
            //    model.DayExpired = null;
            //}

            //string json = JsonConvert.SerializeObject(model);
            if (Request.Form.Files.Count > 0)
            {
                var file = Request.Form.Files[0];
                //var model = new JsonInputModel();
                //model.BuildingId = "2";
                return Ok(iService.UpdateDataBuilding(file));
            }

            return Ok("Fail");
        }

        [HttpPost("createNewBuilding")]
        public IActionResult CreateBuilding([FromBody] Company company)
        { 
            return Ok(iService.CreateNewBuilding(company));
        }

        [HttpPost("updateBuilding")]
        public IActionResult UpdateBuilding([FromBody] Building building)
        {
            return Ok(iService.UpdateBuilding(building));
        }

        [HttpPost("createNewCompany")]
        public IActionResult CreateCompany([FromBody] Company company)
        {
            return Ok(iService.CreateNewCompany(company));
        }

        [HttpPost("updateCompany")]
        public IActionResult UpdateCompany([FromBody] Company company)
        {
            return Ok(iService.UpdateCompany(company));
        }
    }
}
