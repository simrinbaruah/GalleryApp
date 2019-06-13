package com.simrin.galleryapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter myAdapter;
    private GridLayoutManager layoutManager;
    private int currentItems, totalItems, scrollOutItems, previousTotal=0;
    private int viewThreshold=20;
    private int pageNumber = 1;
    private Boolean isScrolling = true;
    private ProgressBar progressBar;
    private List<String> PhotoUrls;
    private List<String> mFilteredList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_search,  container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = getView().findViewById(R.id.recyclerSearchView);
        PhotoUrls = new ArrayList<>();
//        getPhotos();
    }

    private void getPhotos(String userInput){
       // progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SearchAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SearchAPI api = retrofit.create(SearchAPI.class);


        Call<PhotoList> call = api.getPhoto("flickr.photos.search", "6f102c62f41998d151e5a1b48713cf13",
                "json","1","url_s", userInput);

        call.enqueue(new Callback<PhotoList>() {
            @Override
            public void onResponse(Call<PhotoList> call, Response<PhotoList> response) {
                PhotoList result = response.body();

                if(result.getPhotos() != null) {
                    for (Photo photo : result.getPhotos().getPhoto()) {
                        PhotoUrls.add(photo.getUrlS());
                    }
                    setuprecyclerview(PhotoUrls);
                    //myAdapter.notifyDataSetChanged();
                    // progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PhotoList> call, Throwable t) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {
                getPhotos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //getPhotos(newText);
               // getFilter().filter(newText);
                return false;
            }

        });
    }

    private void setuprecyclerview(List<String> photoUrls) {
        myAdapter = new RecyclerViewAdapter(getActivity(), photoUrls);
        layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                currentItems = layoutManager.getChildCount();
//                totalItems = layoutManager.getItemCount();
//                scrollOutItems = layoutManager.findFirstVisibleItemPosition();
//
//                if(dy>0){
//                    if(isScrolling){
//                        if(totalItems>previousTotal){
//                            isScrolling=false;
//                            previousTotal=totalItems;
//                        }
//                    }
//                    if(!isScrolling&&(totalItems-currentItems)<=(scrollOutItems+viewThreshold)){
//                        isScrolling = true;
//                        Toast.makeText(getActivity(), "pageNumber " + String.valueOf(pageNumber), Toast.LENGTH_SHORT).show();
//                        if(pageNumber<3) {
//                            pageNumber++;
//                            getPhotos(String.valueOf(pageNumber));
//                        }
//                    }
//                }
//            }
//        });
    }

//    @Override
//    public Filter getFilter() {
//        return new Filter(){
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                String charString = constraint.toString();
//
//                if (charString.isEmpty()) {
//
//                    mFilteredList = PhotoUrls;
//                } else {
//                    List<String> filteredList = new ArrayList<>();
//
//                    for (String urls : PhotoUrls) {
//                        filteredList.add(urls);
//                    }
//
//                    mFilteredList = filteredList;
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = mFilteredList;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                mFilteredList = (List<String>) results.values;
//                myAdapter.notifyDataSetChanged();
//            }
//        };
//    }
}

