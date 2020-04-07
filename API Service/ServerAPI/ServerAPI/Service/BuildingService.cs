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

        public string UploadFloorMap(IFormFileCollection files, string buildingId)
        {
            return iRepository.UploadFloorMap(files, buildingId);
        }

        public Building GetLocations(string buildingId)
        {
            return iRepository.GetLocations(buildingId);
        }

        public string UpdateDataBuilding(IFormFile file)
        {
            return iRepository.UpdateDataBuilding(file);
        }

        public string CreateNewBuilding(Company company)
        {
            return iRepository.CreateNewBuilding(company);
        }

        public string UpdateBuilding(Building building)
        {
            return iRepository.UpdateBuilding(building);
        }

        public string CreateNewCompany(Company company)
        {
            return iRepository.CreateNewCompany(company);
        }

        public string UpdateCompany(Company company)
        {
            return iRepository.UpdateCompany(company);
        }

        public string CreateNewFloor(string buildingId, string floorId, string floorName, IFormFile file)
        {
            return iRepository.CreateNewFloor(buildingId, floorId, floorName, file);
        }
    }
}
