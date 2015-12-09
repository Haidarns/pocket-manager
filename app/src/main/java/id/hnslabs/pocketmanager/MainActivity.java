package id.hnslabs.pocketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.IOException;

import id.hnslabs.pocketmanager.Adapter.RecViewAdapter;
import id.hnslabs.pocketmanager.Model.Formatter;
import id.hnslabs.pocketmanager.Model.InOutTransModel;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RelativeLayout rl;
    private TextView balanceTV;

    private RealmResults<InOutTransModel> results;

    private final byte[] key = new byte[64];

    private final File sdCard = Environment.getExternalStorageDirectory();
    private final File dir = new File (sdCard.getAbsolutePath() + "/RealmDB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        */

        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        rl = (RelativeLayout) findViewById(R.id.sticky_main);
        balanceTV = (TextView) findViewById(R.id.balanceTv);

        final FloatingActionsMenu fabMen = (FloatingActionsMenu) findViewById(R.id.right_labels);
        final FloatingActionButton fabIn = (FloatingActionButton) findViewById(R.id.fabIncome);
        final FloatingActionButton fabOut = (FloatingActionButton) findViewById(R.id.fabOutcome);

        fabIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Masuk menu add income", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(getApplicationContext(), EditActivity.class);
                it.putExtra("addMode", EditActivity.PEMASUKAN);
                it.putExtra("actMode", EditActivity.TAMBAH);
                startActivity(it);
                fabMen.collapseImmediately();
            }
        });

        fabOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Masuk menu add outcome", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(getApplicationContext(), EditActivity.class);
                it.putExtra("addMode", EditActivity.PENGELUARAN);
                it.putExtra("actMode", EditActivity.TAMBAH);
                startActivity(it);
                fabMen.collapseImmediately();
            }
        });
        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
*/
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private RealmResults<InOutTransModel> getAllData(){
        Realm realm = Realm.getInstance(getApplicationContext());
        RealmResults<InOutTransModel> tmpData = realm.where(InOutTransModel.class).findAll();
        tmpData.sort("id", true);
        return tmpData;
    }

    private float getBalance(RealmResults<InOutTransModel> datas){
        float temp = 0f;
        InOutTransModel tempM;
        for (int i = 0; i < datas.size(); i++) {
            tempM = datas.get(i);
            if(tempM.getInOut()) {
                temp += tempM.getJumlah();
            } else {
                temp -= tempM.getJumlah();
            }
        }

        return temp;
    }

    @Override
    protected void onResume() {
        super.onResume();
        results = getAllData();
        adapter = new RecViewAdapter(results, MainActivity.this);

        float nominal = getBalance(results);

        if(nominal > 0){
            rl.setBackgroundColor(getResources().getColor(R.color.income));
        } else if ( nominal < 0) {
            rl.setBackgroundColor(getResources().getColor(R.color.outcome));
        } else {
            rl.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }

        String balTmp = Formatter.currencyFormatter(nominal);

        balanceTV.setText(balTmp);

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            // Handle the camera action
        } else if (id == R.id.nav_stats) {
            Intent it = new Intent(MainActivity.this, StatsLineActivity.class);
            startActivity(it);
        } else if (id == R.id.nav_stats_pie) {
            Intent it = new Intent(MainActivity.this, StatsPieActivity.class);
            startActivity(it);
        } else if (id == R.id.nav_backup){
            Realm realm = Realm.getInstance(MainActivity.this);

            dir.mkdirs();
            File exportRealmFile = new File(dir, "db.pcm");

            try {
                // if "db.pcm" already exists, delete
                exportRealmFile.delete();

                // copy current realm to "db.pcm"
                realm.writeEncryptedCopyTo(exportRealmFile, key);

                Toast.makeText(MainActivity.this, key.toString(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.i("Save backup", "success");
            }
            realm.close();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
