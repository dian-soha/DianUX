package io.playtext.dianux;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.FillEventHistory;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MainActivity mainActivity;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;




        TextView hello_world = findViewById(R.id.textView_dian);
        hello_world.setText("Dian Soha's UX design Test App");
        hello_world.setGravity(Gravity.CENTER);
        hello_world.setTextSize(22);

        Button dialogs = findViewById(R.id.dialogs);
        dialogs.setOnClickListener(this);

        Button database_viewer = findViewById(R.id.database_viewer);
        database_viewer.setOnClickListener(this);

    }





    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.dialogs:

                Intent intent1 = new Intent(this, DialogsActivity.class);
                startActivity(intent1);
                finish();

                break;

            case R.id.database_viewer:

                Intent intent2 = new Intent(this, StoreActivity.class);
                startActivity(intent2);
                finish();

                break;


            default:
                break;
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
