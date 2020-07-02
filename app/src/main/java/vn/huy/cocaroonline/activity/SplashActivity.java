package vn.huy.cocaroonline.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import vn.huy.cocaroonline.R;

public class SplashActivity extends AppCompatActivity {

    private SplashActivity binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    public void playGame(View view) {
        startActivity(new Intent(this, PlayActivity.class));
    }

    public void readMe(View view) {
        startActivity(new Intent(this, ReadMeActivity.class));
    }

    public void exitGame(View view) {
        finish();
        System.exit(0);
    }

    public void playOnlineGame(View view) {
        startActivity(new Intent(this, PlayOnlineActivity.class));
    }
}