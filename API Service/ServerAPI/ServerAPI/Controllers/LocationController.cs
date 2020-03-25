using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using ServerAPI.IService;
using ServerAPI.Models;
using ServerAPI.Models.DefaultModel;

namespace ServerAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class LocationController : Controller
    {
        private ILocationService iService;

        public LocationController(ILocationService _iService)
        {
            iService = _iService;
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
            } else
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


    }
}
