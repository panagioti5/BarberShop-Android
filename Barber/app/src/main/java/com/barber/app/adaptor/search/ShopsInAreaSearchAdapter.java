package com.barber.app.adaptor.search;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barber.app.R;
import com.barber.app.ShopDetailsActivity;
import com.barber.app.constants.Constants;
import com.barber.app.entities.Shop;
import com.barber.app.entities.ShopWorkers;
import com.barber.app.utils.UtilsImpl;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShopsInAreaSearchAdapter extends RecyclerView.Adapter<ShopsInAreaSearchAdapter.ViewHolder> implements Filterable {
    private Activity context;
    private List<Shop> shopListFull;
    private List<Shop> shopListFiltered;
    private WorkersPerShopAdapter workersPerShopAdapter;
    private static final int COUNT = 2;
    private CountDownLatch latch = new CountDownLatch(COUNT);
    private String searchText;

    public ShopsInAreaSearchAdapter(Activity context, List<Shop> shopList) {
        this.context = context;
        this.shopListFiltered = shopList;
        setFullShopList(shopList);
    }

    private void setFullShopList(List<Shop> shopList) {
        this.shopListFull = new ArrayList<>(shopList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_search_details, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopsInAreaSearchAdapter.ViewHolder holder, int position) {
        String barberShopName = (shopListFiltered.get(position).getShopDetails().getShopName());
        setImageLogoToImageView(holder, position);
        holder.workersFullNames.setLayoutManager(new LinearLayoutManager(this.context));

        workersPerShopAdapter = new WorkersPerShopAdapter(context, shopListFiltered.get(position), searchText);
        holder.workersFullNames.setAdapter(workersPerShopAdapter);

        if (UtilsImpl.isNotNullOrEmpty(searchText) && searchText.length() > 0) {
            SpannableStringBuilder sb = null;
            //color your text here
            int index = barberShopName.indexOf(searchText);
            while (index > 0) {
                sb = new SpannableStringBuilder(barberShopName);
                ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(8, 80, 214)); //specify color here
                sb.setSpan(fcs, index, index + searchText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                index = barberShopName.indexOf(searchText, index + 1);

            }
            holder.barberShopName.setText(sb);

        } else {
            holder.barberShopName.setText(barberShopName);
        }
    }

    private void setImageLogoToImageView(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .asBitmap()
                .load(shopListFiltered.get(position).getLogoLink())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.shopLogo.setImageBitmap(UtilsImpl.getRoundedCroppedBitmap(resource));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    @Override
    public int getItemCount() {
        return shopListFiltered.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView barberShopName;
        public ImageView shopLogo;
        public CardView cardView;
        public RecyclerView workersFullNames;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            barberShopName = (TextView) itemView.findViewById(R.id.shopName);
            shopLogo = (ImageView) itemView.findViewById(R.id.shopLogoSearch);
            workersFullNames = (RecyclerView) itemView.findViewById(R.id.workersFullNames);
            cardView = (CardView) itemView.findViewById(R.id.card_shop_details);
            cardView.setOnClickListener(this);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newsPage = new Intent(view.getContext(), ShopDetailsActivity.class);
                    newsPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_ACTIVITY_TITLE, barberShopName.getText().toString());
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_SHOP_LON, shopListFiltered.get(getAdapterPosition()).getShopDetails().getShopAddress().getShopLongitude());
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_SHOP_LAT, shopListFiltered.get(getAdapterPosition()).getShopDetails().getShopAddress().getShopLatitude());
                    context.startActivity(newsPage);
                }
            });
        }

        @Override
        public void onClick(View v) {
        }
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            setStringConstraint(constraint);
            List<Shop> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(shopListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                filteredList = shopListFull.stream().filter(s -> s.getShopDetails().getShopName().toLowerCase().trim().contains(filterPattern)).collect(Collectors.toList());


                List<Shop> listBasedOnWorkerFullName = shopListFull.stream().filter(s -> s.getShopDetails().getShopWorkers()
                                                        .stream().anyMatch(w -> (w.getName() + " " + w.getSurname()).toLowerCase().trim().contains(filterPattern)))
                                                            .collect(Collectors.toList());

                filteredList.addAll(listBasedOnWorkerFullName);
                Set<Shop> a = new HashSet<>();
                a.addAll(filteredList);
                filteredList.clear();
                filteredList.addAll(a);


            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            shopListFiltered.clear();
            shopListFiltered.addAll((Collection<? extends Shop>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    private void setStringConstraint(CharSequence constraint) {
        searchText = constraint.toString();
    }
}
