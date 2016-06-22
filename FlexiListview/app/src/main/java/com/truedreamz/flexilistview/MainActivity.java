package com.truedreamz.flexilistview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final String TAG="TouristActivity";
    private static final String STATUS_SUCCESS="1";
    //Creating a List of places
    private ArrayList<TouristPlaceModal> listPlaces=new ArrayList<TouristPlaceModal>();

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    private GridView gridviewPlaces;
    private GridAdapter adapterGrid;

    private ImageButton imgBtnGrid,imgBtnList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Initializing Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        gridviewPlaces=(GridView)findViewById(R.id.gridViewPlaces);

        imgBtnGrid=(ImageButton)findViewById(R.id.imgBtnGrid);
        imgBtnList=(ImageButton)findViewById(R.id.imgBtnList);

        try {
            getTouristPlaceList();
        }catch (JSONException ex){
            Log.e(TAG,"JSONException:"+ex.getMessage());
        }
    }

    private String getTouristPlaceList() throws JSONException
    {
        try {
            // Reading static text from raw folder
            InputStream in_s = getResources().openRawResource(R.raw.poi);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            String str = new String(b);

            JSONObject jsonobj = new JSONObject(str);
            JSONObject jsonResponse = jsonobj.getJSONObject("response");
            String status = jsonobj.getString("status");
            if(status.equals(STATUS_SUCCESS)){
                parsingEventList(jsonResponse);
            }
            return status;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "IOException:" + e.getMessage());
            return null;
        }
    }

    public void parsingEventList(JSONObject eventData){

        try {
            JSONArray jsonArray = eventData.getJSONArray("poilist");
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject json = (JSONObject) jsonArray.get(i);
                TouristPlaceModal place=new TouristPlaceModal();
                place.name=(String) json.get("name");
                place.imageUrl=(String) json.get("image");
                place.rank=Integer.valueOf((String) json.get("rank"));
                place.bestTime=(String) json.get("besttime");
                place.city=(String) json.get("city");
                place.latitude=(String) json.get("geolat");
                place.longitude=(String) json.get("geolong");
                place.hoursSpent=(String) json.get("hoursspent");
                listPlaces.add(place);
            }
            Log.d(TAG, "POI list size:"+listPlaces.size());

            //Finally initializing our adapter
            adapter = new CardviewAdapter(listPlaces,recyclerView, this);
            //Adding adapter to recyclerview
            recyclerView.setAdapter(adapter);

            adapterGrid=new GridAdapter(listPlaces,this);
            gridviewPlaces.setAdapter(adapterGrid);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void onGridviewListener(View v){
        gridviewPlaces.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    public void onListviewListener(View v){
        gridviewPlaces.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}