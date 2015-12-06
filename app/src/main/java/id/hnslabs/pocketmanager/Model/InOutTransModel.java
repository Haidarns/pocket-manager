package id.hnslabs.pocketmanager.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HaidarNS on 01/12/2015.
 */
public class InOutTransModel extends RealmObject{
    private int id;

    private boolean inOut;
    private float jumlah;
    private String keterangan, createdTime;
    private int jenisInOut;

    //format tanggal = dd/mm/yyyy

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getInOut() {
        return inOut;
    }

    public void setInOut(boolean inOut) {
        this.inOut = inOut;
    }

    public int getJenisInOut() {
        return jenisInOut;
    }

    public void setJenisInOut(int jenisInOut) {
        this.jenisInOut = jenisInOut;
    }

    public float getJumlah() {
        return jumlah;
    }

    public void setJumlah(float jumlah) {
        this.jumlah = jumlah;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}
