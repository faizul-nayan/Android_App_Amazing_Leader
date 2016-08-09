package a15thaugust.blackhunter.com.shokdibos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {

    Button makeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.app_icon);

        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-3985395576754717~6293075382");

        AdView adView = (AdView) this.findViewById(R.id.ad_view);
        AdView adView2 = (AdView) this.findViewById(R.id.ad_view2);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdRequest adRequest2 = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView2.loadAd(adRequest2);


        makeBtn = (Button) findViewById(R.id.makeBtn);
        makeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ImageMakerActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
            alt_bld.setIcon(R.mipmap.app_icon);
            alt_bld.setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // Action for 'Yes' Button

                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Thank you for using Amazing Leader",
                                            Toast.LENGTH_LONG).show();
                                    finish();

                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // Action for 'NO' Button

                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = alt_bld.create();
            // Title for AlertDialog
            alert.setTitle("Exit Amazing Leader?");
            // Icon for AlertDialog
            alert.show();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
