package id.hnslabs.pocketmanager.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

import id.hnslabs.pocketmanager.Model.Formatter;
import id.hnslabs.pocketmanager.Model.InOutTransModel;
import id.hnslabs.pocketmanager.R;
import io.realm.RealmResults;

/**
 * Created by HaidarNS on 07/12/2015.
 */
public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.RealmHolder> {
    private RealmResults<InOutTransModel> recyclerResults;
    private static RecViewClickListener recViewClickListener;
    private Context ctx;

    public RecViewAdapter(RealmResults<InOutTransModel> datas, Context context){
        recyclerResults = datas;
        ctx = context;
    }

    public static class RealmHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView icon;
        TextView nominal, keterangan, tanggal;
        public RealmHolder(View itemView){
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.cuslist_main_icon);
            nominal = (TextView) itemView.findViewById(R.id.cuslist_main_nom);
            keterangan = (TextView) itemView.findViewById(R.id.cuslist_main_ket);
            tanggal = (TextView) itemView.findViewById(R.id.cuslist_main_tanggal);
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
            holder.icon.setImageResource(Formatter.getIconResId(Formatter.TYPE_INCOME, stringId));
        } else {
            holder.icon.setBackgroundResource(R.drawable.circle_bg_red);
            holder.icon.setImageResource(Formatter.getIconResId(Formatter.TYPE_OUTCOME, stringId));
        }

        String nominalFormat = Formatter.currencyFormatter(obj.getJumlah());

        String[] formatTemp = obj.getCreatedTime().split("-");
        String[] dateTemp = formatTemp[0].split("/");
        String dateFormat = dateTemp[2]+"/"+dateTemp[1];

        holder.nominal.setText(nominalFormat);

        if(obj.getKeterangan().isEmpty()){
            String[] yoi;
            Resources res = ctx.getResources();
            if(obj.getInOut()) {
                yoi = res.getStringArray(R.array.income_str_array);
            } else {
                yoi = res.getStringArray(R.array.outcome_str_array);
            }
            holder.keterangan.setText(yoi[obj.getJenisInOut()]);
        } else {
            holder.keterangan.setText(obj.getKeterangan());
        }
        holder.tanggal.setText(dateFormat);
    }

    @Override
    public int getItemCount() {
        return recyclerResults.size();
    }
}
