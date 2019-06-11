package com.simrin.galleryapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private List<String> PhotoUrls;
    private GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        recyclerView = findViewById(R.id.recyclerview);
        PhotoUrls = new ArrayList<>();
        getPhotos();
        setuprecyclerview(PhotoUrls);
    }

    private void getPhotos(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API api = retrofit.create(API.class);

        Call<PhotoList> call = api.getPhoto("flickr.photos.getRecent", "20", "1",
                "6f102c62f41998d151e5a1b48713cf13","json","1","url_s");

        call.enqueue(new Callback<PhotoList>() {
            @Override
            public void onResponse(Call<PhotoList> call, Response<PhotoList> response) {
                PhotoList result = response.body();
                for (Photo photo: result.getPhotos().getPhoto()) {
                    //Log.i("title", photo.getTitle());
                    PhotoUrls.add(photo.getUrlS());
                }

                setuprecyclerview(PhotoUrls);
                Log.i("PhotoUrls", String.valueOf(PhotoUrls.get(0)));
                Log.i("PhotoUrls", String.valueOf(PhotoUrls.get(1)));

            }

            @Override
            public void onFailure(Call<PhotoList> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setuprecyclerview(List<String> photoUrls) {
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(this, photoUrls);
        layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
    }


}
