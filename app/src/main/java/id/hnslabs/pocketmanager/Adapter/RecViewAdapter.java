package id.hnslabs.pocketmanager.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import id.hnslabs.pocketmanager.Model.IconManager;
import id.hnslabs.pocketmanager.Model.InOutTransModel;
import id.hnslabs.pocketmanager.R;
import io.realm.RealmResults;

/**
 * Created by HaidarNS on 07/12/2015.
 */
public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.RealmHolder> {
    private RealmResults<InOutTransModel> recyclerResults;
    private static RecViewClickListener recViewClickListener;

    public RecViewAdapter(RealmResults<InOutTransModel> datas){
        recyclerResults = datas;
    }

    public static class RealmHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView icon;
        TextView nominal, keterangan;
        public RealmHolder(View itemView){
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.cuslist_main_icon);
            nominal = (TextView) itemView.findViewById(R.id.cuslist_main_nom);
            keterangan = (TextView) itemView.findViewById(R.id.cuslist_main_ket);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recViewClickListener.onItemClick(getPosition(),v);
        }
    }

    public void setOnItemClickListener(RecViewClickListener recViewClickListener) {
        this.recViewClickListener = recViewClickListener;
    }

    public interface RecViewClickListener {
        public void onItemClick(int position, View v);
    }

    @Override
    public RealmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cus_main_list, parent, false);
        RealmHolder rh = new RealmHolder(v);
        return rh;
    }

    @Override
    public void onBindViewHolder(RealmHolder holder, int position) {
        InOutTransModel obj = recyclerResults.get(position);

        int stringId = obj.getJenisInOut();

        if(obj.getInOut()) {
            holder.icon.setBackgroundResource(R.drawable.circle_bg_green);
            holder.icon.setImageResource(IconManager.getIconResId(IconManager.TYPE_INCOME, stringId));
        } else {
            holder.icon.setBackgroundResource(R.drawable.circle_bg_red);
            holder.icon.setImageResource(IconManager.getIconResId(IconManager.TYPE_OUTCOME, stringId));
        }

        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        DecimalFormat df = (DecimalFormat)nf;
        String nominalFormat = "Rp "+ df.format(obj.getJumlah());

        holder.nominal.setText(nominalFormat);
        holder.keterangan.setText(obj.getKeterangan());
    }

    @Override
    public int getItemCount() {
        return recyclerResults.size();
    }
}
