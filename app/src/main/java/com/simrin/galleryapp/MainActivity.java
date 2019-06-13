package com.simrin.galleryapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Snackbar snackbar;
    private CoordinatorLayout layout;
    private boolean isConnected = true;
    private boolean monitoringConnectivity = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        layout = findViewById(R.id.placeSnackbar);
        navigation.setOnNavigationItemSelectedListener(this);
        if(!isNetworkAvailable()){
            showSnackBar("Internet Not There", layout);
        }else{
            loadFragment(new HomeFragment());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isNetworkAvailable()){
            showSnackBar("Internet Not There", layout);
        }
    }

    @Override
    protected void onPause() {
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    private ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isConnected = true;
        }

        @Override
        public void onLost(Network network) {
            isConnected = false;
            showSnackBar("Internet Not There",(CoordinatorLayout) findViewById(R.id.placeSnackbar));
        }
    };

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
             isConnected = mNetworkInfo != null &&
                    mNetworkInfo.isConnectedOrConnecting();
            if (!isConnected) {
                mConnectivityManager.registerNetworkCallback(
                        new NetworkRequest.Builder()
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                .build(), connectivityCallback);
                monitoringConnectivity = true;
            }
            return isConnected;

        }catch (NullPointerException e){
            return false;

        }
    }

    private void showSnackBar(String string, CoordinatorLayout layout)
    {
        snackbar = Snackbar
                .make(layout, string, Snackbar.LENGTH_INDEFINITE).
                        setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                                if(!isNetworkAvailable()){
                                    showSnackBar("Internet Not There",(CoordinatorLayout) findViewById(R.id.placeSnackbar));
                                }else{
                                    loadFragment(new HomeFragment());
                                }
                            }
                        });
        snackbar.show();
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment!=null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
            switch(item.getItemId()){
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    break;

                case R.id.navigation_search:
                    fragment = new SearchFragment();
                    break;

            }
            return loadFragment(fragment);
    }
}