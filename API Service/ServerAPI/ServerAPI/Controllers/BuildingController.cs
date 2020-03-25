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
    public class BuildingController : Controller
    {
        private IBuildingService iService;

        public BuildingController(IBuildingService _iService)
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
        public IActionResult UploadFloorMap()
        {
            if (Request.Form.Files.Count > 0)
            {
                var file = Request.Form.Files[0];
                //var model = new JsonInputModel();
                //model.BuildingId = "2";
                return Ok(iService.UploadMap(file));
            }

            return Ok("Fail");
        }
    }
}
