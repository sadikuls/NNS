package com.sadikul.nns.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sadikul.nns.Activities.FileDetails;
import com.sadikul.nns.Activities.MainActivity;
import com.sadikul.nns.Model.NoticeItem;
import com.sadikul.nns.R;
import com.sadikul.nns.Utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NoticeboardAdminAdapter extends RecyclerView.Adapter<NoticeboardAdminAdapter.ShowsViewHolder> {

    public Context context;

    private List<NoticeItem> notices=new ArrayList<>();
    private String changeMessage = "";
    DeleteHandler deleteHandler;
    int pos;

    public NoticeboardAdminAdapter(Context context, List<NoticeItem> notices) {
        this.context = context;
        this.notices = notices;
        deleteHandler= (MainActivity) context;


    }

    @Override
    public ShowsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.layout_noticeboard_admin_item, parent, false);
        return new ShowsViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final ShowsViewHolder holder, final int position) {

        NoticeItem noticeItem =notices.get(position);
        if(noticeItem.getTitle().length()>=55)
            holder.textviewTitle.setText(noticeItem.getTitle().substring(0,55));
        else
            holder.textviewTitle.setText(noticeItem.getTitle());




        holder.textViewTime.setText(noticeItem.getTime());
        String link= Constant.baseURL+ noticeItem.getImageLink();
        Log.e("link",link);

        Glide.with(context).load(link).placeholder(R.drawable.ic_file_gray_116dp).fitCenter().override(100,100).dontAnimate().into(holder.patientProfileIcon);

        final int id=position;
/*
        holder.popup_menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.popup_menuImage);
                //inflating menu from xml resource
                popup.inflate(R.menu.popup_menu);

                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                //handle menu1 click
                                deleteHandler.delete();
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
*/
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
        @BindView(R.id.pop_up_menu)
        ImageView popup_menuImage;
        @BindView(R.id.body_container)
        RelativeLayout rl_bodyContainer;

        public ShowsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //itemView.setOnClickListener(this);
            popup_menuImage.setOnClickListener(this);
            rl_bodyContainer.setOnClickListener(this);
            patientProfileIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(context, "clicked item", Toast.LENGTH_SHORT).show();
            switch (view.getId()){
                case R.id.pop_up_menu:{

                        //creating a popup menu
                        PopupMenu popup = new PopupMenu(context, popup_menuImage);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.popup_menu);

                        //adding click listener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.delete:
                                        //handle menu1 click
                                        deleteHandler.delete(notices.get(getAdapterPosition()).getId());
                                        break;
                                }
                                return false;
                            }
                        });
                        //displaying the popup
                        popup.show();

                    break;
                }/*
                case R.id.body_container:{
                    Intent intent=new Intent(context, FileDetails.class);
                    intent.putExtra("id",files.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                    break;
                }
                case R.id.patient_profile_icon:{
                    Intent intent=new Intent(context, FileDetails.class);
                    intent.putExtra("id",files.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                    break;
                }*/
                default:{
                    Intent intent=new Intent(context, FileDetails.class);
                    intent.putExtra("id",notices.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                    break;
                }
            }
        }
    }


   public interface DeleteHandler{
        public void delete(String id);
    }
}

