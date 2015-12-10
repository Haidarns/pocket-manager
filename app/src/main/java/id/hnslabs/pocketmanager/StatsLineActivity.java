package id.hnslabs.pocketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import id.hnslabs.pocketmanager.Adapter.RecViewAdapter;
import id.hnslabs.pocketmanager.Model.InOutTransModel;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class StatsLineActivity extends AppCompatActivity {
    private boolean showMode = true; //true = income, false = outcome
    private Realm realm;
    private List<String> xVals;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistik);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PieChart pc = (PieChart) findViewById(R.id.chart_pie);
        pc.setVisibility(View.GONE);

        realm = Realm.getInstance(getApplicationContext());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Statistik Pemasukan");

        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        chart = (LineChart) findViewById(R.id.chart);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createLineChart(showMode);
        createRecViewList(separateDataInOut(showMode));
    }

    private void createRecViewList(final RealmResults<InOutTransModel> results){

        adapter = new RecViewAdapter(results, StatsLineActivity.this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ((RecViewAdapter) adapter).setOnItemClickListener(new RecViewAdapter.RecViewClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i("Recycler Click", "Target : " + results.get(position).getId());
                Intent it = new Intent(getApplicationContext(), EditActivity.class);
                it.putExtra("actMode", EditActivity.LIHAT);
                it.putExtra("dataId", results.get(position).getId());
                startActivity(it);
            }
        });
    }

    private void createLineChart(boolean mode){
        LineData data;
        LineDataSet lds;

        if(mode) {
            lds = getDataSet(separateDataInOut(true),true);
            //chart.setDescription("Chart Pemasukan");
        } else {
            lds = getDataSet(separateDataInOut(false), false);
            //chart.setDescription("Chart Pengeluaran");
        }

        lds.setValueTextSize(10);
        lds.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator(',');
                symbols.setGroupingSeparator('.');

                DecimalFormat df = new DecimalFormat("#,##0.##", symbols);

                String mataUang = "Rp ";

                return mataUang + df.format(v);
            }
        });

        data = new LineData(xVals, lds);

        chart.setBackgroundColor(getResources().getColor(R.color.white));
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setData(data);
        chart.animateY(1000);
        chart.setDescription(" ");
        chart.invalidate();
    }

    private LineDataSet getDataSet(RealmResults<InOutTransModel> modelsOut, boolean mode){
        List<Entry> dataList = new ArrayList<>();
        List<String> dataListLabel = new ArrayList<>();
        List<Float> nominals = new ArrayList<>();
        String setBaseDay = "0";
        int dataIndex = 7;

        //set initial
        for (int i = 0; i < 7; i++) {
            nominals.add(i,0f);
            dataListLabel.add(i,"");
        }

        for (int i = 0; i < modelsOut.size(); i++) {
            InOutTransModel model = modelsOut.get(i);
            String[] formatTemp = model.getCreatedTime().split("-");
            String[] dateTemp = formatTemp[0].split("/");

            if (i < 6) {
                if (!dateTemp[2].equals(setBaseDay)) {
                    dataIndex -= 1;
                    setBaseDay = dateTemp[2];
                    nominals.set(dataIndex, model.getJumlah());

                    String setDateText = dateTemp[2]+"/"+dateTemp[1];

                    dataListLabel.set(dataIndex, setDateText);
                } else {
                    float tmpF = nominals.get(dataIndex);
                    tmpF += model.getJumlah();
                    nominals.set(dataIndex, tmpF);
                }
            }
        }

        for (int i = 0; i <7 ; i++) {
            dataList.add(new Entry(nominals.get(i), i));
        }

        xVals = dataListLabel;

        int color;
        String text;

        if (mode) {
            text = "Pemasukan";
            color = getResources().getColor(R.color.income);
        } else {
            text = "Pengeluaran";
            color = getResources().getColor(R.color.outcome);
        }

        LineDataSet dataSet = new LineDataSet(dataList, text);
        dataSet.setColor(color);
        dataSet.setDrawCircles(false);

        return dataSet;
    }

    private RealmResults<InOutTransModel> separateDataInOut(boolean mode){
        RealmResults<InOutTransModel> results = realm
                .where(InOutTransModel.class)
                .equalTo("inOut", mode)
                .findAll();

        results.sort("id", Sort.DESCENDING);

        return results;
    }

    private void separateDay(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act_menu_stats, menu);
        if(showMode){
            menu.getItem(0).setVisible(false);
        } else {
            menu.getItem(1).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_show_income :
                getSupportActionBar().setTitle("Statistik Pemasukan");
                supportInvalidateOptionsMenu();
                showMode = true;
                createLineChart(showMode);
                createRecViewList(separateDataInOut(showMode));
                return true;
            case R.id.action_show_outcome :
                getSupportActionBar().setTitle("Statistik Pengeluaran");
                supportInvalidateOptionsMenu();
                showMode = false;
                createLineChart(showMode);
                createRecViewList(separateDataInOut(showMode));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
