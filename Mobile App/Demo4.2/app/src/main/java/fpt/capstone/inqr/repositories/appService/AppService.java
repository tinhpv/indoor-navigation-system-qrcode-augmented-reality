package fpt.capstone.inqr.repositories.appService;

import fpt.capstone.inqr.repositories.api.ConfigApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

//TODO: Code here
public interface AppService {

    @GET(ConfigApi.Api.GET_ALL_LOCATION)
    Call<ResponseBody> getAllLocation(@Query("buildingId") String buildingId);

    @GET(ConfigApi.Api.GET_ALL_BUILDING)
    Call<ResponseBody> getAllBuilding();

}
