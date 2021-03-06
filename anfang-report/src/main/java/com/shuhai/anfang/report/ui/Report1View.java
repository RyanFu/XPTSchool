package com.shuhai.anfang.report.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.volley.common.VolleyRequestListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuhai.anfang.report.R;
import com.shuhai.anfang.report.http.HttpAction;
import com.shuhai.anfang.report.module.BarProvinceInfo;
import com.shuhai.anfang.report.module.PieAllStuCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class Report1View extends BaseReportView {

    private PieChart mPieChart;
    private TextView txtAllCard;
    private BarChart[] listBarCharts = null;
    private Context mContext;
    private PieAllStuCard pieAllStuCard;
    private BarProvinceInfo provinceInfo;

    public Report1View(Context context) {
        this(context, null);
    }

    public Report1View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_report1, this, true);
//        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        initPieView();
        initBarView();
        loadData();
    }

    @Override
    public void loadData() {
        super.loadData();
        getPieData();
        getBarData();
    }

    private void initPieView() {
        txtAllCard = (TextView) findViewById(R.id.txtAllCard);
        mPieChart = (PieChart) findViewById(R.id.chart1);
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setCenterTextTypeface(mTfLight);
//        mPieChart.setCenterText(generateCenterSpannableText());

        //外部间距
        mPieChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        //绘制中心圆
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(getResources().getColor(R.color.color_translucent));

//        mPieChart.setTransparentCircleColor(Color.WHITE);
//        mPieChart.setTransparentCircleAlpha(110);

        //中心园半径
        mPieChart.setHoleRadius(58f);
        mPieChart.setTransparentCircleRadius(61f);

        mPieChart.setDrawCenterText(true);
        mPieChart.setCenterText("学生卡使用统计");
        mPieChart.setCenterTextSize(20.0f);
        mPieChart.setEntryLabelTextSize(8f);
        mPieChart.setCenterTextColor(getResources().getColor(R.color.color_white));

        mPieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);

        //图例
        mPieChart.getLegend().setEnabled(false);
    }

    private void getPieData() {
        VolleyHttpService.getInstance().sendGetRequest(HttpAction.STU_CARD_PIE, new VolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            Gson gson = new Gson();
                            pieAllStuCard = gson.fromJson(volleyHttpResult.getData().toString(),
                                    new TypeToken<PieAllStuCard>() {
                                    }.getType());
                            setPieData(pieAllStuCard);
                        } catch (Exception ex) {
                            setPieData(pieAllStuCard);
                        }
                        break;
                    default:
                        setPieData(pieAllStuCard);
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                setPieData(pieAllStuCard);
            }
        });
    }

    private void setPieData(PieAllStuCard pieAllStuCard) {
        if (pieAllStuCard == null) {
            return;
        }

        txtAllCard.setText(pieAllStuCard.getTotal() + "");

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        List<PieAllStuCard.StuCardInfo> cardUsed = pieAllStuCard.getInfo();
        for (int i = 0; i < cardUsed.size(); i++) {
            entries.add(new PieEntry((float) cardUsed.get(i).getValue() / pieAllStuCard.getTotal(),
                    cardUsed.get(i).getName() + cardUsed.get(i).getValue()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "学生卡总量统计");
        ArrayList<Integer> colors = new ArrayList<Integer>();

        colors.add(getResources().getColor(R.color.color_used));
        colors.add(getResources().getColor(R.color.color_unused));

        dataSet.setColors(colors);
        dataSet.setValueLinePart1OffsetPercentage(70.f);
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.8f);
        dataSet.setValueLineWidth(1f);
        dataSet.setValueLineColor(Color.WHITE);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(mTfRegular);
        data.setValueTextSize(20f);
        mPieChart.setData(data);
        mPieChart.setEntryLabelTextSize(18f);

        // undo all highlights
        mPieChart.highlightValues(null);
        mPieChart.invalidate();
        mPieChart.animateXY(1000, 1000);
    }

    private void initBarView() {
        listBarCharts = new BarChart[3];
        listBarCharts[0] = (BarChart) findViewById(R.id.chart_bar1);
        listBarCharts[1] = (BarChart) findViewById(R.id.chart_bar2);
        listBarCharts[2] = (BarChart) findViewById(R.id.chart_bar3);
    }

    private void getBarData() {
        VolleyHttpService.getInstance().sendGetRequest(HttpAction.STU_CARD_BAR, new VolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            Gson gson = new Gson();
                            provinceInfo = gson.fromJson(volleyHttpResult.getData().toString(),
                                    new TypeToken<BarProvinceInfo>() {
                                    }.getType());
                            splitProvinceData(provinceInfo);
                            Log.i(TAG, "onResponse: " + provinceInfo.toString());
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse error: " + ex.getMessage());
                        }
                        break;
                    default:
                        splitProvinceData(provinceInfo);
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                splitProvinceData(provinceInfo);
            }
        });
    }

    private void splitProvinceData(BarProvinceInfo provinceInfo) {

        if (provinceInfo == null) {
            return;
        }

        int xMaxLength = 11;
        int group = xMaxLength;
        String[] allProvinces = provinceInfo.getProv();
        int[] allCard = provinceInfo.getInfo().get(0).getData();
        int[] allUsedCard = provinceInfo.getInfo().get(1).getData();

        for (int i = 0; i < listBarCharts.length; i++) {
            BarChart barChart = listBarCharts[i];

            if ((i + 1) * group >= allProvinces.length) {
                group = allProvinces.length - i * xMaxLength;
            }
            String[] provinces = new String[group];
            int[] splitUsed = new int[group];
            int[] splitUnused = new int[group];
            for (int j = 0; j < group; j++) {
                provinces[j] = allProvinces[i * xMaxLength + j];
                splitUsed[j] = allCard[i * xMaxLength + j];
                splitUnused[j] = allUsedCard[i * xMaxLength + j];
            }
            setBarData(barChart, provinces, splitUsed, splitUnused);
        }
    }

    private void setBarData(BarChart barChart, final String[] provinces, int[] allUsed, int[] unUsed) {
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);

        //图例
        Legend l = barChart.getLegend();
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(5f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setTextColor(getResources().getColor(R.color.color_white));
        l.setTypeface(mTfLight);
        l.setYOffset(0f);
        l.setXOffset(8f);
        l.setYEntrySpace(0.0f);
        l.setXEntrySpace(0.0f);
        l.setFormLineWidth(6f);
        l.setTextSize(10f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setGranularity(1.0f);  //粒度
        xAxis.setDrawAxisLine(true);

//        xAxis.disableAxisLineDashedLine();
        xAxis.setAxisLineColor(getResources().getColor(R.color.color_line));
        xAxis.setDrawGridLines(false);

        xAxis.setDrawLabels(true);
        xAxis.setTextSize(8.0f);
        xAxis.setAxisLineWidth(1.5f);
        xAxis.setLabelCount(12);
        xAxis.setCenterAxisLabels(true);
        xAxis.setTextColor(getResources().getColor(R.color.color_x_axis));

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                if (0 > value || index >= provinces.length) {
                    return "";
                }
                String xVal = provinces[index];
                Log.i(TAG, "getFormattedValue: " + xVal + "  value:" + value);
                return xVal;
            }
        });

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
//        leftAxis.setGridDashedLine(new DashPathEffect(new float[]{5f, 3f}, 3f));
        leftAxis.setLabelCount(4);
        leftAxis.setAxisLineColor(getResources().getColor(R.color.color_y_axis));
        leftAxis.setTextColor(getResources().getColor(R.color.color_white));
        leftAxis.setAxisLineWidth(0.8f);
        leftAxis.setTextSize(10.0f);
        leftAxis.setSpaceTop(15f);  //设置最高柱距顶部距离
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        barChart.getAxisRight().setEnabled(false);

        float groupSpace = 0.6f;
        float barSpace = 0.00f; // x2 DataSet
        float barWidth = 0.2f; // x2 DataSet
        // (0.4 + 0.06) * 2 + 0.08 = 1.00 -> interval per "group"

        int startYear = 0;
        int endYear = provinces.length;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();

        for (int i = startYear; i < endYear; i++) {
            yVals1.add(new BarEntry(i, allUsed[i]));
            yVals2.add(new BarEntry(i, unUsed[i]));
        }

        BarDataSet set1, set2;
        // create 2 DataSets
        set1 = new BarDataSet(yVals1, "总数量");
        set1.setValueTextColor(getResources().getColor(R.color.color_white));
        set1.setColor(getResources().getColor(R.color.color_used));
        set2 = new BarDataSet(yVals2, "正在使用数量");
        set2.setValueTextColor(getResources().getColor(R.color.color_white));
        set2.setColor(getResources().getColor(R.color.color_unused));

        BarData data = new BarData(set1, set2);
//        data.setValueFormatter(new LargeValueFormatter());
//        data.setValueTypeface(mTfLight);
//        data.setValueTextSize(4f);
        data.setDrawValues(false);

        barChart.setData(data);

        // specify the width each bar should have
        barChart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        barChart.getXAxis().setAxisMinimum(startYear);
        barChart.getXAxis().setAxisMaximum(endYear);

        barChart.groupBars(startYear, groupSpace, barSpace);
        barChart.invalidate();

        barChart.animateXY(1000, 1000);
    }

    @Override
    public void animationReportXY() {
        super.animationReportXY();
//        Log.i(TAG, "animationReportXY: report1");
//        for (int i = 0; i < listBarCharts.length; i++) {
//            BarChart barChart = listBarCharts[i];
//            if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0)
//                barChart.animateXY(1000, 1000);
//        }
    }
}
