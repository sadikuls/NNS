package com.sadikul.nns.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sadikul.nns.Activities.FileDetails;
import com.sadikul.nns.Model.NoticeItem;
import com.sadikul.nns.R;
import com.sadikul.nns.Utils.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ASUS on 12-Sep-17.
 */
public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ShowsViewHolder> {

    public Context context;

    private List<NoticeItem> notices;
    private String changeMessage = "";

    public DashboardAdapter(Context context, List<NoticeItem> notices) {
        this.context = context;
        this.notices = notices;


    }

    @Override
    public ShowsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.layout_noticeboard_item, parent, false);
        return new ShowsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ShowsViewHolder holder, int position) {

        NoticeItem NoticeItem =notices.get(position);
        if(NoticeItem.getTitle().length()>=55)
            holder.textviewTitle.setText(NoticeItem.getTitle().substring(0,55));
        else
            holder.textviewTitle.setText(NoticeItem.getTitle());




        holder.textViewTime.setText(NoticeItem.getTime());
        String link= Constant.baseURL+ NoticeItem.getImageLink();
        Log.e("link",link);

        Glide.with(context).load(link).placeholder(R.drawable.ic_file_gray_116dp).fitCenter().override(100,100).dontAnimate().into(holder.patientProfileIcon);

    }

    @Override
    public int getItemCount() {

        //Log.e("size",files.size()+" ");
        return notices.size();
    }

    public class ShowsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.patient_profile_icon)
        CircleImageView patientProfileIcon;
        @BindView(R.id.textview_title)
        TextView textviewTitle;
        @BindView(R.id.textViewTime)
        TextView textViewTime;

        public ShowsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(context, "clicked item", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(context, FileDetails.class);
            intent.putExtra("id",notices.get(getAdapterPosition()).getId());
            context.startActivity(intent);
        }
    }

}

