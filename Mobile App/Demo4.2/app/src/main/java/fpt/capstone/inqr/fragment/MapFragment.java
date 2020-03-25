package fpt.capstone.inqr.fragment;


import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;
import fpt.capstone.inqr.R;
import fpt.capstone.inqr.adapter.MapAdapter;
import fpt.capstone.inqr.adapter.PointViewAdapter;
import fpt.capstone.inqr.dijkstra.DijkstraShortestPath;
import fpt.capstone.inqr.dijkstra.Edge;
import fpt.capstone.inqr.dijkstra.Vertex;
import fpt.capstone.inqr.helper.DatabaseHelper;
import fpt.capstone.inqr.helper.FileHelper;
import fpt.capstone.inqr.model.Floor;
import fpt.capstone.inqr.model.supportModel.Line;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Neighbor;
import fpt.capstone.inqr.model.Room;
import fpt.capstone.inqr.model.supportModel.Step;
import github.nisrulz.qreader.QREader;

import static android.content.Context.SENSOR_SERVICE;
import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends BaseFragment implements SensorEventListener {

    //    private ImageView imgView;
    private RecyclerView rvMap, rvDot;
    private MapAdapter adapterMap;
    private PointViewAdapter adapterPoint;
    private CardView bgNavigate, bgStep;
    private ImageView imgScan;
    private FrameLayout frame;
    private TextView tvTime, tvDistance;

    private DatabaseHelper db;
    private Bitmap mapImg;
    private Canvas canvas;
    private AutoCompleteTextView tvStart;
    private AutoCompleteTextView tvEnd;
    //    private Button btnFindWay;
//    private TextView tvFloor;
    private List<Location> locationList;
    private List<Room> listRoom;
    private List<Room> listSpecialRoom;
    private List<String> listLocationName;
    private List<String> listRoomName;
    private List<Vertex> vertexList;
    private List<Vertex> listPointOnWay;
    private List<String> listFloorIdOnWay;
    private List<Bitmap> listSourceMap;
    private List<Floor> listFloor;
    private List<String> listFloorName;

    private List<List<Line>> listLines;
    private List<Line> lines;

    private List<Step> listStep;

    private Room endRoom;

    private String startLocationId = "";
    private String buildingId = "";


//    private String currentFloorId;


    private DijkstraShortestPath shortestPath;


    // xoay
    private QREader qrEader;
    private ImageView img;
    private SensorManager mSensorManager;
    private Sensor mRotation;
    private float currentDegrees = 0f;

    private FrameLayout bgImg;
    private SurfaceView cameraView;
    //    private Camera camera;
    boolean hadQr = false;
    private Handler checkQrExistHandler;
    private Runnable runnable;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
//        cameraView = new SurfaceView(this.getContext());


        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_GAME);
        Dexter.withActivity(this.getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (qrEader != null) {
                            qrEader.initAndStart(cameraView);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

        checkQrExistHandler.postDelayed(runnable, 100);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        Dexter.withActivity(this.getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (qrEader != null) {
                            qrEader.releaseAndCleanup();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    public void onStop() {
        super.onStop();
        checkQrExistHandler.removeCallbacks(runnable);
        checkQrExistHandler.removeCallbacksAndMessages(null);

//        this.getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("Chỉ đường");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            buildingId = bundle.getString("buildingID", "");
        }

        db = new DatabaseHelper(getContext());
        // get all floor
        listFloor = db.getAllFloors(buildingId);
        // get all location
        locationList = new ArrayList<>();
        for (Floor floor : listFloor) {
            locationList.addAll(db.getAllLocations(floor.getId()));
        }
        // get all location Name
        listLocationName = getListLocationName();
        // get all room
        listRoom = new ArrayList<>();
        for (Location location : locationList) {
            listRoom.addAll(db.getAllRooms(location.getId()));
        }
        // get all room name
        listRoomName = getListRoomName();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initView(view);
        setupInput();

        setupSensor();

        setupScanQR();

        imgScan.setOnClickListener(v -> {
            if (frame.getVisibility() == View.VISIBLE) {
                frame.setVisibility(View.GONE);
            } else if (frame.getVisibility() == View.GONE) {
                frame.setVisibility(View.VISIBLE);
            }
        });

        bgNavigate.setOnClickListener(v -> {


            Toasty.success(getContext(), "Wait for Tinh ^^", Toast.LENGTH_SHORT).show();
        });

        bgStep.setOnClickListener(v -> {
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
            bottomSheetFragment.setListSteps(listStep);
            bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        // prepare rv
        adapterMap = new MapAdapter(getActivity());
        adapterPoint = new PointViewAdapter(this);

        rvMap.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        rvMap.setAdapter(adapterMap);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvMap);

        rvDot.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
        rvDot.setAdapter(adapterPoint);


//        rvMap.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager layoutManager = (LinearLayoutManager) rvMap.getLayoutManager();
//                int positionOfVisibleView = layoutManager.findFirstVisibleItemPosition();
//
//                adapterMap.setListSource(listSourceMap, listLines);
//
//                adapterPoint.setPosition(positionOfVisibleView);
//            }
//        });


        return view;
    }

    public void chooseFloor(int position) {
        if (frame.getVisibility() == View.VISIBLE) {
            adapterMap.setListSource(listSourceMap, listLines, 3f);
        } else {
            adapterMap.setListSource(listSourceMap, listLines, 2f);
        }
        rvMap.scrollToPosition(position);
        adapterPoint.setPosition(position);
    }


    private void setupScanQR() {
        Dexter.withActivity(this.getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        setupCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

//        img.setVisibility(View.INVISIBLE);

        checkQrExistHandler = new Handler();
        runnable = () -> {
            if (hadQr) {
                mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_GAME);
                this.getActivity().runOnUiThread(() -> bgImg.setVisibility(View.VISIBLE));
            } else {
                mSensorManager.unregisterListener(this);
                this.getActivity().runOnUiThread(() -> bgImg.setVisibility(View.INVISIBLE));
            }

            hadQr = false;
            checkQrExistHandler.postDelayed(runnable, 100);
        };
//        checkQrExistHandler.postDelayed(runnable, 100);
    }

    private void setupSensor() {
        mSensorManager = (SensorManager) this.getActivity().getSystemService(SENSOR_SERVICE);

        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    private void setupInput() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, 0);
        for (String s : listLocationName) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(s))
                    adapter.remove(s);
            }
            adapter.add(s);
        }
        tvStart.setAdapter(adapter);
        adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, 0);
        for (String s : listRoomName) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(s))
                    adapter.remove(s);
            }
            adapter.add(s);
        }
        tvEnd.setAdapter(adapter);
        tvEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startLocationId = "";
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ArrayAdapter<String> finalAdapter = adapter;
        tvEnd.setOnItemClickListener((parent, view, position, id) -> {
            String roomName = finalAdapter.getItem(position);

            String locationName = tvStart.getText().toString();
            if (checkInputStartPoint(locationName)) {
                startLocationId = getLocationId(locationName);

                prepareBeforeFindWay(startLocationId, roomName);
            } else {
                tvStart.setError(getResources().getString(R.string.input_error));
            }
        });
    }

    private boolean checkInputStartPoint(String name) {
        for (String locationName : listLocationName) {
            if (name.equals(locationName)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkInputEndPoint(String name) {
        for (String roomName : listRoomName) {
            if (name.equals(roomName)) {
                return true;
            }
        }
        return false;
    }

    private void initView(View view) {
        rvMap = view.findViewById(R.id.rvMap);
        rvDot = view.findViewById(R.id.rvDot);
        bgNavigate = view.findViewById(R.id.bgNavigate);
        bgStep = view.findViewById(R.id.bgStep);
        imgScan = view.findViewById(R.id.imgScan);
//        imgView = view.findViewById(R.id.imgView);
        tvStart = view.findViewById(R.id.tvStart);
        tvEnd = view.findViewById(R.id.tvEnd);

        frame = view.findViewById(R.id.frame);

        img = view.findViewById(R.id.img);
        bgImg = view.findViewById(R.id.bgImg);
//        bgCam = view.findViewById(R.id.bgCam);
        cameraView = view.findViewById(R.id.cameraView);


        tvTime = view.findViewById(R.id.tvTime);
        tvDistance = view.findViewById(R.id.tvDistance);
    }

    private void showMap() {
        rvMap.setVisibility(View.VISIBLE);
        rvDot.setVisibility(View.VISIBLE);
        bgNavigate.setVisibility(View.VISIBLE);
        bgStep.setVisibility(View.VISIBLE);

        tvTime.setVisibility(View.VISIBLE);
        tvDistance.setVisibility(View.VISIBLE);
    }

    private void setupCamera() {
        qrEader = new QREader.Builder(this.getContext(), cameraView, data -> {
            tvStart.post(() -> {
//                    btnFindWay.setText(data);

                // check end point trước
                if (checkInputEndPoint(tvEnd.getText().toString())) {
                    // ID: fpt_demo_l_1 - Name: 112
                    String[] tmp = data.split("\\|");

                    if (tmp.length > 1) {
                        String[] id = tmp[0].trim().toLowerCase().split(":");
                        if (id.length > 1) {
                            // cập nhập
                            hadQr = true;
                            if (!startLocationId.equals(id[1].trim())) {

                                String[] name = tmp[1].trim().split(":");

//                            tvFloor.setText("Vị trí của bạn: " + name[1].trim());


                                // id không có trong db
                                if (getLocation(id[1].trim()) != null) {
                                    tvStart.setText(name[1].trim());
                                    tvStart.setError(null);


                                    startLocationId = id[1].trim();

                                    //tìm đường
                                    prepareBeforeFindWay(startLocationId, tvEnd.getText().toString());
                                } else {
                                    tvStart.setText(getResources().getString(R.string.qr_error));
                                }


                            }
                        } else {
                            tvStart.setText(getResources().getString(R.string.qr_error));
                        }
                    } else {
                        tvStart.setText(getResources().getString(R.string.qr_error));
                    }
                } else {
                    tvEnd.setError(getResources().getString(R.string.input_error));
                }
            });


        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
//                .height(cameraView.getHeight())
//                .width(cameraView.getWidth())
                .build();
        qrEader.start();
    }

    private void updateOrientation(int orientId) {
        switch (orientId) {
            case Neighbor.ORIENT_NULL:
                img.setImageResource(R.drawable.like);
                break;
            case Neighbor.ORIENT_LEFT:
            case Neighbor.ORIENT_LEFT_TURN_LEFT:
            case Neighbor.ORIENT_LEFT_TURN_RIGHT:
                img.setImageResource(R.drawable.arrow_left);
                break;
            case Neighbor.ORIENT_RIGHT:
            case Neighbor.ORIENT_RIGHT_TURN_LEFT:
            case Neighbor.ORIENT_RIGHT_TURN_RIGHT:
                img.setImageResource(R.drawable.arrow_right);
                break;
            case Neighbor.ORIENT_UP:
                img.setImageResource(R.drawable.arrow_up);
                break;
            case Neighbor.ORIENT_DOWN:
                img.setImageResource(R.drawable.arrow_down);
                break;
        }
    }

    private List<Room> getListSpecialRoom(String roomName) {
        List<Room> listWC = new ArrayList<>();
        for (Room room : listRoom) {
            if (roomName.equals(room.getName())) {
                listWC.add(room);
            }
        }
        return listWC;
    }

    private List<String> getListLocationName() {
        List<String> listName = new ArrayList<>();

        for (Location location : locationList) {
            listName.add(location.getName());
        }

        Collections.sort(listName);

        return listName;
    }

    private List<String> getListRoomName() {
        List<String> listName = new ArrayList<>();

        for (Room room : listRoom) {
            listName.add(room.getName());
        }

        Collections.sort(listName);

        return listName;
    }

    private String getLocationId(String name) {
        if (name != null) {
            for (Location location : locationList) {
                if (name.toLowerCase().equals(location.getName().toLowerCase())) {
                    return location.getId();
                }
            }
        }
        return null;
    }

    private String getLocationIdOfRoom(String name) {
        if (name != null) {
            for (Room room : listRoom) {
                if (name.toLowerCase().equals(room.getName().toLowerCase())) {
                    return room.getLocationId();
                }
            }
        }
        return null;
    }

    private int getIndexOfLocation(String id) {

        for (int i = 0; i < locationList.size(); i++) {
            if (id.equals(locationList.get(i).getId())) {
                return i;
            }
        }

        return -1;
    }

    private Location getLocation(String id) {
        for (int i = 0; i < locationList.size(); i++) {
            if (id.equals(locationList.get(i).getId())) {
                return locationList.get(i);
            }
        }
        return null;
    }

    private Room getRoom(String name) {
        for (Room room : listRoom) {
            if (name.equals(room.getName())) {
                return room;
            }
        }
        return null;
    }


    private void prepareData() {
        if (vertexList == null) {
            vertexList = new ArrayList<>();
        } else {
            vertexList.clear();
        }

        for (int i = 0; i < locationList.size(); i++) {
            Vertex vertex = new Vertex(locationList.get(i).getId(), locationList.get(i).getName());
            vertexList.add(vertex);
        }

        for (int i = 0; i < locationList.size(); i++) {
            for (int j = 0; j < locationList.get(i).getNeighborList().size(); j++) {
                int index = getIndexOfLocation(locationList.get(i).getNeighborList().get(j).getId());

                vertexList.get(i).addNeighbour(new Edge(locationList.get(i).getNeighborList().get(j).getDistance(), vertexList.get(i), vertexList.get(index)));
            }
        }
    }

    private void prepareBeforeFindWay(String startLocationId, String roomName) {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tvEnd.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tvStart.getWindowToken(), 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                findWay(startLocationId, roomName);

                List<Vertex> listTmp = checkLocationInListFindWay(startLocationId);
                if (listTmp == null) {
                    destination = roomName;
                    findWay(startLocationId, destination);
                } else {
                    listPointOnWay = listTmp;
                    // update UI
                    drawOnMap();
                }

                // cal time and distance
                // khoảng cách chính xác sẽ bằng: khoảng cách từ điểm bắt đầu đến điểm kết thúc - khoảng cách từ điểm bắt đầu
                // đến điểm đầu tiên trong listPointOnWay
                if (currentPath != 0) {
                    double distanceRemove = listPointOnWay.get(0).getDistance();
                    double distanceReal = currentPath - distanceRemove;

                    // speed : m/h
                    int speed = getActivity().getResources().getInteger(R.integer.speed_walking);
                    double time = distanceReal / speed * 60;

                    int mins = (int) (time / 1);
                    int sens = (int) (time % 1 * 60);

                    if (mins != 0) {
                        tvTime.setText(mins + "min " + sens + "sen");
                    } else {
                        tvTime.setText(sens + "sen");
                    }

                    tvDistance.setText("(" + (int) Math.round(distanceReal) + "m)");
                } else {
                    tvTime.setText("You are at the destination");
                    tvDistance.setText("");
                }


            }
        }, 200);
    }

    private String destination = "";

    private List<Vertex> checkLocationInListFindWay(String locationId) {
        List<Vertex> listTmp = null;
        // kiểm tra điểm cuối có giống k
        if (destination.equals(tvEnd.getText().toString())) {
            if (listPointOnWay != null) {
                if (listPointOnWay.size() > 1) {
                    for (int i = 1; i < listPointOnWay.size() - 1; i++) {
                        if (listPointOnWay.get(i).getId().equals(locationId)) {
                            // Tạo 1 list mới chỉ chứa các location vừa check trở về sau
                            listTmp = new ArrayList<>();
                            for (int j = i; j < listPointOnWay.size(); j++) {
                                listTmp.add(listPointOnWay.get(j));
                            }
                            break;
                        }
                    }
                }
            }
        }


        return listTmp;
    }

    private  double currentPath = 0.0;
    private void findWay(String startLocationId, String roomName) {

//        if (!isFinding) {
//        updateFloor(getLocation(startLocationId).getFloorId());
//        }
//        isFinding = true;

        prepareData();


        if (shortestPath == null) {
            shortestPath = new DijkstraShortestPath();
        }

        // check room belong to which location
        String locationId = getLocationIdOfRoom(roomName);
        Vertex endPoint = getVertexInList(locationId);


        // kiểm tra xem có phải special room không?
//        String roomName = tvEnd.getText().toString();
        if (getRoom(roomName).isSpecialRoom()) {

            //prepare list special room
            listSpecialRoom = getListSpecialRoom(roomName);


//            String startId = getLocationId(tvStart.getText().toString());
            double shortestDistance = 0;
            for (int i = 0; i < listSpecialRoom.size(); i++) {
//                System.out.println("WC: " + commonLocations.get(i).get

                // WC nằm chung vị trí với Start Point
                if (listSpecialRoom.get(i).getLocationId().equals(startLocationId)) {
                    listPointOnWay = new ArrayList<>();
                    listPointOnWay.add(getVertexInList(listSpecialRoom.get(i).getLocationId()));
                    endRoom = listSpecialRoom.get(i);

                    currentPath = 0;
                    break;
                } else {
                    shortestPath.computeShortestPaths(getVertexInList(startLocationId));
                    double tmpPath = getVertexInList(listSpecialRoom.get(i).getLocationId()).getDistance();

                    List<Vertex> listPoint = shortestPath.getShortestsPathTo(getVertexInList(listSpecialRoom.get(i).getLocationId()));

                    if (shortestDistance == 0) {
                        listPointOnWay = listPoint;

                        endRoom = listSpecialRoom.get(i);

                        shortestDistance = tmpPath;
                    } else if (tmpPath < shortestDistance) {
                        listPointOnWay = listPoint;

                        endRoom = listSpecialRoom.get(i);

                        shortestDistance = tmpPath;
                    }
                    prepareData();
                }
            }

            currentPath = shortestDistance;


        } else {

            // check room nằm chung vị trí với Start Point
            endRoom = getRoom(roomName);

            if (endRoom.getLocationId().equals(startLocationId)) {
                listPointOnWay = new ArrayList<>();
                listPointOnWay.add(getVertexInList(endRoom.getLocationId()));

                currentPath = 0;
            } else {
                shortestPath.computeShortestPaths(getVertexInList(startLocationId));
                listPointOnWay = shortestPath.getShortestsPathTo(endPoint);

                currentPath = endPoint.getDistance();
            }


        }
//        shortestPath.computeShortestPaths(getVertexInList(getLocationId(tvStart.getText().toString())));
//        listPointOnWay = shortestPath.getShortestsPathTo(getVertexInList(getLocationId(tvEnd.getText().toString())));

        // update tọa độ của node cuối

        Log.d("Path", listPointOnWay.toString());

        drawOnMap();
    }

    private void drawOnMap() {
        // tìm hướng
        if (listPointOnWay.size() > 1) {
            String neighborId = listPointOnWay.get(1).getId();
            Location location = getLocation(listPointOnWay.get(0).getId());
            updateOrientation(getNeighbor(location, neighborId).getDirection());
        } else {
            // đến nơi rồi
            updateOrientation(Neighbor.ORIENT_NULL);
        }

        getListFloorIdOnWay();

        // refresh map
//        drawImage();

        getListSourceMap();

        // send data to adapter, cập nhập View
        if (frame.getVisibility() == View.VISIBLE) {
            adapterMap.setListSource(listSourceMap, listLines, 3f);
        } else {
            adapterMap.setListSource(listSourceMap, listLines, 2f);
        }

        adapterPoint.setListName(listFloorName);

        // lấy cách đi chi tiết
        getStepDetail();

        // update UI
        showMap();
    }

    private void getStepDetail() {
        if (listStep == null) {
            listStep = new ArrayList<>();
        } else {
            listStep.clear();
        }

        // add điểm bắt đầu
        listStep.add(new Step(Step.TYPE_START_POINT, "Your location: " + tvStart.getText().toString(), null));

//        for (int i = 0; i < listPointOnWay.size() - 1; i++) {
//
//            Location currentLocation = getLocation(listPointOnWay.get(i).getId());
//
//            String currentNeighborId = listPointOnWay.get(i + 1).getId();
//
//            Neighbor currentNeighbor = getNeighbor(currentLocation, currentNeighborId);
//
//            int currentOrientId = currentNeighbor.getDirection();
//
//
//            if (currentOrientId == Neighbor.ORIENT_LEFT) {
//
//                float distance = currentNeighbor.getDistance();
//
//                for (int j = i + 1; j < listPointOnWay.size(); j++) {
//                    if (j == listPointOnWay.size() - 1) {
//                        i = j;
//                        break;
//                    }
//
//                    Location nextLocation = getLocation(listPointOnWay.get(j).getId());
//                    String nextNeighborId = listPointOnWay.get(j + 1).getId();
//
//                    Neighbor nextNeighbor = getNeighbor(nextLocation, nextNeighborId);
//                    int nextOrientId = nextNeighbor.getDirection();
//
//                    if (nextOrientId == Neighbor.ORIENT_LEFT || nextOrientId == Neighbor.ORIENT_RIGHT) {
//                        distance = distance + nextNeighbor.getDistance();
//                    } else {
//                        // cập nhập lại vị trí duyệt
//                        i = j;
//                        break;
//                    }
//                }
//
//                // add step
//                listStep.add(new Step(Step.TYPE_GO_STRAIGHT, "From the left of " + currentLocation.getName() + ", go straight.", distance + "m"));
//            } else if (currentOrientId == Neighbor.ORIENT_RIGHT) {
//                float distance = currentNeighbor.getDistance();
//
//                for (int j = i + 1; j < listPointOnWay.size(); j++) {
//
//                    if (j == listPointOnWay.size() - 1) {
//                        i = j;
//                        break;
//                    }
//
//                    Location nextLocation = getLocation(listPointOnWay.get(j).getId());
//                    String nextNeighborId = listPointOnWay.get(j + 1).getId();
//
//                    Neighbor nextNeighbor = getNeighbor(nextLocation, nextNeighborId);
//                    int nextOrientId = nextNeighbor.getDirection();
//
//                    if (nextOrientId == Neighbor.ORIENT_LEFT || nextOrientId == Neighbor.ORIENT_RIGHT) {
//                        distance = distance + nextNeighbor.getDistance();
//                    } else {
//                        // cập nhập lại vị trí duyệt
//                        i = j;
//                        break;
//                    }
//                }
//
//                // add step
//                listStep.add(new Step(Step.TYPE_GO_STRAIGHT, "From the right of " + currentLocation.getName() + ", go straight.", distance + "m"));
//            } else if (currentOrientId == Neighbor.ORIENT_UP) {
//                // add step
//                listStep.add(new Step(Step.TYPE_UP_STAIR, "Go up at " + currentLocation.getName(), null));
//            } else if (currentOrientId == Neighbor.ORIENT_DOWN) {
//                // add step
//                listStep.add(new Step(Step.TYPE_DOWN_STAIR, "Go down at " + currentLocation.getName(), null));
//            } else if (currentOrientId == Neighbor.ORIENT_LEFT_TURN_LEFT || currentOrientId == Neighbor.ORIENT_RIGHT_TURN_LEFT) {
//                listStep.add(new Step(Step.TYPE_TURN_LEFT, "Turn left at " + currentLocation.getName(), null));
//            } else if (currentOrientId == Neighbor.ORIENT_LEFT_TURN_RIGHT || currentOrientId == Neighbor.ORIENT_RIGHT_TURN_RIGHT) {
//                listStep.add(new Step(Step.TYPE_TURN_RIGHT, "Turn right at " + currentLocation.getName(), null));
//            }
//        }

        // add điểm kết thúc
        listStep.add(new Step(Step.TYPE_END_POINT, "Your destination: " + tvEnd.getText().toString(), null));
    }

    private void getListSourceMap() {
        if (listSourceMap == null) {
            listSourceMap = new ArrayList<>();
        } else {
            listSourceMap.clear();
        }

        if (listLines == null) {
            listLines = new ArrayList<>();
        } else {
            listLines.clear();
        }

        for (String floorId : listFloorIdOnWay) {
            drawImage(floorId);
        }
    }

    private void getListFloorIdOnWay() {
        if (listFloorIdOnWay == null) {
            listFloorIdOnWay = new ArrayList<>();
        } else {
            listFloorIdOnWay.clear();
        }

        if (listFloorName == null) {
            listFloorName = new ArrayList<>();
        } else {
            listFloorName.clear();
        }
        for (Vertex vertex : listPointOnWay) {
            String floorId = getLocation(vertex.getId()).getFloorId();
            if (!listFloorIdOnWay.contains(floorId)) {
                listFloorIdOnWay.add(floorId);
                listFloorName.add(db.getFloorName(floorId));
            }
        }

    }

    private Neighbor getNeighbor(Location location, String neighborId) {
        for (Neighbor neighbor : location.getNeighborList()) {
            if (neighborId.equals(neighbor.getId())) {
                return neighbor;
            }
        }
        return null;
    }

    private void drawImage(String currentFloorId) {

        lines = new ArrayList<>();


        setImage(currentFloorId);
        canvas = new Canvas(mapImg);

//        System.out.println("Starting point: " + getStartingPoint());
//        drawPoint(0);

        if (listPointOnWay.size() == 1) {
            String idStart = listPointOnWay.get(0).getId();
            Location startPoint = getLocation(idStart);
            float xStart, yStart, xEnd, yEnd;
            xStart = Math.round(mapImg.getWidth() * startPoint.getRatioX());
            yStart = Math.round(mapImg.getHeight() * startPoint.getRatioY());

            xEnd = Math.round(mapImg.getWidth() * endRoom.getRatioX());
            yEnd = Math.round(mapImg.getHeight() * endRoom.getRatioY());

            drawLine(xStart, yStart, xEnd, yEnd);
//            imgView.setImageBitmap(mapImg);

            fillArrow(xStart, yStart, xEnd, yEnd);
        } else {
            for (int i = 0; i < listPointOnWay.size(); i++) {

                if (i != listPointOnWay.size() - 1) {

//                int currentFloor = Integer.parseInt(tmp[tmp.length - 1]);
                    if (getLocation(listPointOnWay.get(i).getId()).getFloorId().equals(currentFloorId)) {

                        String idStart = listPointOnWay.get(i).getId();
                        String idEnd = listPointOnWay.get(i + 1).getId();

                        Location startPoint = getLocation(idStart);

                        float xStart, yStart, xEnd, yEnd;
                        xStart = Math.round(mapImg.getWidth() * startPoint.getRatioX());
                        yStart = Math.round(mapImg.getHeight() * startPoint.getRatioY());


                        if (i == listPointOnWay.size() - 2) { // node cuối lấy tọa độ của room
//                        Room room = getRoom(tvEnd.getText().toString());

                            xEnd = Math.round(mapImg.getWidth() * endRoom.getRatioX());
                            yEnd = Math.round(mapImg.getHeight() * endRoom.getRatioY());
                        } else {
                            Location endPoint = getLocation(idEnd);

                            xEnd = Math.round(mapImg.getWidth() * endPoint.getRatioX());
                            yEnd = Math.round(mapImg.getHeight() * endPoint.getRatioY());
                        }

                        drawLine(xStart, yStart, xEnd, yEnd);
//                        imgView.setImageBitmap(mapImg);

                        if (i == 0) {
                            fillArrow(xStart, yStart, xEnd, yEnd);
                        }
                    }


                } else {
                    if (getLocation(listPointOnWay.get(i).getId()).getFloorId().equals(currentFloorId)) {
//                        imgView.setImageBitmap(mapImg);
                    }
                }
            }
        }

        drawPoint(listPointOnWay.get(0).getId(), currentFloorId);

//        imgView.setImageBitmap(mapImg);
        // add map
        listSourceMap.add(mapImg);
        listLines.add(lines);
    }

    private Vertex getVertexInList(String id) {
        for (Vertex vertex : vertexList) {
            if (id.equals(vertex.getId())) {
                return vertex;
            }
        }
        return null;
    }


    private void drawPoint(String idStart, String currentFloorId) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        for (int i = 0; i < listPointOnWay.size() - 1; i++) {
//            Location location = getLocation(listPointOnWay.get(i).getId());
//            if (location.getFloorId().equals(currentFloorId)) {
//
//                canvas.drawCircle(Math.round(mapImg.getWidth() * location.getRatioX()), Math.round(mapImg.getHeight() * location.getRatioY()), 30, new Paint());
//            }
//        }

        if (idStart != null) {
            Location location = getLocation(idStart);
            if (location.getFloorId().equals(currentFloorId)) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.RED);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.current_point);
//                canvas.drawCircle(Math.round(mapImg.getWidth() * location.getRatioX()), Math.round(mapImg.getHeight() * location.getRatioY()), 50, paint);
                canvas.drawBitmap(bitmap, Math.round(mapImg.getWidth() * location.getRatioX() - bitmap.getWidth() / 2), Math.round(mapImg.getHeight() * location.getRatioY() - bitmap.getHeight() / 2), new Paint());
            }

//            Room room = getRoom(tvEnd.getText().toString());
            if (endRoom.getFloorId().equals(currentFloorId)) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.YELLOW);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.destination_on_map);
                canvas.drawBitmap(bitmap, Math.round(mapImg.getWidth() * endRoom.getRatioX() - bitmap.getWidth() / 2), Math.round(mapImg.getHeight() * endRoom.getRatioY() - bitmap.getHeight()), new Paint());
