package com.barber.app.adaptor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barber.app.AddRemoveShopToFavoritesHandler;
import com.barber.app.FavoritesActivity;
import com.barber.app.MainActivity;
import com.barber.app.MainShopsActivity;
import com.barber.app.R;
import com.barber.app.ShopDetailsActivity;
import com.barber.app.codes.OperationCode;
import com.barber.app.constants.Constants;
import com.barber.app.entities.Shop;
import com.barber.app.utils.UtilsImpl;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShopsInAreaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity context;
    private List<Shop> shops;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public ShopsInAreaAdapter(Activity context, List<Shop> shops) {
        this.context = context;
        this.shops = shops;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
            return new ViewHolder(itemView);

        } else if (viewType == TYPE_HEADER) {
            if (context instanceof MainActivity) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.headeritem_main_activity, parent, false);
                return new HeaderViewHolder(itemView);
            } else if (context instanceof FavoritesActivity) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.headeritem_favorites_activity, parent, false);
                return new HeaderViewHolder(itemView);
            } else if (context instanceof MainShopsActivity) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.headeritem_main_shops_activity, parent, false);
                return new HeaderViewHolder(itemView);
            } else return null;
        } else return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            if (context instanceof FavoritesActivity) {
                headerViewHolder.txt_needsreview.setText("" + shops.size());
                headerViewHolder.emojiID.setText(new String(Character.toChars(0x1F60A)));
            }
            if (context instanceof MainShopsActivity || context instanceof MainActivity) {
                if (shops == null || shops.isEmpty()) {
                    headerViewHolder.srlHeaderShopsMain.setVisibility(View.GONE);
                } else {
                    headerViewHolder.srlHeaderShopsMain.setVisibility(View.VISIBLE);
                    headerViewHolder.emojiID.setText(new String(Character.toChars(0x1F60A)));
                }
            }
        } else {
            position = position - 1;
            setStatusOnEachGridElement((ViewHolder) holder, position);
            setIsFavoriteOnEachRVElement((ViewHolder) holder, position);
            ((ViewHolder) holder).barberShopName.setText(shops.get(position).getShopDetails().getShopName().length() > 35 ? shops.get(position).getShopDetails().getShopName().substring(0, 34) + "" +
                    "  id:" + shops.get(position).getShopId() + " RankID: " + shops.get(position).getRankID() :
                    shops.get(position).getShopDetails().getShopName() + "id:" + shops.get(position).getShopId() + "RankID: " + shops.get(position).getRankID());
            Glide.with(context).load(shops.get(position).getImageLink()).into(((ViewHolder) holder).imageView);
            ((ViewHolder) holder).calculatedDistance.setText(UtilsImpl.calculateDistanceFromShop(context, shops.get(position).getShopDetails().getShopAddress().getShopLatitude(), shops.get(position).getShopDetails().getShopAddress().getShopLongitude()));
            setIsNewIconOnEachRVElement((ViewHolder) holder, position);
            setShopLogoOnEachRVElement((ViewHolder) holder, position);
        }
    }

    private void setIsNewIconOnEachRVElement(ViewHolder holder, int position) {
        if (context instanceof FavoritesActivity) {
            (((ViewHolder) holder).isNewShop).setVisibility(View.INVISIBLE);
        } else {
            ((ViewHolder) holder).isNewShop.setVisibility(View.INVISIBLE);
            if (shops.get(position).isRecentlyAdded()) {
                (((ViewHolder) holder).isNewShop).setVisibility(View.VISIBLE);
            }
        }
    }

    private void setShopLogoOnEachRVElement(ViewHolder holder, int position) {
        Glide.with(context)
                .asBitmap()
                .load(shops.get(position).getLogoLink())
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

    private void setIsFavoriteOnEachRVElement(ViewHolder holder, int position) {
        AddRemoveShopToFavoritesHandler checkExistence = new AddRemoveShopToFavoritesHandler(context);
        checkExistence.setShopID(shops.get(position));
        OperationCode exists = checkExistence.isExists();
        if (exists == OperationCode.OperationSuccess) {
            holder.addToFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.fav_added));
        }
        if (exists == OperationCode.OperationFailed) {
            holder.addToFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.favorites));
        }
    }

    private void setStatusOnEachGridElement(ViewHolder holder, int position) {
        if (context instanceof MainActivity || context instanceof FavoritesActivity) {
            holder.barberShopStatusMessage.setText(shops.get(position).getShopDetails().getCity());
            holder.barberShopStatusMessage.setTextColor(Color.parseColor(Constants.SHOP_CITY_COLOR));
            holder.barberShopStatusMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            holder.barberShopStatusMessage.setText(shops.get(position).getStatusMessage());
            if (shops.get(position).getStatusMessage().equals(Constants.SHOP_CLOSED)) {
                holder.barberShopStatusMessage.setTextColor(Color.parseColor(Constants.SHOP_CLOSED_COLOR));
                holder.barberShopStatusMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            if (shops.get(position).getStatusMessage().equals(Constants.SHOP_CLOSES_SOON)) {
                holder.barberShopStatusMessage.setTextColor(Color.parseColor(Constants.SHOP_CLOSES_SOON_COLOR));
                holder.barberShopStatusMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            if (shops.get(position).getStatusMessage().equals(Constants.SHOP_OPEN)) {
                holder.barberShopStatusMessage.setTextColor(Color.parseColor(Constants.SHOP_OPEN_COLOR));
                holder.barberShopStatusMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        }
    }

    @Override
    public int getItemCount() {
        return shops.size() + 1;
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView txt_needsreview, emojiID;
        RelativeLayout srlHeaderShopsMain;

        public HeaderViewHolder(View view) {
            super(view);
            if (context instanceof FavoritesActivity) {
                txt_needsreview = (TextView) view.findViewById(R.id.review_needs_count_text);
                emojiID = (TextView) view.findViewById(R.id.emojiID);
            } else {
                srlHeaderShopsMain = (RelativeLayout) view.findViewById(R.id.srlHeaderShopsMain);
                emojiID = (TextView) view.findViewById(R.id.emojiIDX);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView barberShopName;
        public TextView barberShopStatusMessage;
        public ImageView imageView;
        public TextView calculatedDistance;
        public ImageView shopLogo;
        public CircleImageView isNewShop;
        public CircleImageView addToFavorites;


        public ViewHolder(View itemView) {
            super(itemView);
            barberShopName = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            shopLogo = (ImageView) itemView.findViewById(R.id.shopLogo);
            barberShopStatusMessage = (TextView) itemView.findViewById(R.id.status);
            calculatedDistance = (TextView) itemView.findViewById(R.id.shopDistance);
            isNewShop = (CircleImageView) itemView.findViewById(R.id.isNewShop);
            addToFavorites = (CircleImageView) itemView.findViewById(R.id.addToFavorites);
            imageView.setOnClickListener(this);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newsPage = new Intent(view.getContext(), ShopDetailsActivity.class);
                    newsPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_ACTIVITY_TITLE, barberShopName.getText().toString());
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_SHOP_LON, shops.get(getAdapterPosition() - 1).getShopDetails().getShopAddress().getShopLongitude());
                    newsPage.putExtra(Constants.EXTRA_SESSION_ID_SHOP_LAT, shops.get(getAdapterPosition() - 1).getShopDetails().getShopAddress().getShopLatitude());
                    context.startActivity(newsPage);
                }
            });

            addToFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddRemoveShopToFavoritesHandler addRemoveShopToFavoritesHandler = new AddRemoveShopToFavoritesHandler(context);
                    addRemoveShopToFavoritesHandler.setShopID(shops.get(getAdapterPosition() - 1));
                    OperationCode action = addRemoveShopToFavoritesHandler.handle();
                    if (action == OperationCode.added) {
                        addToFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.fav_added));
                    }
                    if (action == OperationCode.removed) {
                        addToFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.favorites));
                    }
                }
            });


            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int p = getLayoutPosition();
                    showContentInPopup(view, p);
                    return true;
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            showPopupMenu(v, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private void showPopupMenu(View view, int poaition) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_news, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuClickListener(poaition));
        popup.show();
    }

    private void showContentInPopup(View view, int position) {
        AlertDialog dialog = new AlertDialog.Builder(this.context)
                .setTitle(shops.get(position).getShopDetails().getShopName())
                .setMessage(shops.get(position).getStatusMessage())
                .create();
        dialog.show();
    }

    class MenuClickListener implements PopupMenu.OnMenuItemClickListener {
        Integer pos;

        public MenuClickListener(int pos) {
            this.pos = pos;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

//                case R.id.action_watch:
//                    String url = barberShops.get(pos).get();
//                    if (!url.startsWith(Constants.TEXT_HTTP) && !url.startsWith(Constants.TEXT_HTTPS)) {
//                        url = Constants.TEXT_HTTPS + url;
//                    }
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    context.startActivity(browserIntent);
//                    return true;
//
//                case R.id.action_share:
//                    String message = barberShops.get(pos).getNewUrl();
//                    Intent shareText = new Intent(Intent.ACTION_SEND);
//                    shareText.setType("text/plain");
//                    shareText.putExtra(Intent.EXTRA_TEXT, message);
//                    context.startActivity(Intent.createChooser(shareText, "Sharing Options"));
//
//                    return true;

                default:
            }
            return false;
        }
    }
}