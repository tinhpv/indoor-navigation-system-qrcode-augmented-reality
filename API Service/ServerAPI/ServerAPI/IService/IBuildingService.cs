using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Http;
using ServerAPI.Models.DefaultModel;

namespace ServerAPI.IService
{
    public interface IBuildingService
    {
        List<Company> GetAllBuildingsActive();
        List<Company> GetAllBuildings();


        string UploadFloorMap(IFormFileCollection files, string buildingId);
        string CreateNewFloor(string buildingId, string floorId, string floorName, IFormFile file);


        string CreateNewCompany(Company company);
        string UpdateCompany(Company company);





        Building GetLocations(string buildingId);
        string UpdateDataBuilding(IFormFile file);



        string CreateNewBuilding(Company company);

        string UpdateBuilding(Building company);



        string UpdateLocationQrAnchorId(string locationId, string qrAnchorId);
        string UpdateLocationSpaceAnchorId(string locationId, string spaceAnchorId);
        string UpdateRoomSpaceAnchorId(string roomId, string spaceAnchorId);


        string UpdateBuildingVersion(string buildingId);
    }
}