//                canvas.drawCircle(Math.round(mapImg.getWidth() * endRoom.getRatioX()), Math.round(mapImg.getHeight() * endRoom.getRatioY()), 30, paint);
            }
        }

    }

    private void fillArrow(float from_x, float from_y, float to_x, float to_y) {

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(30);
        paint.setColor(Color.GREEN);

        float angle, anglerad, radius, lineangle;

        //values to change for other appearance *CHANGE THESE FOR OTHER SIZE ARROWHEADS*
        radius = 100;
        angle = 60;

        //some angle calculations
        anglerad = (float) (PI * angle / 180.0f);
        lineangle = (float) (atan2(to_y - from_y, to_x - from_x));

        //tha line
        canvas.drawLine(from_x, from_y, to_x, to_y, paint);

        //tha triangle
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(to_x, to_y);
        path.lineTo((float) (to_x - radius * cos(lineangle - (anglerad / 2.0))),
                (float) (to_y - radius * sin(lineangle - (anglerad / 2.0))));
        path.lineTo((float) (to_x - radius * cos(lineangle + (anglerad / 2.0))),
                (float) (to_y - radius * sin(lineangle + (anglerad / 2.0))));
        path.close();

        canvas.drawPath(path, paint);
    }

    private void drawLine(final float xStart, final float yStart, final float xEnd, final float yEnd) {

        lines.add(new Line(xStart, yStart, xEnd, yEnd));


        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#F78B03"));
        paint.setStrokeWidth(25);
        canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);
    }

    private void setImage(String currentFloorId) {
        mapImg = BitmapFactory.decodeStream(FileHelper.getImage(this.getContext(), FileHelper.TYPE_MAP, currentFloorId)).copy(Bitmap.Config.ARGB_8888, true);
    }


    //
