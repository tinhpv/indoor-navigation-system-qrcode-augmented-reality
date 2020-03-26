using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Http;
using ServerAPI.Models.DefaultModel;

namespace ServerAPI.IRepository
{
    public interface IBuildingRepository
    {
        List<Company> GetAllBuildingsActive();
        List<Company> GetAllBuildings();
       

        string UploadMap(IFormFile file);
    }
}
