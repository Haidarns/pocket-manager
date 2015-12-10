package id.hnslabs.pocketmanager.Model;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by HaidarNS on 10/12/2015.
 */
public class Migration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm dynamicRealm, long oldVer, long newVer) {
        RealmSchema schema = dynamicRealm.getSchema();

        if(oldVer == 0){
            schema.create("InOutTransModel")
                    .addField("id", int.class)
                    .addField("inOut", boolean.class)
                    .addField("jenisInOut", int.class)
                    .addField("jumlah", float.class)
                    .addField("keterangan", String.class)
                    .addField("createdTime", String.class);
        }
    }
}
