package ua.str.study.sunshine.activitys;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import ua.str.study.sunshine.R;
import ua.str.study.sunshine.fragments.DetailFragment;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment()).commit();
        }
    }
}