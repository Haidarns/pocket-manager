package id.hnslabs.pocketmanager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class StatistikActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistik);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LineChart chart = (LineChart) findViewById(R.id.chart);

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("1.Q"); xVals.add("2.Q"); xVals.add("3.Q"); xVals.add("4.Q");

        LineData data = new LineData(xVals, getDataSet());
        chart.setData(data);
        chart.setDescription("My Chart");
        chart.animateXY(2000, 2000);
        chart.invalidate();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private ArrayList<LineDataSet> getDataSet() {
        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        ArrayList<Entry> valsComp1 = new ArrayList<>();

        Entry c1e1 = new Entry(100.000f, 0); // 0 == quarter 1
        valsComp1.add(c1e1);
        Entry c1e2 = new Entry(50.000f, 1); // 1 == quarter 2 ...
        valsComp1.add(c1e2);
        Entry c2e1 = new Entry(120.000f, 2); // 0 == quarter 1
        valsComp1.add(c2e1);
        Entry c2e2 = new Entry(110.000f, 3); // 1 == quarter 2 ...
        valsComp1.add(c2e2);

        LineDataSet lineDataSet = new LineDataSet(valsComp1,"Line 1");
        dataSets.add(lineDataSet);

        return dataSets;
    }
}
