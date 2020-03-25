using System.Collections.Generic;
using System.IO;
using Microsoft.AspNetCore.Http;
using ServerAPI.Models.DefaultModel;

namespace ServerAPI.IService
{
    public interface ILocationService
    {
        Building GetLocations(string buildingId);
        string UpdateDataBuilding(IFormFile file);
    }
}
