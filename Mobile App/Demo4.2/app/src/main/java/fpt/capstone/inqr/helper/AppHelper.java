package fpt.capstone.inqr.helper;

import android.content.Context;

import fpt.capstone.inqr.callbacks.CallbackData;
import fpt.capstone.inqr.model.Building;
import fpt.capstone.inqr.model.Company;
import fpt.capstone.inqr.repositories.appService.AppRepository;
import fpt.capstone.inqr.repositories.appService.AppRepositoryImpl;

import java.util.List;

public class AppHelper {

    private static AppHelper appHelper = null;

    private Context context;
    private AppRepository appRepository;

    public AppHelper(Context context) {
        this.context = context;
    }

    public static AppHelper getInstance(Context context) {
        if (appHelper == null) {
            appHelper = new AppHelper(context);
        }
        return appHelper;
    }

    public void getAllLocation(String buildingId, CallbackData<Building> callbackData) {
        appRepository = new AppRepositoryImpl();
        appRepository.getAllLocation(context, callbackData, buildingId);
    }

    public  void getAllBuilding (CallbackData<List<Company>> callbackData) {
        appRepository = new AppRepositoryImpl();
        appRepository.getAllBuilding(context, callbackData);
    }
}
