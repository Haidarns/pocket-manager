package id.hnslabs.pocketmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;

import id.hnslabs.pocketmanager.Model.InOutTransModel;
import io.realm.Realm;
import io.realm.RealmResults;

public class EditActivity extends AppCompatActivity {
    public static final boolean PEMASUKAN = true;
    public static final boolean PENGELUARAN = false;
    public static final boolean TAMBAH = true;
    public static final boolean LIHAT = false;
    private boolean actMode, addMode;
    private EditText editNominal, editKet;
    private Spinner typeSpinIn, typeSpinOut;
    private SharedPreferences sharedpreferences;
    private InOutTransModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addMode = getIntent().getBooleanExtra("addMode",PEMASUKAN);
        actMode = getIntent().getBooleanExtra("actMode",LIHAT);
        sharedpreferences = getSharedPreferences("PocManPref", Context.MODE_PRIVATE);

        editNominal = (EditText) findViewById(R.id.input_nom);
        editKet     = (EditText) findViewById(R.id.input_ket);
        typeSpinIn    = (Spinner) findViewById(R.id.spinnerIn);
        typeSpinOut    = (Spinner) findViewById(R.id.spinnerOut);

        enableEdit(true);

        if (!actMode) {
            int dataId = getIntent().getIntExtra("dataId", 0);
            model = getData(dataId);

            addMode = model.getInOut();
            editNominal.setText(String.valueOf(model.getJumlah()));
            editKet.setText(model.getKeterangan());

            enableEdit(false);

            if(addMode){
                typeSpinIn.setSelection(model.getJenisInOut());
            } else {
                typeSpinOut.setSelection(model.getJenisInOut());
            }
        }

        if (addMode) {
            getSupportActionBar().setTitle(R.string.income);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.income)));
            typeSpinOut.setVisibility(View.GONE);
        } else {
            getSupportActionBar().setTitle(R.string.outcome);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.outcome)));
            typeSpinIn.setVisibility(View.GONE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void enableEdit(boolean enable){
        if (!enable) {
            editNominal.setEnabled(false);
            editKet.setEnabled(false);
            typeSpinOut.setEnabled(false);
            typeSpinIn.setEnabled(false);
        } else {
            editNominal.setEnabled(true);
            editKet.setEnabled(true);
            typeSpinOut.setEnabled(true);
            typeSpinIn.setEnabled(true);
        }
    }

    private InOutTransModel getData(int id){
        Realm realm = Realm.getInstance(getApplicationContext());
        return realm.where(InOutTransModel.class).equalTo("id",id).findFirst();
    }

    private int getSpinnerItemId(){
        int temp = 0;
        if(addMode){
            temp = (int) typeSpinIn.getSelectedItemId();
        } else {
            temp = (int) typeSpinOut.getSelectedItemId();
        }
        return temp;
    }

    private int getCountForId(){
        int prevId = sharedpreferences.getInt("count", 0);

        SharedPreferences.Editor editor= sharedpreferences.edit();
        editor.putInt("count", prevId + 1);
        editor.apply();

        return prevId+1;
    }

    private void deleteData(int id){
        Realm realm = Realm.getInstance(getApplicationContext());
        realm.beginTransaction();
        InOutTransModel modelS = realm.where(InOutTransModel.class).equalTo("id",id).findFirst();
        modelS.removeFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    private void saveData(){
        Calendar cal = Calendar.getInstance();
        int sec   = cal.get(Calendar.SECOND);
        int menit = cal.get(Calendar.MINUTE);
        int jam   = cal.get(Calendar.HOUR_OF_DAY);
        int hari  = cal.get(Calendar.DAY_OF_MONTH);
        int bulan = cal.get(Calendar.MONTH)+1;
        int tahun = cal.get(Calendar.YEAR);

        String nominal = editNominal.getText().toString();
        String keterangan = editKet.getText().toString();
        int jenis         = getSpinnerItemId();

        if (nominal.isEmpty()){
            Toast.makeText(EditActivity.this, "Tolong isi semua kolom", Toast.LENGTH_SHORT).show();
        } else {
            //InOutTransModel model = new InOutTransModel();
            InOutTransModel modelS;
            int idPrev = getCountForId();
            String tglPrev = tahun + "/" + bulan + "/" + hari + "-" + jam + ":" + menit + ":"+ sec;
            Float nominalF    = Float.parseFloat(nominal);

            if(model!=null){
                idPrev = model.getId();
                tglPrev = model.getCreatedTime();
                deleteData(idPrev);
            }

            Realm realm = Realm.getInstance(this);
            realm.beginTransaction();
            modelS = realm.createObject(InOutTransModel.class);
            modelS.setId(idPrev);
            modelS.setInOut(addMode);
            modelS.setJumlah(nominalF);
            modelS.setKeterangan(keterangan);
            modelS.setJenisInOut(jenis);
            modelS.setCreatedTime(tglPrev);
            realm.commitTransaction();
            realm.close();
            //db.inputData(model);

            Toast.makeText(EditActivity.this, "Sukses menambahkan data."+modelS.getInOut()+modelS.getJenisInOut(), Toast.LENGTH_SHORT).show();

            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(actMode) {
            getMenuInflater().inflate(R.menu.tambah_menu, menu);
        }else {
            getMenuInflater().inflate(R.menu.tambah_menu_lihat, menu);
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
            case R.id.action_save :
                saveData();
                return true;
            case R.id.action_edit :
                enableEdit(true);
                actMode = TAMBAH;
                supportInvalidateOptionsMenu();
                return true;
            case R.id.action_delete :
                deleteData(model.getId());
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