//    public void showNextFloor(View view) {
//        if (currentFloorId.equals(getLocation(startLocationId).getFloorId())) {
//
//            updateFloor(endRoom.getFloorId());
//
//            drawImage();
//        } else {
//            updateFloor(getLocation(startLocationId).getFloorId());
//
//            drawImage();
//        }
//    }

//    public void showNextFloor(View view) {
//        if (listPointOnWay == null) {
//            if ((getIndexOfFloor(currentFloorId) + 1) == listFloor.size()) {
//                currentFloorId = listFloor.get(0).getId();
//                updateFloor(currentFloorId);
//                canvas = new Canvas(mapImg);
//                drawPoint(null);
//            } else {
//                currentFloorId = listFloor.get(getIndexOfFloor(currentFloorId) + 1).getId();
//                updateFloor(currentFloorId);
//                canvas = new Canvas(mapImg);
//                drawPoint(null);
//            }
//        } else {
//            if ((getIndexOfFloor(currentFloorId) + 1) == listFloor.size()) {
//                currentFloorId = listFloor.get(0).getId();
//                updateFloor(currentFloorId);
//                drawImage();
//            } else {
//                currentFloorId = listFloor.get(getIndexOfFloor(currentFloorId) + 1).getId();
//                updateFloor(currentFloorId);
//                drawImage();
//            }
//        }
//    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mRotation) {
            // convert a rotation vector to a rotation matrix
            // the result array is passed to rotationMatrix
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            int worldAxisX = SensorManager.AXIS_X; // just testing, we don't need it
            int worldAxisZ = SensorManager.AXIS_Z;
            float[] adjustedRotationMatrix = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);

            float[] orientation = new float[3]; // the result array >>> x, y, z - rotated coordinate system of device
            SensorManager.getOrientation(adjustedRotationMatrix, orientation);

            // Notice: multiple by -60 = chỉ số tâm linh >>>> convert from radian to degree
            float pitch = orientation[1] * (-60); // rotate by X-axis
            float roll = orientation[2] * (-60); // rotate by Z-axis ===> That's what we need


            RotateAnimation ra = new RotateAnimation(
                    currentDegrees,
                    -roll,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(300);

            // set the animation after the end of the reservation status
            ra.setFillAfter(true);

            // Start the animation
            img.startAnimation(ra);
            currentDegrees = roll;
        } // end if sensor type
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    public void speak(View view) {
//        VoiceHelper voiceHelper = new VoiceHelper();
//        int orientation = 0;
//        String startPoint = getLocation(listPointOnWay.get(0).getId()).getName();
//        String nextPoint = "";
//        if (listPointOnWay.size() == 1) {
//            //do Nothing
//        } else {
//            orientation = getNeighbor(getLocation(startLocationId), getLocation(listPointOnWay.get(1).getId()).getId());
////        Location neighbor = getLocation(listPointOnWay.get(1).getId());
////        Location location = getLocation(startLocationId);
////        updateOrientation(getOrientation(location, neighbor.getId()));
//            for (int i = 1; i < listPointOnWay.size(); i++) {
//                if (orientation != getNeighbor(getLocation(listPointOnWay.get(i - 1).getId()), getLocation(listPointOnWay.get(i).getId()).getId())) {
//                    nextPoint = getLocation(listPointOnWay.get(i - 1).getId()).getName();
//                    break;
//                } else {
//                    nextPoint = getLocation(listPointOnWay.get(i).getId()).getName();
//                }
//            }
//        }
//        System.out.println("Start Point: " + startPoint);
//        System.out.println("Next Point: " + nextPoint);
//        System.out.println("Orien: " + orientation);
//
//        voiceHelper.generateSpeechLine(getActivity(), startPoint, nextPoint, orientation);
//    }

}
