package ua.str.study.sunshine.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ua.str.study.sunshine.FetchWeatherTask;
import ua.str.study.sunshine.Utility;
import ua.str.study.sunshine.R;
import ua.str.study.sunshine.data.ForecastAdapter;
import ua.str.study.sunshine.data.WeatherContract;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FORECAST_LOADER_ID = 119;
    private static final String KEY_POSITION = "position";

    private ForecastAdapter forecastAdapter;
    private int mSelectedPosition = ListView.INVALID_POSITION;
    private Callback listener;
    private ListView listView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onLocationChanged() {
        loadWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                loadWeather();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        forecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_POSITION)) {
            mSelectedPosition = savedInstanceState.getInt(KEY_POSITION);
        }
        return rootView;
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // CursorAdapter returns a cursor at the correct position for getItem(), or null
            // if it cannot seek to that position.
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            mSelectedPosition = position;
            if (cursor != null) {
                String locationSetting = Utility.getPreferredLocation(getActivity());
                Uri data = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        locationSetting, cursor.getLong(Utility.COL_WEATHER_DATE));
                listener.onItemSelected(data);
            }
        }
    };

    private void loadWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        String temperatureUnits = prefs.getString(getString(R.string.pref_temperature_units_key),getString(R.string.pref_temperature_units_default));
        new FetchWeatherTask(getActivity()).execute(location, temperatureUnits);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSetting = Utility.getPreferredLocation(getActivity());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                Utility.FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        forecastAdapter.swapCursor(data);
        if (mSelectedPosition != ListView.INVALID_POSITION) {
            listView.smoothScrollToPosition(mSelectedPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        forecastAdapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSelectedPosition != ListView.INVALID_POSITION) {
            outState.putInt(KEY_POSITION, mSelectedPosition);
        }
    }
}