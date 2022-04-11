//Jack Weir S1830098
package org.me.gcu.weir_jack_s1830098;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private TextView roadData;
    private TextView moreInfo;
    private Button currentIncidentsButton;
    private Button currentRoadworksButton;
    private Button plannedRoadworksButton;
    private String result = "";
    private SearchView roadSearch;
    private TextView header;
    private ProgressBar progressBar;


    private String currentIncidentsUrl = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String currentRoadworksUrl = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String plannedRoadworksUrl = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";


    private ArrayList<RoadItem> roadItems;
    private SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("MyTag", "in onCreate");

        header = (TextView) findViewById(R.id.header);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        roadSearch = (SearchView) findViewById(R.id.roadSearch);
        roadSearch.setQueryHint("Enter search query");

        roadData = (TextView) findViewById(R.id.roadData);
        roadData.setOnClickListener(this);


        currentIncidentsButton = (Button) findViewById(R.id.currentIncidentsButton);
        currentIncidentsButton.setOnClickListener(this);

        currentRoadworksButton = (Button) findViewById(R.id.currentRoadworksButton);
        currentRoadworksButton.setOnClickListener(this);

        plannedRoadworksButton = (Button) findViewById(R.id.plannedRoadworksButton);
        plannedRoadworksButton.setOnClickListener(this);

        if(!isNetworkAvailable())
        {
            new AlertDialog.Builder(this)
                    .setTitle("Internet Connection Alert")
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).show();
        }
    }

    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {

                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {

                        return true;
                    }
                }
            }
        }

        return false;

    }

    @Override
    public void onClick(View view) {
        if (currentIncidentsButton.isPressed()) {
            new Thread(new Task(currentIncidentsUrl)).start();
            header.setText("Current Incidents");
            progressBar.setVisibility(View.VISIBLE);
        }

        if (currentRoadworksButton.isPressed()) {
            new Thread(new Task(currentRoadworksUrl)).start();
            header.setText("Current Roadworks");
            progressBar.setVisibility(View.VISIBLE);
        }

        if (plannedRoadworksButton.isPressed()) {
            new Thread(new Task(plannedRoadworksUrl)).start();
            header.setText("Planned Roadworks");
            progressBar.setVisibility(View.VISIBLE);
        }

        if (roadData.isPressed()) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_window, null);
            moreInfo = (TextView) popupView.findViewById(R.id.moreInfo);

            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popupWindow.dismiss();
                    return true;
                }
            });
        }
    }


    public ArrayList<RoadItem> getData(String dataToParse) {

        ArrayList<RoadItem> roadItems = null;
        roadItems = new ArrayList<RoadItem>();
        RoadItem curItem = new RoadItem();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader(dataToParse));

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("channel")) {
                        roadItems = new ArrayList<RoadItem>();

                    } else if (xpp.getName().equalsIgnoreCase("item")) {
                        Log.e("MyTag", "Item start found");
                        curItem = new RoadItem();

                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        String temp = xpp.nextText();
                        Log.e("MyTag", "Title is " + temp);
                        curItem.setTitle(temp);

                    } else if (xpp.getName().equalsIgnoreCase("description")) {
                        String temp = xpp.nextText();
                        Log.e("MyTag", "Description is " + temp);
                        curItem.setDescription(temp);

                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                        String temp = xpp.nextText();
                        Log.e("MyTag", "Link is " + temp);
                        curItem.setLink(temp);

                    } else if (xpp.getName().equalsIgnoreCase("georss:point")) {
                        String temp = xpp.nextText();
                        Log.e("MyTag", "GeoPoint is " + temp);
                        curItem.setGeoPoint(temp);

                    } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                        String temp = xpp.nextText();
                        Log.e("MyTag", "pubDate is " + temp);
                        curItem.setPubDate(temp);
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        if (curItem.getTitle().toLowerCase().contains(roadSearch.getQuery().toString().toLowerCase())){
                            roadItems.add(curItem);
                        }
                        Log.e("MyTag", "Item is " + curItem.toString());
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException ae1) {
            Log.e("MyTag", "Parsing Error " + ae1.toString());
        } catch (IOException ae1) {
            Log.e("MyTag", "IO Error during parsing");
        }
        Log.e("MyTag", "End of Document");
        return roadItems;
    }

    private class Task implements Runnable
    {
        private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                Log.e("MyTag","after ready");
                if (!result.equals("")){
                    result = "";
                }
                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception in run");
            }

            final List<RoadItem> roadItems;
            roadItems = getData(result);

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    if(roadItems != null){
                        roadData.setText("");
                        for(Object o : roadItems){
                            roadData.append(o.toString());
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }
}