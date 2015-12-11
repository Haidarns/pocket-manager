package id.hnslabs.pocketmanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import id.hnslabs.pocketmanager.Adapter.RecViewAdapter;
import id.hnslabs.pocketmanager.Model.Formatter;
import id.hnslabs.pocketmanager.Model.InOutTransModel;
import id.hnslabs.pocketmanager.Model.RealmConfig;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

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
        Realm realm = Realm.getInstance(new RealmConfig(this,"datas").getConfig());
        RealmResults<InOutTransModel> tmpData = realm.where(InOutTransModel.class).findAll();
        tmpData.sort("id", Sort.ASCENDING);
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

    private void readRealmFile(){
        String uri = dir.toString()+"/db.pcm";

        File f = new File(uri);

        InputStream fIn = null;

        try {
            fIn = new FileInputStream(f);
            Toast.makeText(MainActivity.this, "Sukses membuka file", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(MainActivity.this, "Gagal membuka file", Toast.LENGTH_SHORT).show();
        }

        Log.i("File", f.toString());
        copyBundledRealmFile(fIn, "datas");

        Realm realmImp = Realm.getInstance(new RealmConfig(this,"datas").getConfig());
        //Realm realm = Realm.getInstance(MainActivity.this);

        RealmResults<InOutTransModel> a = realmImp.where(InOutTransModel.class).findAll();
        a.sort("id", Sort.ASCENDING);

        for (int i = 0; i < a.size(); i++) {
            Log.i("Realm Import",String.valueOf(a.get(i).getId()));
        }

        //realm.copyToRealmOrUpdate();
        showStatus(realmImp);
        realmImp.close();
    }

    private String copyBundledRealmFile(InputStream inputStream, String outFileName) {
        try {
            File file = new File(this.getFilesDir(), outFileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[0];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String realmString(Realm realm) {
        StringBuilder stringBuilder = new StringBuilder();
        for (InOutTransModel iotm : realm.allObjects(InOutTransModel.class)) {
            stringBuilder.append(iotm.toString()).append("\n");
        }

        return (stringBuilder.length() == 0) ? "<data was deleted>" : stringBuilder.toString();
    }

    private void showStatus(Realm realm) {
        Toast.makeText(MainActivity.this, realmString(realm) , Toast.LENGTH_SHORT).show();
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
        } /* disable the feature, because can cause freeze to the app. Still haven't any idea to fix it
        else if (id == R.id.nav_backup){
            Realm realm = Realm.getInstance(new RealmConfig(this,"datas").getConfig());

            dir.mkdirs();
            File exportRealmFile = new File(dir, "db.pcm");

            try {
                // if "db.pcm" already exists, delete
                exportRealmFile.delete();

                // copy current realm to "db.pcm"
                //realm.writeEncryptedCopyTo(exportRealmFile,key);
                realm.writeCopyTo(exportRealmFile);

                Toast.makeText(MainActivity.this, key.toString(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.i("Save backup", "success");
            }
            realm.close();
        } else if (id == R.id.nav_restore){
            readRealmFile();
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
