package ua.str.study.sunshine.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import ua.str.study.sunshine.fragments.MainPreferenceFragment;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }
}