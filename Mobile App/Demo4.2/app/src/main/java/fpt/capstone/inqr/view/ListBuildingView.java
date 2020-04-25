package fpt.capstone.inqr.view;

import java.util.List;

import fpt.capstone.inqr.model.Building;
import fpt.capstone.inqr.model.Company;

/**
 * Demo4
 * Created by TinhPV on 2020-04-25
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public interface ListBuildingView {
    void onSuccessLoadBuildings(List<Building> buildings);
    void onSuccessGetAllBuildingsServer(List<Company> companyList);
    void onFailGetBuildingsFromServer();
    void onSuccessGetAllLocationsServer(Building building);
    void onFailGetLocationsFromServer();
}
