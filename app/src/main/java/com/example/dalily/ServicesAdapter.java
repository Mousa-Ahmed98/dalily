package com.example.dalily;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.DataViewHolder> {
    private OnItemClickListener mListener;
    private List<Service> serviceList;
    private Context context;

    public ServicesAdapter(Context context, List<Service> serviceList){
        this.context = context;
        this.serviceList = serviceList;
    }


    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service_view,parent,false);

        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        Service service = serviceList.get(position);
        Glide.with(context).load(service.getPhoto_uri()).centerCrop().into(holder.imageView);
        holder.name.setText(service.getTitle());
        holder.distance.setText(String.valueOf(service.getDistance_from_center()) + context.getString(R.string.m));
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public void swapDataSet(List<Service> list){
        this.serviceList = list;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }

    public class DataViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imageView;
        TextView name, distance;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            name = itemView.findViewById(R.id.name_view);
            distance = itemView.findViewById(R.id.distance_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
