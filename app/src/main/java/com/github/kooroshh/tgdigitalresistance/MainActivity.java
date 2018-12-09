package com.github.kooroshh.tgdigitalresistance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;

public class MainActivity extends AppCompatActivity implements IResistanceServerResult {
    private final Context mContext = this ;
    ImageView btnConnect,imgStatus;
    TextView txtActivate ;
    HttpClient mClient = new HttpClient();
    ProgressDialog dlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imgBackground = findViewById(R.id.background);
        imgBackground.setImageResource(R.drawable.bg);
        btnConnect = findViewById(R.id.img_power);
        imgStatus = findViewById(R.id.imgStatus);
        txtActivate = findViewById(R.id.txtActivate);
        btnConnect.setOnClickListener(btnConnect_clicked );
        imgStatus.setOnClickListener(btnConnect_clicked);
        dlg = new ProgressDialog(this);
        dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dlg.setMessage("Please Wait ...");
        dlg.setCancelable(false);
        txtActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Console.setProxy(mContext);
            }
        });
        if(ResistanceService.servers == null || ResistanceService.servers.size() == 0){
            mClient.Get(HttpClient.API_URL,this);
            dlg.show();
        }
        if(ResistanceService.isConnected){
            txtActivate.setVisibility(View.VISIBLE);
            imgStatus.setImageResource(R.drawable.secured);
            btnConnect.setImageResource(R.drawable.power_off);
        }else{
            txtActivate.setVisibility(View.GONE);
            imgStatus.setImageResource(R.drawable.not_secured);
            btnConnect.setImageResource(R.drawable.power_on);
        }

    }
    View.OnClickListener btnConnect_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(ResistanceService.isConnected){
                stopService(new Intent(mContext,ResistanceService.class));
                txtActivate.setVisibility(View.GONE);
                imgStatus.setImageResource(R.drawable.not_secured);
                btnConnect.setImageResource(R.drawable.power_on);
            }else{
                Tapsell.requestAd(mContext, null,new TapsellAdRequestOptions(TapsellAdRequestOptions.CACHE_TYPE_CACHED), new TapsellAdRequestListener() {
                    @Override
                    public void onError (String error)
                    {
                        Log.d("TGDigitalResistance","AD ERR");

                    }

                    @Override
                    public void onAdAvailable (TapsellAd ad)
                    {
                        TapsellShowOptions options = new TapsellShowOptions();
                        options.setBackDisabled(true);
                        ad.show(mContext,options , new TapsellAdShowListener() {
                            @Override
                            public void onOpened (TapsellAd ad)
                            {
                                Log.d("TGDigitalResistance","AD Opened");
                            }
                            @Override
                            public void onClosed (TapsellAd ad)
                            {
                                Log.d("TGDigitalResistance","AD Closed");

                            }
                        });
                    }

                    @Override
                    public void onNoAdAvailable ()
                    {
                        Log.d("TGDigitalResistance","AD NOT AVAILABLE");

                    }

                    @Override
                    public void onNoNetwork ()
                    {
                        Log.d("TGDigitalResistance","AD NO NETWORK");

                    }

                    @Override
                    public void onExpiring (TapsellAd ad)
                    {
                        Log.d("TGDigitalResistance","AD EXPIRING");

                    }
                });
                if(ResistanceService.servers != null && ResistanceService.servers.size() > 0){
                    ResistanceServer server = ResistanceService.servers.get(0);
                    if(ResistanceService.servers.size() > 1) {
                        Random r = new Random();
                        int rand = r.nextInt(ResistanceService.servers.size() - 1);
                        server = ResistanceService.servers.get(rand);
                    }

                    Intent i = new Intent(mContext,ResistanceService.class);
                    i.putExtra("SERVER",server.server);
                    i.putExtra("ENCRYPTION",server.encryption);
                    i.putExtra("PASSWORD",server.password);
                    i.putExtra("PORT",server.port);
                    startService(i);
                    txtActivate.setVisibility(View.VISIBLE);
                    imgStatus.setImageResource(R.drawable.secured);
                    btnConnect.setImageResource(R.drawable.power_off);
                    Console.setProxy(mContext);

                }else{
                    AlertDialog dlg = new AlertDialog.Builder(mContext).setTitle("Error").setMessage("Unable connect to the server").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create();
                    dlg.show();
                }
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResistanceServerAvailable(List<ResistanceServer> servers) {
        ResistanceService.servers = servers;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!isFinishing())
                        dlg.dismiss();
                }catch (Exception E){
                    finish();
                }
            }
        });
    }

    @Override
    public void onResistanceServerNotAvailable() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!isFinishing())
                        dlg.dismiss();
                    AlertDialog dlg = new AlertDialog.Builder(mContext).setTitle("Error").setMessage("Unable connect to the server").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create();
                    dlg.show();
                }catch (Exception E){
                    finish();
                }
            }
        });
    }
}
