package id.hnslabs.pocketmanager;

import android.content.Context;
import android.util.Log;

import id.hnslabs.pocketmanager.Model.InOutTransModel;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HaidarNS on 06/12/2015.
 */
public class DBHelper {
    private String LOG_TITLE = "DBHelper";
    private Context context;
    private Realm realm;
    private InOutTransModel modelTemp;

    public DBHelper (Context context){
        this.context = context;
    }

    private void openDB(boolean begin){
        realm = Realm.getInstance(context);
        if (begin) {
            realm.beginTransaction();
        }
    }

    private void closeDB(boolean commit){
        if (commit) {
            realm.commitTransaction();
        }
        realm.close();
    }

    public void inputData(InOutTransModel data){
        openDB(true);
        modelTemp = realm.createObject(InOutTransModel.class);
        modelTemp = data;
        closeDB(true);
        Log.i(LOG_TITLE, "Success adding data"+data.getKeterangan());
    }

    public void hapusData(String id){
        openDB(true);
        modelTemp = realm.where(InOutTransModel.class).equalTo("id",id).findFirst();
        modelTemp.removeFromRealm();
        closeDB(true);
        Log.i(LOG_TITLE, "Success delete a data");
    }

    public RealmResults<InOutTransModel> getAllData(){
        RealmResults<InOutTransModel> resTemp;
        openDB(false);

        resTemp = realm.where(InOutTransModel.class).findAll();

        //Log.i("lol",resTemp.first().getKeterangan());

        return resTemp;
    }
}
