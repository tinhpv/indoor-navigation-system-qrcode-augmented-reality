package fpt.capstone.inqr.repositories.appService;

import android.content.Context;

import fpt.capstone.inqr.callbacks.CallbackData;
import fpt.capstone.inqr.model.Building;
import fpt.capstone.inqr.model.Company;
import fpt.capstone.inqr.model.supportModel.JsonModel;
import fpt.capstone.inqr.repositories.api.ClientApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepositoryImpl implements AppRepository {

    @Override
    public void getAllLocation(Context context, final CallbackData<Building> callbackData, String buildingId) {
        ClientApi clientApi = new ClientApi();
        Call<ResponseBody> serviceCall = clientApi.getAppService().getAllLocation(buildingId);
        serviceCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        try {
                            String result = response.body().string();
                            Type type = new TypeToken<JsonModel<Building>>() {
                            }.getType();
                            JsonModel<Building> model = new Gson().fromJson(result, type);

                            // check status
                            if (model.getStatus() == 1) {
                                if (model.getData() != null) {
                                    Building building = model.getData();

                                    // return
                                    callbackData.onSuccess(building);
                                } else {
                                    callbackData.onFail("Error: No data");
                                }
                            } else {
                                callbackData.onFail("Error: Server problem");
                            }
                        } catch (IOException e) {
                            callbackData.onFail("Error: " + e.getMessage());
                        }
                    }
                } else {
                    callbackData.onFail("Error on response!!!");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callbackData.onFail("Error: " + t.getMessage());
            }
        });
    }

    @Override
    public void getAllBuilding(Context context, CallbackData<List<Company>> callbackData) {
        ClientApi clientApi = new ClientApi();
        Call<ResponseBody> serviceCall = clientApi.getAppService().getAllBuilding();
        serviceCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        try {
                            String result = response.body().string();
                            Type type = new TypeToken<JsonModel<List<Company>>>() {
                            }.getType();
                            JsonModel<List<Company>> model = new Gson().fromJson(result, type);

                            // check status
                            if (model.getStatus() == 1) {
                                if (model.getData() != null) {
                                    List<Company> listCompany = model.getData();

                                    // return
                                    callbackData.onSuccess(listCompany);
                                } else {
                                    callbackData.onFail("Error: No data");
                                }
                            } else {
                                callbackData.onFail("Error: Server problem");
                            }
                        } catch (IOException e) {
                            callbackData.onFail("Error: " + e.getMessage());
                        }
                    }
                } else {
                    callbackData.onFail("Error on response!!!");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callbackData.onFail("Error: " + t.getMessage());
            }
        });
    }
}
