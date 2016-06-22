package com.truedreamz.flexilistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class GridAdapter extends BaseAdapter{
    private ImageLoader imageLoader;
    private Context context;
    List<TouristPlaceModal> touristPlaceList;

    public GridAdapter(List<TouristPlaceModal> list,Context context){
        super();
        //Getting all the places
        this.touristPlaceList = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return touristPlaceList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        public NetworkImageView imageViewPlace;
        public TextView textViewPlaceName;
        public TextView textViewPlaceCity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder gridViewImageHolder;
//             check to see if we have a view
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            view = inflater.inflate(R.layout.tourist_place_grid_raw, parent, false);
            gridViewImageHolder = new ViewHolder();
            gridViewImageHolder.imageViewPlace = (NetworkImageView) view.findViewById(R.id.networkImgPlace);
            gridViewImageHolder.textViewPlaceName= (TextView) view.findViewById(R.id.txtPlaceName);
            gridViewImageHolder.textViewPlaceCity= (TextView) view.findViewById(R.id.txtCity);
            view.setTag(gridViewImageHolder);
        } else {
            gridViewImageHolder = (ViewHolder) view.getTag();
        }

        TouristPlaceModal place =  touristPlaceList.get(position);

        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        if(place.imageUrl!=null){
            imageLoader.get(place.imageUrl, ImageLoader.getImageListener(gridViewImageHolder.imageViewPlace,
                    R.drawable.default_pic, android.R.drawable.ic_dialog_alert));
            gridViewImageHolder.imageViewPlace.setImageUrl(place.imageUrl, imageLoader);
        }

        if(!place.name.equals("") && place.name!=null) gridViewImageHolder.textViewPlaceName.setText(place.name);
        if(!place.city.equals("") && place.city!=null) gridViewImageHolder.textViewPlaceCity.setText(place.city);

        return view;
    }
}
