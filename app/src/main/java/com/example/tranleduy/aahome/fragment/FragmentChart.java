package com.example.tranleduy.aahome.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.tranleduy.aahome.AbstractTheme;
import com.example.tranleduy.aahome.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Random;

import com.example.tranleduy.aahome.data.Database;
import com.example.tranleduy.aahome.items.ChartItem;

/**
 * Created by ${DUY} on 18-Jun-16.
 */
public class FragmentChart extends Fragment implements AbstractTheme.ChartListener {
    private XYMultipleSeriesRenderer muiltiTempRenderer, muiltiHumiRenderer;
    private XYMultipleSeriesDataset tempDataset, humiDataset;
    private GraphicalView chartTemp, chartHumidity;
    private Context context;
    private Database database;
    private ArrayList<ChartItem> chartItems;
    private LinearLayout chartTempContainer, chartHumiContainer;

    public FragmentChart(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_chart, container, false);
        chartTempContainer = (LinearLayout) view.findViewById(R.id.main_chart_temp);
        chartHumiContainer = (LinearLayout) view.findViewById(R.id.main_chart_humi);
        getData();
        setupChartTemp();
        setupChartHumi();
//        chartTemp = ChartFactory.getLineChartView(context, tempDataset, muiltiTempRenderer);
//        chartHumidity = ChartFactory.getLineChartView(context, humiDataset, muiltiHumiRenderer);
        initView();
        return view;
    }

    private void initView() {
        chartHumidity = ChartFactory.getBarChartView(context, humiDataset,
                muiltiHumiRenderer, BarChart.Type.HEAPED);
        chartTemp = ChartFactory.getLineChartView(context, tempDataset,
                muiltiTempRenderer);
        chartTempContainer.addView(chartTemp);
        chartHumiContainer.addView(chartHumidity);

        chartHumiContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ChartFactory.getBarChartIntent(context, humiDataset,
                        muiltiHumiRenderer,
                        BarChart.Type.DEFAULT, "ĐỘ ẨM");
                startActivity(intent);
            }
        });

        chartTempContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ChartFactory.getLineChartIntent(context, tempDataset,
                        muiltiTempRenderer,
//                        BarChart.Type.DEFAULT,
                        "NHIỆT ĐỘ");
                startActivity(intent);
            }
        });
    }

    private void setupChartHumi() {
        XYSeries humiSeries = getSeriesHumi();

        humiDataset.addSeries(humiSeries);

        // TODO: 18-Jun-16

        XYSeriesRenderer humiRenderer = new XYSeriesRenderer();
        humiRenderer.setColor(Color.BLUE); //color of the graph set to cyan
        humiRenderer.setFillPoints(true);
        humiRenderer.setLineWidth(3);
        humiRenderer.setPointStyle(PointStyle.CIRCLE);
        humiRenderer.setStroke(BasicStroke.SOLID);


        muiltiHumiRenderer.setChartTitle("Độ ẩm");
        muiltiHumiRenderer.setXAxisColor(Color.BLUE);
        muiltiHumiRenderer.setYAxisColor(Color.BLUE);

//        muiltiTempRenderer.setZoomEnabled(true);
//        muiltiTempRenderer.setZoomButtonsVisible(true);
//        muiltiTempRenderer.setExternalZoomEnabled(true);

        muiltiHumiRenderer.setShowGrid(true);
        muiltiHumiRenderer.setGridColor(R.color.colorAccent);
        muiltiHumiRenderer.setMargins(new int[]{30, 30, 30, 30});

        muiltiHumiRenderer.setBackgroundColor(Color.WHITE);
        muiltiHumiRenderer.setApplyBackgroundColor(true);
        muiltiHumiRenderer.setMarginsColor(Color.WHITE);

        muiltiHumiRenderer.setXTitle("Thời gian");
        muiltiHumiRenderer.setXLabelsColor(Color.BLUE);
        muiltiHumiRenderer.setYTitle("%");
        muiltiHumiRenderer.setYLabelsColor(0, Color.BLUE);

        muiltiHumiRenderer.setXAxisMax(24);
        muiltiHumiRenderer.setXAxisMin(0);
        muiltiHumiRenderer.setYAxisMax(100);
        muiltiHumiRenderer.setYAxisMin(0);
        //add com.example.tranleduy.aahome.data
        muiltiHumiRenderer.addSeriesRenderer(humiRenderer);
    }

    private XYSeries getSeriesHumi() {
        XYSeries humiSeries = new XYSeries("Độ ẩm");
        for (int i = 0; i < 24; i++) {
//            Date date = new Date(com.example.tranleduy.aahome.data.get(i).getMillisSecond());
            humiSeries.add(i, new Random().nextInt(100));
        }
        return humiSeries;
    }

    private XYSeries getSeriesTemp() {
        XYSeries tempSeries = new XYSeries("Nhiệt độ");
        for (int i = 0; i < 24; i++) {
//            Date date = new Date(com.example.tranleduy.aahome.data.get(i).getMillisSecond());
            tempSeries.add(i, new Random().nextInt(60));
        }
        return tempSeries;

    }

    private void setupChartTemp() {

        XYSeries temperatureSeries = getSeriesTemp();
        tempDataset.addSeries(temperatureSeries);

        // TODO: 18-Jun-16

        XYSeriesRenderer tempRenderer = new XYSeriesRenderer();
        tempRenderer.setColor(Color.RED); //color of the graph set to cyan
        tempRenderer.setFillPoints(true);
        tempRenderer.setLineWidth(3);
        tempRenderer.setPointStyle(PointStyle.CIRCLE);
        tempRenderer.setStroke(BasicStroke.SOLID);


        muiltiTempRenderer.setChartTitle("Nhiệt độ");
        muiltiTempRenderer.setXAxisColor(Color.RED);
        muiltiTempRenderer.setYAxisColor(Color.RED);

//        muiltiTempRenderer.setZoomEnabled(true);
//        muiltiTempRenderer.setZoomButtonsVisible(true);
//        muiltiTempRenderer.setExternalZoomEnabled(true);

        muiltiTempRenderer.setShowGrid(true);
        muiltiTempRenderer.setGridColor(R.color.colorAccent);
        muiltiTempRenderer.setMargins(new int[]{30, 30, 30, 30});

        muiltiTempRenderer.setBackgroundColor(Color.WHITE);
        muiltiTempRenderer.setApplyBackgroundColor(true);
        muiltiTempRenderer.setMarginsColor(Color.WHITE);

        muiltiTempRenderer.setXTitle("Thời gian");
        muiltiTempRenderer.setXLabelsColor(Color.RED);
        muiltiTempRenderer.setYTitle("độ C");
        muiltiTempRenderer.setYLabelsColor(0, Color.RED);

        muiltiTempRenderer.setXAxisMax(24);
        muiltiTempRenderer.setXAxisMin(0);
        muiltiTempRenderer.setYAxisMax(60);
        muiltiTempRenderer.setYAxisMin(0);

        //add com.example.tranleduy.aahome.data
        muiltiTempRenderer.addSeriesRenderer(tempRenderer);
    }

    private void getData() {
        database = new Database(context);
        chartItems = database.getAllChartItem();
        muiltiHumiRenderer = new XYMultipleSeriesRenderer();
        muiltiTempRenderer = new XYMultipleSeriesRenderer();
        tempDataset = new XYMultipleSeriesDataset();
        humiDataset = new XYMultipleSeriesDataset();
    }

    @Override
    public void updateChart() {
        Log.e("Chart update", "ok");
        getData();
        setupChartHumi();
        setupChartTemp();
        chartHumidity.post(new Runnable() {
            @Override
            public void run() {
                chartHumidity.repaint();
            }
        });
        chartTemp.post(new Runnable() {
            @Override
            public void run() {
                chartTemp.repaint();
            }
        });
    }

}
