package com.simrin.galleryapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var PhotoUrls: MutableList<String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView = view!!.findViewById(R.id.recyclerSearchView)
        PhotoUrls = ArrayList()
    }

    private fun getPhotos(userInput: String) {
        PhotoUrls!!.clear()
        val retrofit = Retrofit.Builder()
                .baseUrl(SearchAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val api = retrofit.create<SearchAPI>(SearchAPI::class.java)


        val call = api.getPhoto("flickr.photos.search", "6f102c62f41998d151e5a1b48713cf13",
                "json", "1", "url_s", userInput)

        call.enqueue(object : Callback<PhotoList> {
            override fun onResponse(call: Call<PhotoList>, response: Response<PhotoList>) {
                val result = response.body()

                if (result!!.photos != null) {
                    for (photo in result.photos.photo) {
                        PhotoUrls!!.add(photo.urlS)
                    }
                    setuprecyclerview(PhotoUrls)
                }
            }

            override fun onFailure(call: Call<PhotoList>, t: Throwable) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.search_menu, menu)
        val item = menu!!.findItem(R.id.action_search)
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)

        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {


            override fun onQueryTextSubmit(query: String): Boolean {
                getPhotos(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

        })
    }

    private fun setuprecyclerview(photoUrls: List<String>?) {
        val myAdapter = RecyclerViewAdapter(activity, photoUrls)
        val layoutManager = GridLayoutManager(activity, 3)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = myAdapter
    }
}

