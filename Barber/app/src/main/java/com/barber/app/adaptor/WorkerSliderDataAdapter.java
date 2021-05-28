package com.barber.app.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barber.app.R;
import com.barber.app.dao.SpecificShopEmployeeImageSelector;
import com.barber.app.entities.ShopWorkers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class WorkerSliderDataAdapter extends RecyclerView.Adapter<WorkerSliderDataAdapter.ViewHolder> {

    private Context context;
    private List<ShopWorkers> shopWorkers = new ArrayList<>();
    private SpecificShopEmployeeImageSelector mListener;
    int row_index = -1;

    //For setting strings on worker image click we defined OnWorkerImageSelected which is implemented here and we pass OnWorkerImageSelected in constructor
    public WorkerSliderDataAdapter(Context context, SpecificShopEmployeeImageSelector listener) {
        this.context = context;
        mListener = listener;
    }

    public void deleteAllItems() {
        this.shopWorkers.clear();
        notifyDataSetChanged();
    }


    public void addItem(ShopWorkers sliderItem) {
        this.shopWorkers.add(sliderItem);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.worker_slider_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Glide.with(context)
//                .asBitmap()
//                .load(shopWorkers.get(position).getWorkerProfilePictureLink())
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        holder.imageView.setImageBitmap(UtilsImpl.getRoundedCroppedBitmap(resource));
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                    }
//                });
//USING PICASO TO KAME SCREEN TOT FLICKERING WHEN CALLING notifyDataSetChanged();
        Picasso.get()
                .load(shopWorkers.get(position).getWorkerProfilePictureLink())
                .resize(400, 400)
                .centerCrop()
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                row_index = position;
                notifyDataSetChanged();
            }
        });
        if (row_index == position) {
            holder.imageView.setBorderColor(Color.parseColor("#5EBA7D"));
            holder.imageView.setBorderWidth(14);
            ShopWorkers worker = shopWorkers.get(position);
            mListener.onImageSelectedLine1(worker.getName() + " " + worker.getSurname());
            mListener.onImageSelectedLine2(worker.getWorkerDesc());
        } else {
            holder.imageView.setBorderColor(Color.parseColor("#FFFFFF"));
            holder.imageView.setBorderWidth(2);
        }
    }

    @Override
    public int getItemCount() {
        return shopWorkers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView
                    .findViewById(R.id.profile_image);
        }
    }

    private void showContentOnWorkerImageClick(View view, int position) {
    }
}