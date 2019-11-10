package com.example.sensordatacollector;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sensordatacollector.models.AppDatabase;
import com.example.sensordatacollector.models.SensorDataModel;

import java.util.List;

public class RecyclerActivity extends Activity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private class getDataTask extends AsyncTask<Object, Void, List<SensorDataModel>>{
        @Override
        protected void onPostExecute(List<SensorDataModel> aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new SensorDataAdapter(aVoid);
            recyclerView.setAdapter(mAdapter);
        }

        @Override
        protected List<SensorDataModel> doInBackground(Object... objects) {
            Context ctx = (Context) objects[0];
            List<SensorDataModel> lsdm = AppDatabase.getInstance(ctx).sensorDataDAO().getAllItems();
            return lsdm;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewdata);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        AsyncTask atsk = new getDataTask();
        atsk.execute(getApplicationContext());
    }
}
