package com.shuhai.anfang.report.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.volley.common.VolleyRequestListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuhai.anfang.report.R;
import com.shuhai.anfang.report.http.HttpAction;
import com.shuhai.anfang.report.module.AppModuleCount;
import com.shuhai.anfang.report.module.AppUseCount;
import com.shuhai.anfang.report.module.UserCount;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class Report3View extends BaseReportView {

    private TextView txtTeacherSum, txtTeacherOnline, txtParentSum, txtParentOnline;
    private LineChart[] lineCharts;
    int[] colors1 = new int[]{getResources().getColor(R.color.color_line_chart_ios),
            getResources().getColor(R.color.color_line_chart)};
    String[] row1Val = new String[]{"苹果端", "安卓端"};
    private UserCount userCount;    //用户数量
    private AppUseCount appUseCount;    //app使用次数
    private AppModuleCount appModuleCount;  //app功能使用次数
    private int[] chats = new int[24];    //聊天统计

    public Report3View(Context context) {
        this(context, null);
    }

    public Report3View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_report3, this, true);
        initView();
    }

    private void initView() {
        txtTeacherSum = (TextView) findViewById(R.id.txtTeacherSum);
        txtTeacherOnline = (TextView) findViewById(R.id.txtTeacherOnline);
        txtParentSum = (TextView) findViewById(R.id.txtParentSum);
        txtParentOnline = (TextView) findViewById(R.id.txtParentOnline);

        lineCharts = new LineChart[6];
        lineCharts[0] = (LineChart) findViewById(R.id.chart1);
        lineCharts[1] = (LineChart) findViewById(R.id.chart2);
        lineCharts[2] = (LineChart) findViewById(R.id.chart3);
        lineCharts[3] = (LineChart) findViewById(R.id.chart4);
        lineCharts[4] = (LineChart) findViewById(R.id.chart5);
        lineCharts[5] = (LineChart) findViewById(R.id.chart6);

        setLineChartLegend(lineCharts[0], colors1, row1Val);
        setLineChartLegend(lineCharts[1], colors1, row1Val);

        int[] colors21 = new int[]{getResources().getColor(R.color.color_line_chart_ios),
                getResources().getColor(R.color.color_line_chart),
                getResources().getColor(R.color.color_line_chart_3)};
        String[] row21Val = new String[]{"查看位置", "发布作业", "处理审批"};
        setLineChartLegend(lineCharts[2], colors21, row21Val);

        int[] colors22 = new int[]{getResources().getColor(R.color.color_line_chart_ios),
                getResources().getColor(R.color.color_line_chart),
                getResources().getColor(R.color.color_line_chart_3),
                getResources().getColor(R.color.color_line_chart_4)};
        String[] row22Val = new String[]{"查看位置", "查看作业", "发布审批", "学生卡设置"};
        setLineChartLegend(lineCharts[3], colors22, row22Val);

        lineCharts[4].getLegend().setEnabled(false);

        int[] colors3 = new int[]{getResources().getColor(R.color.color_line_chart_ios),
                getResources().getColor(R.color.color_line_chart)};
        String[] row3Val = new String[]{"点击数量", "显示数量"};
        setLineChartLegend(lineCharts[5], colors3, row3Val);
    }

    @Override
    public void loadData() {
        super.loadData();
        getUserCount();
        getAppUseCount();
        getAppModuleCount();
        getAppChatCount();
    }

    private void getUserCount() {
        VolleyHttpService.getInstance().sendGetRequest(HttpAction.USER_COUNT_INFO, new VolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            Gson gson = new Gson();
                            userCount = gson.fromJson(volleyHttpResult.getData().toString(),
                                    new TypeToken<UserCount>() {
                                    }.getType());
                            setLeftUser(userCount);
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse error: " + ex.getMessage());
                            setLeftUser(userCount);
                        }
                        break;
                    default:
                        setLeftUser(userCount);
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                setLeftUser(userCount);
            }
        });
    }

    private void setLeftUser(UserCount userCount) {
        if (userCount == null) {
            return;
        }
        txtTeacherSum.setText(userCount.getTeacher() + "");
        txtTeacherOnline.setText(userCount.getTeacher_online() + "");
        txtParentSum.setText(userCount.getParents() + "");
        txtParentOnline.setText(userCount.getParents_online() + "");
    }

    private void getAppUseCount() {
        VolleyHttpService.getInstance().sendGetRequest(HttpAction.APP_COUNT_INFO, new VolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            Gson gson = new Gson();
                            appUseCount = gson.fromJson(volleyHttpResult.getData().toString(),
                                    new TypeToken<AppUseCount>() {
                                    }.getType());
                            drawAppUseCount(appUseCount);
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse error: " + ex.getMessage());
                            drawAppUseCount(appUseCount);
                        }
                        break;
                    default:
                        drawAppUseCount(appUseCount);
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                drawAppUseCount(appUseCount);
            }
        });
    }

    private void drawAppUseCount(AppUseCount appUseCount) {
        if (appUseCount == null) {
            return;
        }
        List<int[]> chart1Val = new ArrayList<int[]>();
        chart1Val.add(appUseCount.getIOSteacher());
        chart1Val.add(appUseCount.getAndroidteacher());
        setLineChartStyle(lineCharts[0], chart1Val, colors1, colors1);

        List<int[]> chart2Val = new ArrayList<int[]>();
        chart2Val.add(appUseCount.getIOSparents());
        chart2Val.add(appUseCount.getAndroidparents());
        setLineChartStyle(lineCharts[1], chart2Val, colors1, colors1);
    }

    private void getAppModuleCount() {
        VolleyHttpService.getInstance().sendGetRequest(HttpAction.APP_MODULE_COUNT_INFO, new VolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            Log.i(TAG, "onResponse: " + volleyHttpResult.getData().toString());
                            Gson gson = new Gson();
                            appModuleCount = gson.fromJson(volleyHttpResult.getData().toString(),
                                    new TypeToken<AppModuleCount>() {
                                    }.getType());
                            drawAppModuleCount(appModuleCount);
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse error: " + ex.getMessage());
                            drawAppModuleCount(appModuleCount);
                        }
                        break;
                    default:
                        drawAppModuleCount(appModuleCount);
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                drawAppModuleCount(appModuleCount);
            }
        });
    }

    private void drawAppModuleCount(AppModuleCount appModuleCount) {
        if (appModuleCount == null) {
            return;
        }
        int[] colors21 = new int[]{getResources().getColor(R.color.color_line_chart_ios),
                getResources().getColor(R.color.color_line_chart),
                getResources().getColor(R.color.color_line_chart_3)};
        List<int[]> chart1Val = new ArrayList<int[]>();
        chart1Val.add(appModuleCount.getTeaTrack());
        chart1Val.add(appModuleCount.getTeaHomework());
        chart1Val.add(appModuleCount.getTeaLeave());
        setLineChartStyle(lineCharts[2], chart1Val, colors21, colors21);

        int[] colors2 = new int[]{getResources().getColor(R.color.color_line_chart_ios),
                getResources().getColor(R.color.color_line_chart),
                getResources().getColor(R.color.color_line_chart_3),
                getResources().getColor(R.color.color_line_chart_4)};

        List<int[]> chart2Val = new ArrayList<int[]>();
        chart2Val.add(appModuleCount.getParTrack());
        chart2Val.add(appModuleCount.getParHomework());
        chart2Val.add(appModuleCount.getParLeave());
        chart2Val.add(appModuleCount.getParStuCard());
        setLineChartStyle(lineCharts[3], chart2Val, colors2, colors2);

        int[] colors3 = new int[]{getResources().getColor(R.color.color_line_chart_ios),
                getResources().getColor(R.color.color_line_chart)};
        List<int[]> chart3Val = new ArrayList<int[]>();
        chart3Val.add(appModuleCount.getBannerClick());
        chart3Val.add(appModuleCount.getBannerView());
        setLineChartStyle(lineCharts[5], chart3Val, colors3, colors3);
    }

    private void getAppChatCount() {
        VolleyHttpService.getInstance().sendGetRequest(HttpAction.APP_CHAT_INFO, new VolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            JSONArray array = new JSONArray(volleyHttpResult.getData().toString());
                            chats = new int[array.length()];
                            for (int i = 0; i < array.length(); i++) {
                                chats[i] = (int) array.get(i);
                            }
                            drawChats(chats);
                        } catch (Exception ex) {
                            Log.i(TAG, "getAppChatCount: " + ex.getMessage());
                            drawChats(chats);
                        }
                        break;
                    default:
                        drawChats(chats);
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                drawChats(chats);
            }
        });
    }

    private void drawChats(int[] chats) {
        int[] colors2 = new int[]{getResources().getColor(R.color.color_line_chart)};
        List<int[]> chart2Val = new ArrayList<int[]>();
        chart2Val.add(chats);
        setLineChartStyle(lineCharts[4], chart2Val, colors2, colors2);
    }

    private void setLineChartStyle(LineChart mChart, List<int[]> y_values, int[] lineColors, int[] circleColors) {
        mChart.getDescription().setEnabled(false);
        // enable touch gestures
        mChart.setTouchEnabled(false);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
//        mChart.getLegend().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(8.0f);
        xAxis.setLabelCount(24);
        xAxis.setAxisLineWidth(0.8f);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(getResources().getColor(R.color.color_x_axis));
        xAxis.setDrawGridLines(false);

        int hourCount = 24;
        int maxYVal = 0;
        for (int j = 0; j < y_values.size(); j++) {
            for (int i = 0; i < hourCount; i++) {
                int val = y_values.get(j)[i];
                if (val > maxYVal) {
                    maxYVal = val;
                }
            }
        }

        maxYVal += maxYVal * 0.2;

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisLineColor(getResources().getColor(R.color.color_y_axis));
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineWidth(0.8f);
        leftAxis.setAxisMaximum((int) maxYVal);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setLabelCount(4);
        leftAxis.setTextSize(8f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        mChart.getAxisRight().setEnabled(false);

        setLineData(mChart, y_values, lineColors, circleColors);
    }

    private void setLineChartLegend(LineChart mChart, int[] colors, String[] values) {
        Legend l = mChart.getLegend();
        l.setEnabled(true);
//        if (l.getEntries()[0].label != null && !l.getEntries()[0].label.isEmpty()) {
//            return;
//        }
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(9f);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setTypeface(mTfLight);
        l.setYOffset(0f);
        l.setYEntrySpace(0.0f);

        l.setXEntrySpace(1.0f);
        l.setXOffset(values.length * 8f);

        List<LegendEntry> legendEntries = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            LegendEntry entry1 = new LegendEntry();
            entry1.label = values[i];
            entry1.formColor = colors[i];
            entry1.formLineWidth = 3f;
            legendEntries.add(entry1);
        }
        l.setCustom(legendEntries);
    }

    private void setLineData(LineChart mChart, List<int[]> yValues, int[] lineColors, int[] circleColors) {
        LineData data = new LineData();
        int length = yValues.size();
        int count = yValues.get(0).length;

        for (int j = 0; j < length; j++) {
            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            for (int i = 0; i < count; i++) {
                yVals1.add(new Entry(i, yValues.get(j)[i]));
            }
            LineDataSet set1 = new LineDataSet(yVals1, "");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(lineColors[j]);
            set1.setCircleColor(circleColors[j]);
            set1.setLineWidth(1f);
            set1.setCircleRadius(2f);
            set1.setValues(yVals1);
            data.addDataSet(set1);
        }
        data.setDrawValues(false);
        // set data
        mChart.setData(data);

        mChart.animateXY(1000, 1000);
    }

    @Override
    public void animationReportXY() {
        super.animationReportXY();
//        Log.i(TAG, "animationReportXY: report3");
//        for (int i = 0; i < lineCharts.length; i++) {
//            LineChart lineChart = lineCharts[i];
//            if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0)
//                lineChart.animateXY(1000, 1000);
//        }
    }
}
