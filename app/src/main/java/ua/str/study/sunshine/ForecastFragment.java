package ua.str.study.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForecastFragment extends Fragment {
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    // Will contain the raw JSON response as a string.
    String forecastJsonStr = null;

    public ForecastFragment() {
        //forecast Cherkasy
        //http://api.openweathermap.org/data/2.5/forecast/daily?id=710791&mode=json&units=metric&cnt=7
        //correct app url
        //http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new FetchWeatherTask().execute("94043");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        List<String> list = new ArrayList<>(Arrays.asList(
                "Today - Sunny - 88 / 63",
                "Tomorrow - Foggy - 70 / 46",
                "Weds - Cloudy - 72 / 63",
                "Thurs - Rainy - 64 / 51",
                "Fri - Foggy - 70 / 46",
                "Sat - Sunny - 76 / 68"
        ));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, list);
        ((ListView)rootView.findViewById(R.id.listView_forecast)).setAdapter(adapter);
        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = this.getClass().getSimpleName();

        protected Void doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }
            try {
//                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
                Uri uri = Uri.parse("http://api.openweathermap.org/data/2.5/forecast/daily?").buildUpon()
                        .appendQueryParameter("q", params[0])
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("cnt", "7")
                        .build();
                URL url = new URL(uri.toString());

                Log.v(LOG_TAG, "URI: " + uri.toString());


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}