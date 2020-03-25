using System;
namespace ServerAPI.Models.DefaultModel
{
    public class Model<T>
    {
        //public string Company { get; set; }
        //public string Building { get; set; }
        //public int? Version { get; set; }
        public int Status { get; set; }
        public T Data { get; set; }
        //public string DayExpired { get; set; }

        public Model()
        {
        }

        public Model(int status, T data)
        {
            Status = status;
            Data = data;
        }

        //public Model(int? version, int status, T data, string dayExpired)
        //{
        //    Version = version;
        //    Status = status;
        //    Data = data;
        //    DayExpired = dayExpired;
        //}


    }
}
