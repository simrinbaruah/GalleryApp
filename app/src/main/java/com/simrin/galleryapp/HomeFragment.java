package com.simrin.galleryapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment{

    private RecyclerView recyclerView;
    private RecyclerViewAdapter myAdapter;
    private List<String> PhotoUrls;
    private GridLayoutManager layoutManager;
    private Boolean isScrolling = true;
    private int currentItems, totalItems, scrollOutItems, previousTotal=0;
    private int viewThreshold=20;
    private int pageNumber = 1;
    private ProgressBar progressBar;
    private Snackbar snackbar;
    private RelativeLayout relativeLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = getView().findViewById(R.id.recyclerview);
        progressBar = getView().findViewById(R.id.progress);
        relativeLayout = getView().findViewById(R.id.relativeLayout);
        PhotoUrls = new ArrayList<>();
//        if(isNetworkAvailable() == false){
//            showSnackBar("Intenet Not There", relativeLayout);
//        }
        getPhotos(String.valueOf(pageNumber));
    }

    private void getPhotos(String page){
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API api = retrofit.create(API.class);


        Call<PhotoList> call = api.getPhoto("flickr.photos.getRecent", "20", page,
                "6f102c62f41998d151e5a1b48713cf13","json","1","url_s");

        call.enqueue(new Callback<PhotoList>() {
            @Override
            public void onResponse(Call<PhotoList> call, Response<PhotoList> response) {
                PhotoList result = response.body();
                for (Photo photo: result.getPhotos().getPhoto()) {
                    PhotoUrls.add(photo.getUrlS());
                }
                setuprecyclerview(PhotoUrls);
                myAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PhotoList> call, Throwable t) {
            }
        });
    }

    private void setuprecyclerview(List<String> photoUrls) {
        if(myAdapter==null){
            myAdapter = new RecyclerViewAdapter(getActivity(), photoUrls);
            layoutManager = new GridLayoutManager(getActivity(), 3);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(myAdapter);
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = layoutManager.getChildCount();
                totalItems = layoutManager.getItemCount();
                scrollOutItems = layoutManager.findFirstVisibleItemPosition();

                if(dy>0){
                    if(isScrolling){
                        if(totalItems>previousTotal){
                            isScrolling=false;
                            previousTotal=totalItems;
                        }
                    }
                    if(!isScrolling&&(totalItems-currentItems)<=(scrollOutItems+viewThreshold)){
                        isScrolling = true;
                        Toast.makeText(getActivity(), "pageNumber " + String.valueOf(pageNumber), Toast.LENGTH_SHORT).show();
                        if(pageNumber<3) {
                            pageNumber++;
                            getPhotos(String.valueOf(pageNumber));
                        }
                    }
                }
            }
        });
    }
}

