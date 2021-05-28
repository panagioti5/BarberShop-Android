package com.barber.app.adaptor.search;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barber.app.R;
import com.barber.app.ShopDetailsActivity;
import com.barber.app.constants.Constants;
import com.barber.app.entities.Shop;
import com.barber.app.entities.ShopWorkers;
import com.barber.app.utils.UtilsImpl;

import java.util.List;

public class WorkersPerShopAdapter extends RecyclerView.Adapter<WorkersPerShopAdapter.ViewHolder>
{
    private Activity context;
    private List<ShopWorkers> workers;
    private Shop shop;
    private String searchText;
    private String barberFullName;

    public WorkersPerShopAdapter(Activity context, Shop shop)
    {
        this.context = context;
        this.shop = shop;
        this.workers = shop.getShopDetails().getShopWorkers();
    }

    public WorkersPerShopAdapter(Activity context, Shop shop, String searchText)
    {
        this.context = context;
        this.shop = shop;
        this.workers = shop.getShopDetails().getShopWorkers();
        this.searchText = searchText;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.workers_full_names_search, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        if (UtilsImpl.isNotNullOrEmpty(searchText) && searchText.length() > 0)
        {
            SpannableStringBuilder sb = null;
            barberFullName = workers.get(position).getName() + " " + workers.get(position).getSurname();
            int index = barberFullName.indexOf(searchText);
            while (index > 0)
            {
                sb = new SpannableStringBuilder(barberFullName);
                ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(8, 80, 214)); //specify color here
                sb.setSpan(fcs, index, index + searchText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                index = barberFullName.indexOf(searchText, index + 1);

            }
            holder.barberShopFullName .setText(sb);

        }
        else
        {
            holder.barberShopFullName.setText(workers.get(position).getName() + " " + workers.get(position).getSurname());
        }

    }

    @Override
    public int getItemCount()
    {
        return workers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView barberShopFullName;
        public View layoutWorkers;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            barberShopFullName = itemView.findViewById(R.id.fullName);
            layoutWorkers = itemView.findViewById(R.id.layout_workers);
            layoutWorkers.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View view)
                {
                    Intent newsPage = new Intent(view.getContext(), ShopDetailsActivity.class);
                    newsPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_ACTIVITY_TITLE, shop.getShopDetails().getShopName());
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_SHOP_LON, shop.getShopDetails().getShopAddress().getShopLongitude());
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_SHOP_LAT, shop.getShopDetails().getShopAddress().getShopLatitude());
                    context.startActivity(newsPage);
                }
            });
        }

        @Override
        public void onClick(View view)
        {

        }
    }
}

