package id.hnslabs.pocketmanager.Model;

import android.content.Context;

import io.realm.RealmConfiguration;

/**
 * Created by HaidarNS on 10/12/2015.
 */
public class RealmConfig {
    private Context context;
    private byte[] key;
    private RealmConfiguration config;

    public RealmConfig(Context context, byte[] key, String fileName){
        RealmConfiguration config = new RealmConfiguration
                .Builder(context)
                .name(fileName)
                .encryptionKey(key)
                .build();

        this.config = config;
        this.key = key;
        this.context = context;
    }

    public RealmConfig(Context context, String fileName){
        RealmConfiguration config = new RealmConfiguration
                .Builder(context)
                .name(fileName)
                .build();

        this.config = config;
        this.context = context;
    }

    public RealmConfiguration getConfig(){
        return config;
    }
}
