package fpt.capstone.inqr.repositories.api;

public class ConfigApi {
//    http://13.229.117.90:7070/api/location/getalllocations
    public static final String BASE_URL = "http://13.229.117.90:7070/";

    public interface Api {
        String GET_ALL_LOCATION = "api/inqr/getalllocations";
        String GET_ALL_BUILDING = "api/inqr/getAllBuildingsActive";
    }
}