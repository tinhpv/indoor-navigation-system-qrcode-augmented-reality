using System.Collections.Generic;
using Microsoft.AspNetCore.Http;
using ServerAPI.Models.DefaultModel;

namespace ServerAPI.IRepository
{
    public interface ILocationRepository
    {
        Building GetLocations(string buildingId);
        string UpdateDataBuilding(IFormFile file);
      }
}
