//package com.example.inqr.activity;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.inqr.R;
//import com.example.inqr.callbacks.CallbackData;
//import com.example.inqr.helper.AppHelper;
//import com.example.inqr.helper.DatabaseHelper;
//import com.example.inqr.helper.FileHelper;
//import com.example.inqr.model.Building;
//import com.example.inqr.model.Company;
//import com.example.inqr.model.Floor;
//import com.example.inqr.model.Location;
//import com.example.inqr.model.Neighbor;
//import com.example.inqr.model.Room;
//
//import java.io.File;
//import java.util.List;
//
//public class MainActivity extends AppCompatActivity {
//    private DatabaseHelper db;
//
//    private TextView txtDbStatus;
//    private Button btnAddDatabase;
//    private Button btnClearDatabase;
//    private Button btnShowMap;
//
//    private List<Building> listBuilding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        txtDbStatus = findViewById(R.id.txtDatabaseStatus);
//        btnAddDatabase = findViewById(R.id.btnAddDatabase);
//        btnClearDatabase = findViewById(R.id.btnClearDatabase);
//        btnShowMap = findViewById(R.id.btnShowMap);
//
//
//        db = new DatabaseHelper(getApplicationContext());
//
////        listBuilding = db.getAllBuildings();
//
//
//        updateStatus();
////
////        db.closeDB();
//    }
//
//    //Kiểm tra nếu database tồn tại
//    private boolean checkExistedDb(Context context) {
//        File dbFile = context.getDatabasePath("Capstone");
//        return dbFile.exists();
//    }
//
//    private void updateStatus() {
//        if (checkExistedDb(this)) {
//            if (db.getAllLocations().size() > 0) {
//                txtDbStatus.setText("Database Exists.!!!!");
////            txtLocations.setText("Total Locations: " + db.getAllLocations().size());
////            txtNeighbour.setText("Total Neighbours: " + db.getAllNeighbors().size());
//                btnAddDatabase.setEnabled(false);
//                btnClearDatabase.setEnabled(true);
//                btnShowMap.setEnabled(true);
//            }
//        } else {
//            txtDbStatus.setText("Database Doesn't Exist!");
//
//            btnAddDatabase.setEnabled(true);
//            btnClearDatabase.setEnabled(false);
//            btnShowMap.setEnabled(false);
//        }
//    }
//
//    //Xóa Database
//    public void clearDatabase(View view) {
//        File dbFile = this.getDatabasePath("Capstone");
//        if (dbFile.delete()) {
////            txtDbStatus.setText("Xóa thành công db, reset app!");
//            updateStatus();
//        }
//    }
//
//    //Tạo Database
//    public void createDatabase(final View view) {
//
//
//        AppHelper helper = AppHelper.getInstance(this);
//        helper.getAllLocation("fpt_demo", new CallbackData<Building>() {
//            @SuppressLint("StaticFieldLeak")
//            @Override
//            public void onSuccess(final Building building) {
////                Gson gson = new Gson();
////                String json = gson.toJson(locations);
////
////                txtDbStatus.setText(json);
//
//
//                btnAddDatabase.setEnabled(false);
//
//
//                new AsyncTask<Void, Void, Void>() {
//
//                    @Override
//                    protected Void doInBackground(Void... voids) {
//
//                        // add building
//                        db.addBuilding(building);
//
//
//                        List<Floor> floors = building.getListFloor();
//
//                        for (Floor floor : floors) {
////                    txtDbStatus.setText("Loading... Add LocationID: " + location.getId());
//
//                            // add floor
//
//                            db.addFloor(floor);
//
//                            //save map image
//                            FileHelper.saveFileFromUrl(getApplicationContext(), FileHelper.TYPE_MAP, floor.getLinkMap());
//
//                            //add location
//                            for (Location location : floor.getLocationList()) {
//                                db.addLocation(location, floor.getId());
//
//                                // save qr code
////                                FileHelper.saveFileFromUrl(getApplicationContext(), FileHelper.TYPE_QR, location.getLinkQr());
//
//                                // add neighbor
//                                if (location.getNeighborList() != null) {
//                                    if (!location.getNeighborList().isEmpty()) {
//                                        for (Neighbor neighbor : location.getNeighborList()) {
//                                            db.addNeighbor(location.getId(), neighbor);
//                                        }
//                                    }
//                                }
//
//                                // add room
//                                if (location.getListRoom() != null) {
//                                    if (!location.getListRoom().isEmpty()) {
//                                        for (Room room : location.getListRoom()) {
//                                            db.addRoom(room, location.getId(), floor.getId());
//                                        }
//                                    }
//                                }
//                            }
//
//
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        super.onPostExecute(aVoid);
//                        db.closeDB();
////        txtDbStatus.setText("Đã tạo Database");
//                        updateStatus();
//                    }
//                }.execute();
//
//
//            }
//
//            @Override
//            public void onFail(String message) {
//                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//                System.out.println("hieu => " + message);
//                updateStatus();
//            }
//        });
//
//
//    }
//
//    public void showMap(View view) {
//        Intent intent = new Intent(this, MapActivity.class);
//        startActivity(intent);
//    }
//
//    public void checkData(View view) {
//            AppHelper appHelper = AppHelper.getInstance(this);
//            appHelper.getAllBuilding(new CallbackData<List<Company>>() {
//                @SuppressLint("StaticFieldLeak")
//                @Override
//                public void onSuccess(List<Company> companies) {
//
//                    new AsyncTask<Void, Void, Void>(){
//
//                        @Override
//                        protected Void doInBackground(Void... voids) {
//
//                            for (Company company : companies) {
//                                for (Building building : company.getListBuilding()) {
//                                    if (checkBuildingExist(building.getId())) {
//
//                                    } else  {
//                                        db.addBuilding(building);
//                                    }
//                                }
//                            }
//
//                            return null;
//                        }
//                    }.execute();
//                }
//
//                @Override
//                public void onFail(String message) {
//                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//                }
//            });
//    }
//
//    private boolean checkBuildingExist(String buildingId) {
//        for (Building building : listBuilding) {
//            if (building.equals(building.getId())) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
