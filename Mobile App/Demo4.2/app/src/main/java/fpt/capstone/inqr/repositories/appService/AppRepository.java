package fpt.capstone.inqr.repositories.appService;

import android.content.Context;

import fpt.capstone.inqr.callbacks.CallbackData;
import fpt.capstone.inqr.model.Building;
import fpt.capstone.inqr.model.Company;

import java.util.List;

public interface AppRepository {
    void getAllLocation(Context context, CallbackData<Building> callbackData, String buildingId);

    void getAllBuilding(Context context, CallbackData<List<Company>> callbackData);
}
