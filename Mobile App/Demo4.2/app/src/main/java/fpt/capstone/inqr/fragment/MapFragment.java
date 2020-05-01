package fpt.capstone.inqr.fragment;


import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.adapter.MapAdapter;
import fpt.capstone.inqr.adapter.PointViewAdapter;
import fpt.capstone.inqr.adapter.StepAdapter;
import fpt.capstone.inqr.dijkstra.Vertex;
import fpt.capstone.inqr.helper.CanvasHelper;
import fpt.capstone.inqr.helper.ImageHelper;
import fpt.capstone.inqr.helper.PreferenceHelper;
import fpt.capstone.inqr.helper.Wayfinder;
import fpt.capstone.inqr.model.Floor;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Neighbor;
import fpt.capstone.inqr.model.Room;
import fpt.capstone.inqr.model.supportModel.Line;
import fpt.capstone.inqr.model.supportModel.Step;
import fpt.capstone.inqr.presenter.MapPresenter;
import fpt.capstone.inqr.view.MapView;
import github.nisrulz.qreader.QREader;

import static android.content.Context.SENSOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends BaseFragment implements SensorEventListener, MapView {

    //    private ImageView imgView;
    private View view;
    private RecyclerView rvMap, rvDot;
    private MapAdapter adapterMap;
    private PointViewAdapter adapterPoint;
    private LinearLayout btNavigate;
    private RelativeLayout btStepList;
    private ImageView imgScan, imgLike;
    private FrameLayout frame;
    private TextView tvTime, tvDistance;
    private LinearLayout howItWorkBlock;

    private Bitmap mapImg;
    private AutoCompleteTextView tvStart, tvEnd;

    private List<Location> locationList;
    private List<Room> roomList;
    private List<Floor> floorList;
    private List<String> locationNameList, roomNameList, listFloorName, allFloorNames, listFloorIdOnWay;
    private List<Vertex> listPointOnWay;
    private List<Bitmap> listSourceMap;
    private List<List<Line>> listLines;
    private List<Step> listStep;

    private String buildingId = "", nameOfDestinationRoom, startLocationId = "";

    // xoay
    private QREader qrEader;
    private ImageView img;
    private SensorManager mSensorManager;
    private Sensor mRotation;
    private float currentDegrees = 0f;

    private FrameLayout bgImg;
    private SurfaceView cameraView;
    boolean hadQr = false;
    private Handler checkQrExistHandler;
    private Runnable runnable;

    // walking-speed
    private double oldDistanceRemove;
    private String oldTimeScan, currentTime;
    private SimpleDateFormat sdf;

    // TODO: ADD SOME BOTTOM-SHEET HANDLER
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayoutBottomSheet;
    private RelativeLayout mapFooterSection;
    private RecyclerView rvStep;
    private StepAdapter stepAdapter;
    private ImageView imgWayInfoToggle;
    private TextView tvWayInfoToggleName;

    private MapPresenter mMapPresenter;
    private Wayfinder wayfinder;

    private Calendar calendar;

    private TextToSpeech textToSpeech;
    private boolean favoriteDestination = false;

    public MapFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();

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

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map_modified_updated, container, false);
        sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        buildingId = getArguments().getString("buildingID", "");

        // TODO: MODIFY HERE
        mMapPresenter = new MapPresenter(this, getContext());
        mMapPresenter.loadBuildingData(buildingId);
        return view;
    }

    @Override
    public void onSuccessLoadBuildingData(List<Floor> floors, List<Location> locations, List<Room> rooms, List<String> floorNames, List<String> locationNames, List<String> roomNames) {
        floorList = floors;
        allFloorNames = floorNames;
        locationList = locations;
        locationNameList = locationNames;
        roomList = rooms;
        roomNameList = roomNames;
        wayfinder = new Wayfinder(locationList, roomList);

        initView(view);
//        setupInput();
        setupInputNew();

        setupSensor();
        setupScanQR();
    }

    @Override
    public void onLoadRoomData(List<Room> listRooms) {
        this.roomList = listRooms;
    }

    private void setupInputNew() {
        tvStart.setOnClickListener(v -> {
            if (frame.getVisibility() == View.VISIBLE) {
                frame.setVisibility(View.GONE);
            }

            //remove destination
            tvEnd.setText("");

            this.changeFragment(new ChooseLocationFragment(this, locationList), true, false);
        });

        tvEnd.setOnClickListener(v -> {
            if (frame.getVisibility() == View.VISIBLE) {
                frame.setVisibility(View.GONE);
            }
            this.changeFragment(new ChooseDestinationFragment(this, roomList, buildingId), true, false);
        });
    }

    public void speak(String message) {
        String utteranceId = UUID.randomUUID().toString();
        textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, utteranceId);
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

    public void setStartLocation(Location location) {
        tvStart.setText(location.getName());

        tvStart.setError(null);
    }

    public void setDestination(Room room) {
        tvEnd.setText(room.getName());
        tvEnd.setError(null);

        if (imgLike.getVisibility() == View.GONE) {
            imgLike.setVisibility(View.VISIBLE);
        }

        if (room.isSpecialRoom()) {
            int count = PreferenceHelper.getInt(getContext(), buildingId + "_" + room.getName().toLowerCase());
            // save in preference
            PreferenceHelper.putInt(getContext(), buildingId + "_" + room.getName().toLowerCase(), count + 1);

            favoriteDestination = PreferenceHelper.getBoolean(getContext(), buildingId + "_" + room.getName().toLowerCase() + "_favorite");
        } else {
            favoriteDestination = room.isFavorite();

            // update counter
            mMapPresenter.updateRoomCounter(buildingId, room.getId(), room.getCounter() + 1);
        }

        if (favoriteDestination) {
            imgLike.setImageResource(R.drawable.ic_like);
        } else {
            imgLike.setImageResource(R.drawable.ic_no_like);
        }

        // process find way
        if (tvStart.getText().toString().isEmpty()) {
            tvStart.setError("Invalid");
        } else {
            nameOfDestinationRoom = room.getName();

            startLocationId = getLocationId(tvStart.getText().toString());
            processFindWay(startLocationId, room.getName());
        }


    }

    private void setupInput() {
        // LOCATION >> STARTING INPUT
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, 0);
        for (String s : locationNameList) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).equals(s))
                    adapter.remove(s);
            }
            adapter.add(s);
        }

        tvStart.setAdapter(adapter);
        adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, 0);

        // ROOM >> DESTINATION
        for (String s : roomNameList) {
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
            nameOfDestinationRoom = roomName;

            String locationName = tvStart.getText().toString();
            if (checkInputStartPoint(locationName)) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tvEnd.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(tvStart.getWindowToken(), 0);

                startLocationId = getLocationId(locationName);
                processFindWay(startLocationId, roomName);
            } else {
                tvStart.setError(getResources().getString(R.string.input_error));
            }
        });
    }

    private boolean checkInputStartPoint(String name) {
        for (String locationName : locationNameList) {
            if (name.equals(locationName)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkInputEndPoint(String name) {
        for (String roomName : roomNameList) {
            if (name.equals(roomName)) {
                return true;
            }
        }
        return false;
    }

    private void initView(View view) {
        rvMap = view.findViewById(R.id.rvMap);
        rvDot = view.findViewById(R.id.rvDot);
        btNavigate = view.findViewById(R.id.bt_navigate);
        btStepList = view.findViewById(R.id.map_footer_section);
        imgScan = view.findViewById(R.id.imgScan);
        imgLike = view.findViewById(R.id.imgLike);
        tvStart = view.findViewById(R.id.tvStart);
        tvEnd = view.findViewById(R.id.tvEnd);

        frame = view.findViewById(R.id.frame);
        howItWorkBlock = view.findViewById(R.id.how_it_work_block);

        img = view.findViewById(R.id.img);
        bgImg = view.findViewById(R.id.bgImg);
        cameraView = view.findViewById(R.id.cameraView);

        // TODO: CHANGE THE ID OF TEXT VIEW
        tvTime = view.findViewById(R.id.tv_time);
        tvDistance = view.findViewById(R.id.tv_distance);
        tvWayInfoToggleName = view.findViewById(R.id.tv_way_info_toggle_name);
        imgWayInfoToggle = view.findViewById(R.id.img_way_info_toggle);

        rvStep = view.findViewById(R.id.rvStep);
        stepAdapter = new StepAdapter(this, new ArrayList<>());
        rvStep.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        rvStep.setAdapter(stepAdapter);

        mapFooterSection = view.findViewById(R.id.map_footer_section);
        linearLayoutBottomSheet = view.findViewById(R.id.way_info_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tvWayInfoToggleName.setText("SHOW MAP");
                    imgWayInfoToggle.setImageResource(R.drawable.ic_show_map);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tvWayInfoToggleName.setText("STEP AND MORE");
                    imgWayInfoToggle.setImageResource(R.drawable.ic_step_and_more);
                } // end if
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        btNavigate.setOnClickListener(v -> {
            NavigationFragment navFragment = new NavigationFragment(locationList, roomList, nameOfDestinationRoom);
            changeFragment(navFragment, true, false);
        });

        btStepList.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });


        imgScan.setOnClickListener(v -> {
            setupCameraPreview();
        });

        imgLike.setOnClickListener(v -> {
            if (wayfinder.getRoom(tvEnd.getText().toString()).isSpecialRoom()) {
                if (favoriteDestination) {
                    imgLike.setImageResource(R.drawable.ic_no_like);

                    PreferenceHelper.putBoolean(getContext(), buildingId + "_" + tvEnd.getText().toString().toLowerCase() + "_favorite", false);

                    favoriteDestination = false;
                } else {
                    imgLike.setImageResource(R.drawable.ic_like);

                    PreferenceHelper.putBoolean(getContext(), buildingId + "_" + tvEnd.getText().toString().toLowerCase() + "_favorite", true);

                    favoriteDestination = true;
                }
            } else {
                if (favoriteDestination) {
                    imgLike.setImageResource(R.drawable.ic_no_like);

                    mMapPresenter.updateFavoriteRoom(buildingId, wayfinder.getRoom(tvEnd.getText().toString()).getId(), 0);

                    favoriteDestination = false;
                } else {
                    imgLike.setImageResource(R.drawable.ic_like);

                    mMapPresenter.updateFavoriteRoom(buildingId, wayfinder.getRoom(tvEnd.getText().toString()).getId(), 1);

                    favoriteDestination = true;
                }
            }
        });

        // prepare rv
        adapterMap = new MapAdapter(getActivity());
        adapterPoint = new PointViewAdapter(this, getActivity());

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
    }

    public void setupCameraPreview() {
        if (frame.getVisibility() == View.VISIBLE) {
            frame.setVisibility(View.GONE);
        } else if (frame.getVisibility() == View.GONE) {
            frame.setVisibility(View.VISIBLE);

//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(tvEnd.getWindowToken(), 0);
//            imm.hideSoftInputFromWindow(tvStart.getWindowToken(), 0);
        }
    }

    private void showMap() {
        howItWorkBlock.setVisibility(View.GONE);
        linearLayoutBottomSheet.setVisibility(View.VISIBLE);
        mapFooterSection.setVisibility(View.VISIBLE);
        rvMap.setVisibility(View.VISIBLE);
        rvDot.setVisibility(View.VISIBLE);
        tvTime.setVisibility(View.VISIBLE);
        tvDistance.setVisibility(View.VISIBLE);
        stepAdapter.setListSteps(listStep);
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
                                    processFindWay(startLocationId, tvEnd.getText().toString());
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
                .height(cameraView.getHeight())
                .width(cameraView.getWidth())
                .build();
        qrEader.start();
    }

    private void updateOrientation(String orientId) {
        switch (orientId) {
            case Neighbor.ORIENT_NULL:
                img.setImageResource(R.drawable.like);
                break;
            case Neighbor.ORIENT_LEFT:
                img.setImageResource(R.drawable.arrow_left);
                break;
            case Neighbor.ORIENT_RIGHT:
                img.setImageResource(R.drawable.arrow_right);
                break;
            case Neighbor.ORIENT_UP:
                img.setImageResource(R.drawable.arrow_up);
                break;
            case Neighbor.ORIENT_DOWN:
                img.setImageResource(R.drawable.arrow_down);
                break;
            case Neighbor.ORIENT_FORWARD:
                img.setImageResource(R.drawable.arrow_forward);
                break;
            case Neighbor.ORIENT_BACKWARD:
                img.setImageResource(R.drawable.arrow_back);
                break;
        }
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

    private Location getLocation(String id) {
        for (int i = 0; i < locationList.size(); i++) {
            if (id.equals(locationList.get(i).getId())) {
                return locationList.get(i);
            }
        }

        return null;
    }

    private void processFindWay(String startLocationId, String roomName) {
        new Handler().postDelayed(() -> {
            List<Vertex> listTmp = checkLocationInListFindWay(startLocationId);

            if (listTmp == null) {
                destination = roomName;
                wayfinder.findWay(startLocationId, destination);
                listPointOnWay = wayfinder.getShortestPathList();
            } else {
                listPointOnWay = listTmp;
                wayfinder.setShortestPathList(listPointOnWay);
            }

            processDistanceAndTime();
            drawOnMap();

        }, 200);
    }

    private void processDistanceAndTime() {
        // cal time and distance
        // khoảng cách chính xác sẽ bằng: khoảng cách từ điểm bắt đầu đến điểm kết thúc - khoảng cách từ điểm bắt đầu
        // đến điểm đầu tiên trong listPointOnWay
        double shortestDistance = wayfinder.getCurrentShortestDistance();
        if (shortestDistance != 0) {
            double distanceRemove = listPointOnWay.get(0).getDistance();
            double distanceReal = shortestDistance - distanceRemove;
            int speed;

            if (distanceRemove != 0) {
                double tmpDistance = distanceRemove - oldDistanceRemove;

                currentTime = sdf.format(new Date());
                Date startTime = null;
                Date endTime = null;
                try {
                    startTime = sdf.parse(oldTimeScan);
                    endTime = sdf.parse(currentTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long tmpTime = endTime.getTime() - startTime.getTime();
                speed = (int) ((tmpDistance * (1000 * 60 * 60)) / tmpTime);
            } else {
                // speed : m/h
                speed = PreferenceHelper.getInt(getContext(), "speed_walking");

                if (speed == 0) {
                    speed = getActivity().getResources().getInteger(R.integer.speed_walking);
                    PreferenceHelper.putInt(getContext(), "speed_walking", speed);
                }
            }

            double time = distanceReal / speed * 60;

            int mins = (int) (time / 1);
            int sens = (int) (time % 1 * 60);

            currentTime = sdf.format(new Date());
            Date date = null;
            try {
                date = sdf.parse(currentTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.SECOND, sens);
            calendar.add(Calendar.MINUTE, mins);

            if (mins == 0) {
                tvTime.setText(sens + "secs");
            } else {
                if (mins == 1) {
                    tvTime.setText(mins + "min " + sens + "secs");
                } else {
                    tvTime.setText(mins + "mins " + sens + "secs");
                }
            }

//            tvTime.setText(currentTime + " - " + sdf.format(calendar.getTime()));
            tvDistance.setText("(" + (int) Math.round(distanceReal) + "m)");

            oldTimeScan = currentTime;
            oldDistanceRemove = distanceRemove;

        } else {
            tvTime.setText("You are at the destination");
            tvDistance.setText("");
        }
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
        getListSourceMap();

        // send data to adapter, cập nhập View
        if (frame.getVisibility() == View.VISIBLE) {
            adapterMap.setListSource(listSourceMap, listLines, 3f);
        } else {
            adapterMap.setListSource(listSourceMap, listLines, 2f);
        }

        adapterPoint.setListName(listFloorName);


        // EXTRACT DIRECTION_GUIDE TO TEXT LIST
        if (listStep == null) {
            listStep = new ArrayList<>();
        } else {
            listStep.clear();
        }

        // lấy cách đi chi tiết
        double shortestDistance = wayfinder.getCurrentShortestDistance();
        if (shortestDistance == 0) {
//            listStep.add(new Step(Step.TYPE_START_POINT, "You are at: " + tvStart.getText().toString(), null));
//            listStep.addAll(wayfinder.getListStepGuide());
//            listStep.add(new Step(Step.TYPE_END_POINT, "You reach the destination: " + tvEnd.getText().toString(), null));
        } else {
            listStep.add(new Step(Step.TYPE_START_POINT, "You are at: " + tvStart.getText().toString() + " at " + currentTime, null));
            listStep.addAll(wayfinder.getListStepGuide());
            listStep.add(new Step(Step.TYPE_END_POINT, "You reach the destination: " + tvEnd.getText().toString() + " at about " + sdf.format(calendar.getTime()), null));
        }


        // update UI
        showMap();
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

        // DRAW PATH ON EACH FLOOR
        List<Location> locationPathList = wayfinder.getLocationPathList();
        Room destinationRoom = wayfinder.getEndRoom();

        for (String floorId : listFloorIdOnWay) {
            mapImg = ImageHelper.getBitmap(getContext(), buildingId, floorId);
            List<Line> lines = CanvasHelper.drawImage(getContext(), mapImg, floorId, locationPathList, destinationRoom);
            listSourceMap.add(mapImg);
            listLines.add(lines);
        } // end for floor to draw path
    }

    private String getFloorName(String floorID) {
        for (Floor floor : floorList) {
            if (floorID.equals(floor.getId())) return floor.getName();
        }

        return "";
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
                listFloorName.add(getFloorName(floorId));
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
}
