package com.chenghsi.lise.gas.other;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.chenghsi.lise.gas.Constant;
import com.chenghsi.lise.gas.R;
import com.chenghsi.lise.gas.task.TaskActivity;


public class RoutePlanningActivity extends Activity
{
    private WebView webView;
    private LocationManager locationManager;
    private Location mostRecentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_planning);

        webView = (WebView) findViewById(R.id.WebView_Map);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(Constant.MAP_URL);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        enableLocationUpdate();
        getOldLocation();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        disableLocationUpdate();
    }

    private void enableLocationUpdate()
    {
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);
            Toast.makeText(this, R.string.navigation_use_gps, Toast.LENGTH_SHORT).show();
        }
        else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, locationListener);
            Toast.makeText(this, R.string.navigation_use_network, Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, R.string.navigation_no_location_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void disableLocationUpdate()
    {
        locationManager.removeUpdates(locationListener);
        Toast.makeText(this, R.string.navigation_close_navigation, Toast.LENGTH_SHORT).show();
    }

    private void getOldLocation()
    {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location==null)
        {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if(location!=null)
        {
            Log.i("RouterPlanningActivity", "Get last location successfully.");
            locationListener.onLocationChanged(location);
        }
        else
        {
            Log.i("RouterPlanningActivity", "Fail to get last location.");
        }
    }

    private String getAllAddress()
    {
        String str="";
        TaskActivity taskActivity = new TaskActivity();
        //TODO 需要檢查
        boolean [] state = taskActivity.getTempState();
        String [] tempAddress = taskActivity.getTempAddress();
        Log.i("Router", "state Length:"+ state.length);
        for(int i=0; i<state.length; i++)
        {
            if(state[i]) {
                str += tempAddress[i] + ",";
                Log.i("Router", str);
            }else{
                Toast.makeText(this, "尚未承接任何案子", Toast.LENGTH_SHORT).show();
            }
            //TODO 新增任務清單上已承接的地點
        }

        if(str.equals("")) return str;
        else return str.substring(0,str.length()-1);
    }

    public void onClick_btn_navigation(View view)
    {
        getOldLocation();
        String address = getAllAddress();
        webView.loadUrl("javascript:calcRoute(\"" + address + "\")");
    }

    private LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            String latLog = Double.toString(latitude) + "," + Double.toString(longitude);
            Log.i("RouterPlanningActivity", "Current location: " + latLog);
            String centerURL = "javascript:centerAt(" + latLog + ")";
            String url = "javascript:markMe(" + latLog + ")";
            webView.loadUrl(centerURL);
            webView.loadUrl(url);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            //
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            //
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            //
        }
    };
}
