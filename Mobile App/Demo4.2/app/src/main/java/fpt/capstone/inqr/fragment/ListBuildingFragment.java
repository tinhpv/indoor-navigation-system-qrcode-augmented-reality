package fpt.capstone.inqr.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;
import fpt.capstone.inqr.R;
import fpt.capstone.inqr.adapter.BuildingAdapter;
import fpt.capstone.inqr.callbacks.CallbackData;
import fpt.capstone.inqr.dialog.DeleteDialog;
import fpt.capstone.inqr.dialog.InfoDialog;
import fpt.capstone.inqr.dialog.NotificationDialog;
import fpt.capstone.inqr.dialog.WarningDialog;
import fpt.capstone.inqr.dialog.WarningDownloadDialog;
import fpt.capstone.inqr.helper.AppHelper;
import fpt.capstone.inqr.helper.DatabaseHelper;
import fpt.capstone.inqr.helper.FileHelper;
import fpt.capstone.inqr.model.Building;
import fpt.capstone.inqr.model.Company;
import fpt.capstone.inqr.model.Floor;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Neighbor;
import fpt.capstone.inqr.model.Room;
import fpt.capstone.inqr.model.supportModel.Notification;
import fpt.capstone.inqr.viewmodel.BuildingViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListBuildingFragment extends BaseFragment {

    private DatabaseHelper db;
    private List<Building> listBuilding;
    private BuildingViewModel buildingViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvBuilding;
    private BuildingAdapter adapter;

    private EditText edtSearch;

    private List<Notification> listNotification;

    public ListBuildingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHelper(this.getContext());

        if (checkExistedDb(getContext())) {
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
        } else {
            listBuilding = new ArrayList<>();
        }

        buildingViewModel = ViewModelProviders.of(this).get(BuildingViewModel.class);
    }

    private void registerLiveDataListener() {
        buildingViewModel.getBuildingData().observe(this, buildings -> {
//            listBuilding = buildings;
            adapter.setListBuilding(buildings);
        });
    }

    public void askDeleteBuildingData(String buildingId, String buildingName) {
//        new AlertDialog.Builder(this.getContext())
//                .setTitle("Cảnh báo")
//                .setMessage("Bạn có muốn xóa dữ liệu của '" + buildingName + "' không?")
//                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
//                    db.deleteBuildingData(buildingId);
//                    updateListBuilding();
//                })
//                .setNegativeButton(android.R.string.no, null)
//                .setCancelable(false)
//                .show();
        DeleteDialog deleteDialog = new DeleteDialog(this, buildingId, buildingName);

        deleteDialog.show(getChildFragmentManager(), "delete");
    }

    public void deleteBuildingData(String buildingId) {
        db.deleteBuildingData(buildingId);
        updateListBuilding();
    }


    public void updateBuildingData(String buildingId, int position) {
        // xóa data cũ
        db.deleteBuildingData(buildingId);
        // thêm data mới
        downloadBuildingData(buildingId, position);
    }

    public void showInfo(Building building) {
        InfoDialog infoDialog = new InfoDialog(building.getName(), building.getDescription() + "\n\n" + "Day Expired: " + building.getDayExpired());

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

    public void downloadBuildingData(String buildingId, int position) {
        adapter.setPosition(position);

        showLoadingBar();

        AppHelper helper = AppHelper.getInstance(this.getContext());
        helper.getAllLocation(buildingId, new CallbackData<Building>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onSuccess(final Building building) {
//                Gson gson = new Gson();
//                String json = gson.toJson(locations);
//
//                txtDbStatus.setText(json);


                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        List<Floor> floors = building.getListFloor();

                        for (Floor floor : floors) {
//                    txtDbStatus.setText("Loading... Add LocationID: " + location.getId());

                            // add floor

                            db.addFloor(floor, buildingId);

                            //save map image
                            FileHelper.saveFileFromUrl(getContext(), FileHelper.TYPE_MAP, floor.getLinkMap());

                            //add location
                            for (Location location : floor.getLocationList()) {
                                db.addLocation(location, floor.getId());

                                // save qr code
//                                FileHelper.saveFileFromUrl(getApplicationContext(), FileHelper.TYPE_QR, location.getLinkQr());

                                // add neighbor
                                if (location.getNeighborList() != null) {
                                    if (!location.getNeighborList().isEmpty()) {
                                        for (Neighbor neighbor : location.getNeighborList()) {
                                            db.addNeighbor(location.getId(), neighbor);
                                        }
                                    }
                                }

                                // add room
                                if (location.getListRoom() != null) {
                                    if (!location.getListRoom().isEmpty()) {
                                        for (Room room : location.getListRoom()) {
                                            db.addRoom(room, location.getId(), floor.getId());
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
                        db.updateBuildingStatus(buildingId, Building.DOWNLOADED);

                        updateListBuilding();

                        db.closeDB();
//        txtDbStatus.setText("Đã tạo Database");
                        removeLoadingBar();

                        InfoDialog infoDialog = new InfoDialog("Building Updated", building.getName());
                        infoDialog.show(getChildFragmentManager(), "info");
                    }
                }.execute();


            }

            @Override
            public void onFail(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                System.out.println("hieu => " + message);
                removeLoadingBar();
            }
        });
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
        MapFragment fragment = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putString("buildingID", buildingId);
        fragment.setArguments(bundle);
        this.changeFragment(fragment, true, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_building, container, false);

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

        // chạy ngay lần đầu mở app luôn
        checkDataOnServer();

        return view;
    }

    private void setView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        rvBuilding = view.findViewById(R.id.rvBuilding);
        edtSearch = view.findViewById(R.id.edtSearch);
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

    private void checkDataOnServer() {
        showLoadingBar();

        AppHelper appHelper = AppHelper.getInstance(this.getContext());
        appHelper.getAllBuilding(new CallbackData<List<Company>>() {
            @Override
            public void onSuccess(List<Company> companies) {

                if (listNotification == null) {
                    listNotification = new ArrayList<>();
                } else {
                    listNotification.clear();
                }

                List<Building> listBuildingServer = new ArrayList<>();

                for (Company company : companies) {
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
                            // do nothing
                        }
                    }
                }

                // check từng building local xem có trên server k?
                for (Building buildingLocal : listBuilding) {
                    // nếu không có thì xóa data của building đó
                    if (!checkBuildingActive(listBuildingServer, buildingLocal.getId())) {
                        db.deleteAllBuilding(buildingLocal.getId());
                        // thêm thông tin cập nhập
                        listNotification.add(new Notification(Notification.TYPE_REMOVE, buildingLocal.getName()));
                    }
                }

                updateListBuilding();

                removeLoadingBar();
//                Toasty.success(getContext(), "Updated successfully", Toasty.LENGTH_SHORT).show();

                swipeRefreshLayout.setRefreshing(false);


                if (listNotification.size() > 0) {
                    NotificationDialog dialog = new NotificationDialog(listNotification);
                    dialog.show(getChildFragmentManager(), "notification");
                }
            }

            @Override
            public void onFail(String message) {
                removeLoadingBar();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int checkBuildingExist(String buildingId, int version) {
        for (Building building : listBuilding) {
            if (buildingId.equals(building.getId())) {
                if (version > building.getVersion()) {
                    // nếu có version mới và đã download thì cập nhập trạng thái update
                    if (building.getStatus() == Building.DOWNLOADED) {
                        return Building.UPDATE_DATA;
                    } else  if (building.getStatus() == Building.NOT_DOWNLOAD) {
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

    private boolean checkExistedDb(Context context) {
        File dbFile = context.getDatabasePath("Capstone");
        return dbFile.exists();
    }
}
