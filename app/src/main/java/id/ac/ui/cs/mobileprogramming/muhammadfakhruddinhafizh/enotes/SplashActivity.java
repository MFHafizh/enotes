package id.ac.ui.cs.mobileprogramming.muhammadfakhruddinhafizh.enotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by AbhiAndroid
 */

public class SplashActivity extends Activity {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_file);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this, NoteListActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);

    }
}