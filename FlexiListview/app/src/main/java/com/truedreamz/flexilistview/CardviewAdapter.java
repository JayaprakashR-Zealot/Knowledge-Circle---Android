package com.truedreamz.flexilistview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by Jayaprakash on 6/6/2016.
 */
public class CardviewAdapter extends RecyclerView.Adapter<CardviewAdapter.ViewHolder> {

    private ImageLoader imageLoader;
    private Context context;

    List<TouristPlaceModal> touristPlaceList;
    RecyclerView recyclerViewPlaces;


    public CardviewAdapter(List<TouristPlaceModal> list,RecyclerView recyclerView, Context context){
        super();
        //Getting all the places
        this.touristPlaceList = list;
        this.recyclerViewPlaces=recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tourist_place_list, parent, false);
        v.setOnClickListener(new PlacesOnClickListener());
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TouristPlaceModal place =  touristPlaceList.get(position);

        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(place.imageUrl, ImageLoader.getImageListener(holder.imageViewPlace,
                R.drawable.default_pic, android.R.drawable.ic_dialog_alert));

        holder.imageViewPlace.setImageUrl(place.imageUrl, imageLoader);
        holder.textViewPlaceName.setText(place.name);
        holder.textViewPlaceRank.setText(String.valueOf(place.rank));
        holder.textViewPlaceCity.setText(place.city);
        holder.textViewPlaceBestTime.setText(place.bestTime);
        holder.textViewPlaceHoursSpent.setText(place.hoursSpent+" hours");

    }

    @Override
    public int getItemCount() {
        return touristPlaceList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public NetworkImageView imageViewPlace;
        public TextView textViewPlaceName;
        public TextView textViewPlaceRank;
        public TextView textViewPlaceCity;
        public TextView textViewPlaceBestTime;
        public TextView textViewPlaceHoursSpent;


        public ViewHolder(View itemView) {
            super(itemView);
            imageViewPlace = (NetworkImageView) itemView.findViewById(R.id.imageViewPlace);
            textViewPlaceName = (TextView) itemView.findViewById(R.id.textViewPlaceName);
            textViewPlaceRank= (TextView) itemView.findViewById(R.id.textViewPlaceRank);
            textViewPlaceCity= (TextView) itemView.findViewById(R.id.textViewPlaceCity);
            textViewPlaceBestTime= (TextView) itemView.findViewById(R.id.textViewPlaceBestTime);
            textViewPlaceHoursSpent= (TextView) itemView.findViewById(R.id.textViewPlaceHoursSpent);
        }
    }

    class PlacesOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int itemPosition = recyclerViewPlaces.getChildPosition(v);
            Log.d("Position:", String.valueOf(itemPosition));
            String placeName=touristPlaceList.get(itemPosition).name;
            String city=touristPlaceList.get(itemPosition).city;
            String latitude=touristPlaceList.get(itemPosition).latitude;
            String longitude=touristPlaceList.get(itemPosition).longitude;

            Toast.makeText(context,"Redirecting to "+placeName+", "+city+".",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q="+latitude+","+longitude+"(" + placeName + ")"));
            context.startActivity(intent);

        }
    }
}