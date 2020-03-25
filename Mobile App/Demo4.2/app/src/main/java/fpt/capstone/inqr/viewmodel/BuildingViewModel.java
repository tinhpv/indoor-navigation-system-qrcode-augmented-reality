package fpt.capstone.inqr.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import fpt.capstone.inqr.model.Building;

import java.util.List;

public class BuildingViewModel extends AndroidViewModel {

    private MutableLiveData<List<Building>> buildingData = new MutableLiveData<>();

    public BuildingViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<Building>> getBuildingData() {
        return buildingData;
    }

    public void setBuildingData(List<Building> listBuilding) {
        this.buildingData.setValue(listBuilding);
    }
}
