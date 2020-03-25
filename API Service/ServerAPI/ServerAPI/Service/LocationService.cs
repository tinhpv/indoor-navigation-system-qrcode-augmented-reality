using System.Collections.Generic;
using Microsoft.AspNetCore.Http;
using ServerAPI.IRepository;
using ServerAPI.IService;
using ServerAPI.Models.DefaultModel;

namespace ServerAPI.Service
{
    public class LocationService : ILocationService
    {
        private readonly ILocationRepository iRepository;

        public LocationService(ILocationRepository _iRepository)
        {
            this.iRepository = _iRepository;
        }

        public Building GetLocations(string buildingId)
        {
            return iRepository.GetLocations(buildingId);
        }

        public string UpdateDataBuilding(IFormFile file)
        {
            return iRepository.UpdateDataBuilding(file);
        }
    }
}
