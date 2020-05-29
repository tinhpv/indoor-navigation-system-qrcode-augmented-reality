package fpt.capstone.inqr.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.adapter.BuildingAdapter;
import fpt.capstone.inqr.dialog.ChangeWalkingSpeedDialog;
import fpt.capstone.inqr.dialog.DeleteDialog;
import fpt.capstone.inqr.dialog.InfoDialog;
import fpt.capstone.inqr.dialog.NotificationDialog;
import fpt.capstone.inqr.dialog.WarningDialog;
import fpt.capstone.inqr.dialog.WarningDownloadDialog;
import fpt.capstone.inqr.helper.DatabaseHelper;
import fpt.capstone.inqr.helper.FileHelper;
import fpt.capstone.inqr.model.Building;
import fpt.capstone.inqr.model.Company;
import fpt.capstone.inqr.model.Floor;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Neighbor;
import fpt.capstone.inqr.model.Room;
import fpt.capstone.inqr.model.supportModel.Notification;
import fpt.capstone.inqr.presenter.ListBuildingPresenter;
import fpt.capstone.inqr.view.ListBuildingView;
import fpt.capstone.inqr.viewmodel.BuildingViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListBuildingFragment extends BaseFragment implements ListBuildingView {

    View view;
    private DatabaseHelper db;
    private List<Building> listBuilding;
    private BuildingViewModel buildingViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvBuilding;
    private BuildingAdapter adapter;

    private EditText edtSearch;
    private ImageView imgSetting;

    private List<Notification> listNotification;
    private ListBuildingPresenter mBuildingPresenter;

    public ListBuildingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void registerLiveDataListener() {
        buildingViewModel = ViewModelProviders.of(this).get(BuildingViewModel.class);
        buildingViewModel.getBuildingData()
                .observe(this, buildings -> adapter.setListBuilding(buildings));
    }

    public void askDeleteBuildingData(String buildingId, String buildingName) {
        DeleteDialog deleteDialog = new DeleteDialog(this, buildingId, buildingName);
        deleteDialog.show(getChildFragmentManager(), "delete");
    }

    public void deleteBuildingData(String buildingId) {
        mBuildingPresenter.deleteBuildingData(buildingId);

        // delete map of building
        FileHelper.deleteOldData(getContext(), buildingId);

        updateListBuilding();
    }


    public void updateBuildingData(String buildingId, int position) {
        // xóa data cũ
        mBuildingPresenter.deleteBuildingData(buildingId);
        // thêm data mới
        downloadBuildingData(buildingId, position);
    }

    public void showInfo(Building building) {
        InfoDialog infoDialog =
                new InfoDialog(building.getName(),
                        building.getDescription() + "\n\n" + "Day Expired: " + building.getDayExpired());
        infoDialog.show(getChildFragmentManager(), "info");
    }

    public void showWarningDownload(Building building, int type, int position) {
        WarningDownloadDialog dialog = new WarningDownloadDialog(this, type, building, position);
        dialog.show(getChildFragmentManager(), "warning_download");
    }

    public void showWarning(String warning) {
        WarningDialog dialog = new WarningDialog("Warning", warning);
        dialog.show(getChildFragmentManager(), "warning");
    }

    @Override
    public void onSuccessGetAllLocationsServer(Building building) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                List<Floor> floors = building.getListFloor();

                for (Floor floor : floors) {
                    // add floor
                    mBuildingPresenter.addFloor(floor, building.getId());
                    //save map image
                    FileHelper.saveFileFromUrl(getContext(), building.getId(), FileHelper.TYPE_MAP, floor.getLinkMap());

                    //add location
                    for (Location location : floor.getLocationList()) {
                        mBuildingPresenter.addLocation(location, floor.getId());

                        // add neighbor
                        if (location.getNeighborList() != null) {
                            if (!location.getNeighborList().isEmpty()) {
                                for (Neighbor neighbor : location.getNeighborList()) {
                                    mBuildingPresenter.addNeighbor(location.getId(), neighbor);
                                }
                            }
                        }

                        // add room
                        if (location.getListRoom() != null) {
                            if (!location.getListRoom().isEmpty()) {
                                for (Room room : location.getListRoom()) {
                                    mBuildingPresenter.addRoom(room, location.getId(), floor.getId());
                                }
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // update building status
                mBuildingPresenter.updateBuildingStatus(building.getId(), Building.DOWNLOADED);
                updateListBuilding();
                mBuildingPresenter.closeDB();
                removeLoadingBar();
                InfoDialog infoDialog = new InfoDialog("Building Downloaded", building.getName());
                infoDialog.show(getChildFragmentManager(), "info");
            }
        }.execute();
    }

    @Override
    public void onFailGetLocationsFromServer() {
        Toast.makeText(getContext(), "There're something wrong!", Toast.LENGTH_SHORT).show();
        removeLoadingBar();
    }

    public void downloadBuildingData(String buildingId, int position) {
        adapter.setPosition(position);
        showLoadingBar();
        mBuildingPresenter.getAllLocations(buildingId);
    }

    private void updateListBuilding() {
        this.listBuilding = db.getAllBuildings();

        if (this.listBuilding != null) {
            if (this.listBuilding.size() > 1) {
                Collections.sort(this.listBuilding, (o1, o2) -> {
                    int c = o1.getCompanyName().toLowerCase().compareTo(o2.getCompanyName().toLowerCase());
                    if (c == 0) {
                        c = o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                    }
                    return c;
                });
            }
        }

        buildingViewModel.setBuildingData(this.listBuilding);
    }

    public void showMapFragment(String buildingId) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

        MapFragment fragment = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putString("buildingID", buildingId);
        fragment.setArguments(bundle);

        this.setSupporter(fragment);
        this.changeFragment(fragment, true, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_building_updated, container, false);
        db = new DatabaseHelper(this.getContext());
        mBuildingPresenter = new ListBuildingPresenter(this, getContext());
        mBuildingPresenter.getBuildings();
        return view;
    }

    @Override
    public void onSuccessLoadBuildings(List<Building> buildings) {
        this.listBuilding = buildings;

        setView(view);
        adapter = new BuildingAdapter(this);
        adapter.setListBuilding(listBuilding);

        rvBuilding.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        rvBuilding.setAdapter(adapter);

        registerLiveDataListener();
        // search
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                buildingViewModel.setBuildingData(searchBuilding(edtSearch.getText().toString()));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> checkDataOnServer());

        // setting
        imgSetting.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            // set layout
            popupMenu.inflate(R.menu.popup_menu_setting);
            // set sự kiện chọn menu
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.itemChangeSpeed:
                        ChangeWalkingSpeedDialog dialog = new ChangeWalkingSpeedDialog();
                        dialog.show(getChildFragmentManager(), "walking_speed");
                        return true;
                    default:
                        return false;
                }

            });

            popupMenu.show();
        });

        // chạy ngay lần đầu mở app luôn
        checkDataOnServer();
    }

    private void setView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        rvBuilding = view.findViewById(R.id.rvBuilding);
        edtSearch = view.findViewById(R.id.edtSearch);
        imgSetting = view.findViewById(R.id.imgSetting);
    }

    private List<Building> searchBuilding(String searchKey) {
        if (searchKey.isEmpty()) {
            return this.listBuilding;
        } else {
            List<Building> result = new ArrayList<>();

            for (Building building : this.listBuilding) {
                if (building.getName().toLowerCase().contains(searchKey.toLowerCase()) || building.getCompanyName().toLowerCase().contains(searchKey.toLowerCase())) {
                    result.add(building);
                }
            }

            return result;
        }
    }

    private boolean checkBuildingActive(List<Building> listBuildingServer, String buildingLocalId) {
        for (Building buildingServer : listBuildingServer) {
            if (buildingLocalId.equals(buildingServer.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSuccessGetAllBuildingsServer(List<Company> companyList) {
        if (listNotification == null) {
            listNotification = new ArrayList<>();
        } else {
            listNotification.clear();
        }

        List<Building> listBuildingServer = new ArrayList<>();
        for (Company company : companyList) {

            // check nếu building k còn active trên server (json không trả về nữa) thì xóa data dưới local
            listBuildingServer.addAll(company.getListBuilding());

            for (Building building : company.getListBuilding()) {
                // check nếu building k còn active trên server (json không trả về nữa) thì xóa data dưới local
                // check đã lưu db chưa
                int status = checkBuildingExist(building.getId(), building.getVersion());
                // nếu server có thêm building thì lưu thêm vào db
                if (status == Building.NOT_EXIST) {
                    building.setCompanyName(company.getName());
                    db.addBuilding(building);
                    // thêm thông tin cập nhập
                    listNotification.add(new Notification(Notification.TYPE_ADD, building.getName()));
                } else if (status == Building.UPDATE_DATA) {
                    // update data building
                    building.setCompanyName(company.getName());
                    db.updateBuilding(building);
                    // thêm thông tin cập nhập
                    listNotification.add(new Notification(Notification.TYPE_UPDATE, building.getName()));
                } else if (status == Building.EXISTED) {
                    // update building information
                    building.setCompanyName(company.getName());
                    db.updateBuildingInformation(building);
                }
            }
        }

        // check từng building local xem có trên server k?
        for (Building buildingLocal : listBuilding) {
            // nếu không có thì xóa data của building đó
            if (!checkBuildingActive(listBuildingServer, buildingLocal.getId())) {
                db.deleteAllBuilding(getContext(), buildingLocal.getId());
                // thêm thông tin cập nhập
                listNotification.add(new Notification(Notification.TYPE_REMOVE, buildingLocal.getName()));
            }
        }

        updateListBuilding();
        removeLoadingBar();
        swipeRefreshLayout.setRefreshing(false);

        if (listNotification.size() > 0) {
            NotificationDialog dialog = new NotificationDialog(listNotification);
            dialog.show(getChildFragmentManager(), "notification");
        }
    }

    @Override
    public void onFailGetBuildingsFromServer() {
        removeLoadingBar();
        Toast.makeText(getContext(), "There something wrong! Please check!", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void checkDataOnServer() {
        showLoadingBar();
        mBuildingPresenter.getAllBuildingsFromServer();
    }

    private int checkBuildingExist(String buildingId, int version) {
        for (Building building : listBuilding) {
            if (buildingId.equals(building.getId())) {
                if (version > building.getVersion()) {
                    // nếu có version mới và đã download thì cập nhập trạng thái update
                    if (building.getStatus() == Building.DOWNLOADED) {
                        return Building.UPDATE_DATA;
                    } else if (building.getStatus() == Building.NOT_DOWNLOAD) {
                        return Building.EXISTED;
                    }
                } else {
                    return Building.EXISTED;
                }
            } else {

            }
        }

        return Building.NOT_EXIST;
    }


}
