using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Http;
using ServerAPI.IRepository;
using ServerAPI.IService;
using ServerAPI.Models.DefaultModel;

namespace ServerAPI.Service
{
    public class BuildingService : IBuildingService
    {
        private readonly IBuildingRepository iRepository;

        public BuildingService(IBuildingRepository _iRepository)
        {
            this.iRepository = _iRepository;
        }

        
        public List<Company> GetAllBuildings()
        {
            return iRepository.GetAllBuildings();
        }

        public List<Company> GetAllBuildingsActive()
        {
            return iRepository.GetAllBuildingsActive();
        }

        public string UploadMap(IFormFile file)
        {
            return iRepository.UploadMap(file);
        }
    }
}
