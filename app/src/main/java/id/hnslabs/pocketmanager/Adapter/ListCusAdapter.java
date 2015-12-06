package id.hnslabs.pocketmanager.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import id.hnslabs.pocketmanager.Model.IconManager;
import id.hnslabs.pocketmanager.Model.InOutTransModel;
import id.hnslabs.pocketmanager.R;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by HaidarNS on 05/12/2015.
 */
public class ListCusAdapter extends RealmBaseAdapter<InOutTransModel> implements ListAdapter{
    RealmResults<InOutTransModel> datas;
    private Context context;

    public ListCusAdapter(Context ctx, RealmResults<InOutTransModel> results, boolean automaticUpdate){
        super(ctx, results, automaticUpdate);
        datas = results;
        context = ctx;
    }

    @Override
    public InOutTransModel getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;

        if(view == null){
            LayoutInflater inf = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            holder = new ViewHolder();

            view = inf.inflate(R.layout.cus_main_list, parent, false);

            holder.icon       = (ImageView) view.findViewById(R.id.cuslist_main_icon);
            holder.nominal    = (TextView) view.findViewById(R.id.cuslist_main_nom);
            holder.keterangan = (TextView) view.findViewById(R.id.cuslist_main_ket);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        InOutTransModel obj = datas.get(position);

        int stringId = obj.getJenisInOut();

        if(obj.getInOut()) {
            holder.icon.setBackgroundResource(R.drawable.circle_bg_green);
            holder.icon.setImageResource(IconManager.getIconResId(IconManager.TYPE_INCOME, stringId));
        } else {
            holder.icon.setBackgroundResource(R.drawable.circle_bg_red);
            holder.icon.setImageResource(IconManager.getIconResId(IconManager.TYPE_OUTCOME, stringId));
        }

        holder.nominal.setText(String.valueOf(obj.getJumlah()));
        holder.keterangan.setText(obj.getKeterangan());

        Log.i("hoam",obj.getKeterangan());

        return view;
    }

    private static class ViewHolder{
        ImageView icon;
        TextView nominal, keterangan;
    }
}