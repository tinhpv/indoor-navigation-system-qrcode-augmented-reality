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
        string UploadMap(IFormFile file);
    }
}
