package com.xptschool.teacher.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.view.ArrowTextView;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.teacher.R;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.bean.BeanHTLocation;
import com.xptschool.teacher.bean.BeanRTLocation;
import com.xptschool.teacher.bean.BeanRail;
import com.xptschool.teacher.bean.BeanStudent;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.ui.alarm.AlarmActivity;
import com.xptschool.teacher.ui.mine.StudentAdapter;
import com.xptschool.teacher.ui.mine.StudentPopupWindowView;
import com.xptschool.teacher.view.TimePickerPopupWindow;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MapFragment extends MapBaseFragment implements BDLocationListener, SensorEventListener {

    @BindView(R.id.txtSDate)
    ArrowTextView txtSDate;
    @BindView(R.id.txtEDate)
    ArrowTextView txtEDate;
    @BindView(R.id.txtStudentName)
    ArrowTextView txtStudentName;
    @BindView(R.id.flTransparent)
    FrameLayout flTransparent;
    @BindView(R.id.llLocation)
    LinearLayout llLocation;
    @BindView(R.id.llTrack)
    LinearLayout llTrack;
    @BindView(R.id.llRailings)
    LinearLayout llRailings;

    @BindView(R.id.llBindRoad)
    LinearLayout llBindRoad;
    @BindView(R.id.switchBindRoad)
    Button switchBindRoad;

    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;

    private Timer timer = null;
    private TimerTask task;
    TimePickerPopupWindow sDatePopup, eDatePopup;
    private String startTime, endTime;
    PopupWindow studentPopup;
    StudentPopupWindowView studentPopupWindowView;
    private int locationTime = 0;

    public MapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, mRootView);
        mMapView = (MapView) mRootView.findViewById(R.id.mapView);
        progress = (ProgressBar) mRootView.findViewById(R.id.progress);
        //初始化传感器
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        return mRootView;
    }

    @Override
    protected void initData() {
        getStudents();
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.getUiSettings().setRotateGesturesEnabled(true);
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(mContext);
        mLocClient.registerLocationListener(this);
        super.initData();

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(3000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(CommonUtil.getDateBefore(1));
        endTime = CommonUtil.getCurrentDateTime();
        txtSDate.setText(startTime);
        txtEDate.setText(endTime);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions != null && permissions.length > 0) {
            Log.i(TAG, "onRequestPermissionsResult: " + permissions[0]);
        }
        MapFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnClick({R.id.txtSDate, R.id.txtEDate, R.id.txtStudentName, R.id.llLocation, R.id.llTrack,
            R.id.switchBindRoad, R.id.llRailings, R.id.llAlarm, R.id.llMyLocation})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.txtSDate:
                showSDatePop();
                break;
            case R.id.txtEDate:
                showEDatePop();
                break;
            case R.id.txtStudentName:
                showStudent();
                break;
            case R.id.llLocation:
                if (view.getTag() == null || !(Boolean) view.getTag()) {
                    if (currentStudent == null) {
                        Toast.makeText(mContext, R.string.toast_choose_student, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startRTLocationTimer();
                } else {
                    cancelRTLocationTimer();
                }
                llBindRoad.setVisibility(View.GONE);
                resetDefaultBg(view);
                llTrack.setTag(false);
                llRailings.setTag(false);
                break;
            case R.id.llTrack:
                if (view.getTag() == null || !(Boolean) view.getTag()) {
                    if (currentStudent == null) {
                        Toast.makeText(mContext, R.string.toast_choose_student, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cancelRTLocationTimer();
                    getHistoryTrackByStu();
                } else {
                    mHandler.removeCallbacksAndMessages(null);
                }
                llBindRoad.setVisibility(View.VISIBLE);
                resetDefaultBg(view);
                llLocation.setTag(false);
                llRailings.setTag(false);
                break;
            case R.id.switchBindRoad:
                isBindRoadForHistoryTrack = !isBindRoadForHistoryTrack;
                switchBindRoad.setBackgroundResource(isBindRoadForHistoryTrack ? R.drawable.layer_switch_on : R.drawable.layer_switch_off);
                getHistoryTrackByStu();
                break;
            case R.id.llRailings:
                if (view.getTag() == null || !(Boolean) view.getTag()) {
                    if (currentStudent == null) {
                        Toast.makeText(mContext, R.string.toast_choose_student, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.i(TAG, "viewClick: llRailings " + false);
                    mHandler.removeCallbacksAndMessages(null);
                    cancelRTLocationTimer();
                    getStudentRail();
                }
                llBindRoad.setVisibility(View.GONE);
                resetDefaultBg(view);
                llTrack.setTag(false);
                llLocation.setTag(false);
                break;
            case R.id.llAlarm:
                startActivity(new Intent(getContext(), AlarmActivity.class));
                break;
            case R.id.llMyLocation:
                MapFragmentPermissionsDispatcher.onLocationPermitWithCheck(this);
                break;
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void onLocationPermit() {
        Log.i(TAG, "onLocationPermit: ");
        isFirstLoc = true;
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void onLocationDenied() {
        Log.i(TAG, "onLocationDenied: ");
        Toast.makeText(this.getContext(), R.string.permission_location_denied, Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForLocation(PermissionRequest request) {
        Log.i(TAG, "showRationaleForLocation: ");
        request.proceed();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void onLocationNeverAskAgain() {
        Log.i(TAG, "onLocationNeverAskAgain: ");
        Toast.makeText(this.getContext(), R.string.permission_location_never_askagain, Toast.LENGTH_SHORT).show();
    }

    private void resetDefaultBg(View currentView) {
        llLocation.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_item_selector));
        llTrack.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_item_selector));
        llRailings.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_item_selector));

        if (currentView.getTag() == null || !(Boolean) currentView.getTag()) {
            currentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_item_selected));
            currentView.setTag(true);
        } else {
            currentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.map_item_normal));
            currentView.setTag(false);
        }
        //设置其他按钮tag 为 false
    }

    private void showSDatePop() {
        if (sDatePopup == null) {
            sDatePopup = new TimePickerPopupWindow(this.getContext(), txtSDate.getText().toString(),
                    new TimePickerPopupWindow.OnTimePickerClickListener() {

                        @Override
                        public void onTimePickerResult(String result) {
                            if (!result.isEmpty()) {
                                txtSDate.setText(result);
                                startTime = result;
                            }
                            if (currentStudent == null) {
                                return;
                            }

                            if (llTrack.getTag() != null && (Boolean) llTrack.getTag()) {
                                getHistoryTrackByStu();
                            }
                        }
                    });
            sDatePopup.setTouchable(true);
            sDatePopup.setBackgroundDrawable(new ColorDrawable());
            sDatePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    flTransparent.setVisibility(View.GONE);
                }
            });
        }
        flTransparent.setVisibility(View.VISIBLE);
        sDatePopup.showAsDropDown(txtSDate, 0, 3);
    }

    private void showEDatePop() {
        if (eDatePopup == null) {
            eDatePopup = new TimePickerPopupWindow(this.getContext(), txtEDate.getText().toString(),
                    new TimePickerPopupWindow.OnTimePickerClickListener() {

                        @Override
                        public void onTimePickerResult(String result) {
                            if (!result.isEmpty()) {
                                txtEDate.setText(result);
                                endTime = result;
                            }
                            if (currentStudent == null) {
                                return;
                            }

                            if (llTrack.getTag() != null && (Boolean) llTrack.getTag()) {
                                getHistoryTrackByStu();
                            }
                        }
                    });
            eDatePopup.setTouchable(true);
            eDatePopup.setBackgroundDrawable(new ColorDrawable());
            eDatePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    flTransparent.setVisibility(View.GONE);
                }
            });
        }
        flTransparent.setVisibility(View.VISIBLE);
        eDatePopup.showAsDropDown(txtEDate, 0, 3);
    }

    public void getStudents() {
        Log.i(TAG, "getStudentByClass: ");
        if (studentPopupWindowView == null) {
            studentPopupWindowView = new StudentPopupWindowView(getContext());
            studentPopupWindowView.setMyGridViewClickListener(new StudentAdapter.MyGridViewClickListener() {
                @Override
                public void onGridViewItemClick(BeanStudent student) {
                    studentPopup.dismiss();
                    if (student == null) {
                        return;
                    }
                    txtStudentName.setText(student.getStu_name());
                    //根据学生id获取学生位置
                    if (student != currentStudent) {
                        currentStudent = student;
                        locationTime = 0;
                    } else {
                        return;
                    }

                    Log.i(TAG, "onGridViewItemClick: " + student.getStu_name());
                    //获取实时位置or历史轨迹or电子围栏
                    if (llLocation.getTag() != null && (Boolean) llLocation.getTag()) {
                        getRealTimeLocationByStu();
                    }
                    if (llTrack.getTag() != null && (Boolean) llTrack.getTag()) {
                        getHistoryTrackByStu();
                    }
                    if (llRailings.getTag() != null && (Boolean) llRailings.getTag()) {
                        getStudentRail();
                    }
                }
            });
        }
    }

    private void showStudent() {
        if (studentPopup == null) {
            studentPopup = new PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT,
                    XPTApplication.getInstance().getWindowHeight() / 2);
            studentPopup.setFocusable(true);
            studentPopup.setTouchable(true);
            studentPopup.setBackgroundDrawable(new BitmapDrawable());
            studentPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    flTransparent.setVisibility(View.GONE);
                    txtStudentName.collapse();
                }
            });
        }

        studentPopup.setContentView(studentPopupWindowView);

        flTransparent.setVisibility(View.VISIBLE);
        studentPopup.showAsDropDown(txtStudentName, 0, 5);
    }

    private void startRTLocationTimer() {
        locationTime = 0;
        task = new TimerTask() {
            @Override
            public void run() {
                getRealTimeLocationByStu();
            }
        };
        if (timer == null) {
            timer = new Timer(true);
        }
        //start the timer
        timer.schedule(task, 1000, 60 * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelRTLocationTimer();
    }

    private void cancelRTLocationTimer() {
        // Stop Timer
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
        }
    }

    private void getRealTimeLocationByStu() {
        locationTime++;
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Track_RealTime, new VolleyHttpParamsEntity()
                        .addParam("stu_id", currentStudent.getStu_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Track_RealTime)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        mHandler.sendEmptyMessage(SHOW_PROGRESS);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        mHandler.sendEmptyMessage(HIDE_PROGRESS);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    BeanRTLocation rtLocation = gson.fromJson(volleyHttpResult.getData().toString(), BeanRTLocation.class);

                                    Message message = new Message();
                                    message.what = DrawLocation;
                                    message.arg1 = locationTime;
                                    message.obj = rtLocation;
                                    mHandler.sendMessage(message);
                                } catch (Exception ex) {
                                    Toast.makeText(mContext, "解析失败：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(mContext, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                cancelRTLocationTimer();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        mHandler.sendEmptyMessage(HIDE_PROGRESS);
                        cancelRTLocationTimer();
                    }
                });
    }

    private void getHistoryTrackByStu() {
        String state_bind_road = "0";
        if (isBindRoadForHistoryTrack) {
            state_bind_road = "1";
        }

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Track_HistoryTrack, new VolleyHttpParamsEntity()
                        .addParam("sdate", startTime + ":00")
                        .addParam("edate", endTime + ":00")
                        .addParam("state", state_bind_road)
                        .addParam("stu_id", currentStudent.getStu_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Track_HistoryTrack)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        if (progress_bar != null)
                            progress_bar.setVisibility(View.VISIBLE);
                        if (switchBindRoad != null)
                            switchBindRoad.setEnabled(false);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        if (progress_bar != null)
                            progress_bar.setVisibility(View.GONE);
                        if (switchBindRoad != null)
                            switchBindRoad.setEnabled(true);

                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    List<BeanHTLocation> listLocations = gson.fromJson(volleyHttpResult.getData().toString(),
                                            new TypeToken<List<BeanHTLocation>>() {
                                            }.getType());
                                    drawTrack(listLocations);
                                } catch (Exception ex) {
                                    Toast.makeText(mContext, "解析错误" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(mContext, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        if (progress_bar != null)
                            progress_bar.setVisibility(View.GONE);
                        if (switchBindRoad != null)
                            switchBindRoad.setEnabled(true);
                    }
                });
    }

    private void getStudentRail() {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Track_StudentFence, new VolleyHttpParamsEntity()
                        .addParam("stu_id", currentStudent.getStu_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Track_StudentFence)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        if (progress_bar != null)
                            progress_bar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        if (progress_bar != null)
                            progress_bar.setVisibility(View.GONE);

                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    List<BeanRail> listFence = gson.fromJson(volleyHttpResult.getData().toString(),
                                            new TypeToken<List<BeanRail>>() {
                                            }.getType());
                                    if (listFence.size() == 0) {
                                        Toast.makeText(mContext, R.string.toast_fence_empty, Toast.LENGTH_SHORT).show();
                                    }
                                    drawRail(listFence);
                                } catch (Exception ex) {
                                    Toast.makeText(mContext, "解析错误" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(mContext, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        if (progress_bar != null)
                            progress_bar.setVisibility(View.GONE);
                    }
                });
    }

}
