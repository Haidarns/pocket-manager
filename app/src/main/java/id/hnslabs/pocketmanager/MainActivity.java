package id.hnslabs.pocketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import id.hnslabs.pocketmanager.Adapter.RecViewAdapter;
import id.hnslabs.pocketmanager.Model.InOutTransModel;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //DBHelper db;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private RealmResults<InOutTransModel> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        */

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
        tmpData.sort("createdTime",true);
        return tmpData;
    }

    @Override
    protected void onResume() {
        super.onResume();
        results = getAllData();

        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new RecViewAdapter(results);

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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
