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
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import id.hnslabs.pocketmanager.Adapter.RecViewAdapter;
import id.hnslabs.pocketmanager.Model.InOutTransModel;
import id.hnslabs.pocketmanager.Model.RealmConfig;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class StatsPieActivity extends AppCompatActivity {
    private boolean showMode = true; //true = income, false = outcome
    private Realm realm;
    private List<String> xVals;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private PieChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistik);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LineChart lc = (LineChart) findViewById(R.id.chart);
        lc.setVisibility(View.GONE);

        realm = Realm.getInstance(new RealmConfig(this,"datas").getConfig());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Statistik Pemasukan");

        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        chart = (PieChart) findViewById(R.id.chart_pie);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createPieChart(showMode);
        createRecViewList(separateDataInOut(showMode));
    }

    private void createRecViewList(final RealmResults<InOutTransModel> results){

        adapter = new RecViewAdapter(results, StatsPieActivity.this);

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

    private void createPieChart(boolean mode){
        PieData data;
        PieDataSet pds;

        if(mode) {
            pds = getDataSet(separateDataInOut(true),true);
            //chart.setDescription("Chart Pemasukan");
        } else {
            pds = getDataSet(separateDataInOut(false), false);
            //chart.setDescription("Chart Pengeluaran");
        }

        pds.setDrawValues(false);

        data = new PieData(xVals, pds);

        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.CIRCLE);

        chart.getLegend().setWordWrapEnabled(true);
        chart.setBackgroundColor(getResources().getColor(R.color.white));
        chart.setTouchEnabled(false);
        chart.setData(data);
        chart.animateY(1000);
        chart.setDescription(" ");
        chart.setDrawHoleEnabled(false);
        chart.setDrawSliceText(false);
        chart.invalidate();
    }

    private PieDataSet getDataSet(RealmResults<InOutTransModel> modelsOut, boolean mode){
        List<Entry> dataList = new ArrayList<>();
        List<String> dataListLabel = new ArrayList<>();
        List<Float> nominals = new ArrayList<>();
        String[] typeLabels;

        int[] colors = new int[]{
                getResources().getColor(R.color.clr1),
                getResources().getColor(R.color.clr2),
                getResources().getColor(R.color.clr3),
                getResources().getColor(R.color.clr4),
                getResources().getColor(R.color.clr5),
                getResources().getColor(R.color.clr6),
                getResources().getColor(R.color.clr7),
                getResources().getColor(R.color.clr8),
                getResources().getColor(R.color.clr9),
        };

        if (mode){
            typeLabels = getResources().getStringArray(R.array.income_str_array);
        } else {
            typeLabels = getResources().getStringArray(R.array.outcome_str_array);
        }

        int length = typeLabels.length;


        //set initial
        for (int i = 0; i < length; i++) {
            nominals.add(i,0f);
            dataListLabel.add(i, typeLabels[i]);
        }

        for (int i = 0; i < modelsOut.size(); i++) {
            InOutTransModel model = modelsOut.get(i);

            int type = model.getJenisInOut();

            float tmpF = nominals.get(type);
            tmpF += model.getJumlah();
            nominals.set(type, tmpF);
        }

        for (int i = 0; i < length ; i++) {
            dataList.add(new Entry(nominals.get(i), i));
        }

        xVals = dataListLabel;

        PieDataSet dataSet = new PieDataSet(dataList, "");
        dataSet.setColors(colors);

        return dataSet;
    }

    private RealmResults<InOutTransModel> separateDataInOut(boolean mode){
        RealmResults<InOutTransModel> results = realm
                .where(InOutTransModel.class)
                .equalTo("inOut", mode)
                .findAll();

        results.sort(new String[]{"jenisInOut","id"}, new Sort[]{Sort.ASCENDING, Sort.ASCENDING});

        return results;
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
                createPieChart(showMode);
                createRecViewList(separateDataInOut(showMode));
                return true;
            case R.id.action_show_outcome :
                getSupportActionBar().setTitle("Statistik Pengeluaran");
                supportInvalidateOptionsMenu();
                showMode = false;
                createPieChart(showMode);
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
