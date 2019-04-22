package com.example.abc.presentervideoplayer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Main2Activity extends AppCompatActivity {
    VideoView videoView;
    MediaController mediaController;
    private MediaPlayer mmp;
    private TextView txtString;
    // VideoView iv;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main2);

        txtString = (TextView) findViewById(R.id.testView1);

        /*if (str.equals("no_result")){

            switchFunction("error_text");
        }else {


            switchFunction(str);
        }
*/


        final ArrayList<String> aaa = new ArrayList<>();
        aaa.add("7");
        aaa.add("8");
        aaa.add("8");
        aaa.add("9");
        aaa.add("10");
        aaa.add("11");
        aaa.add("12");
        aaa.add("13");
        aaa.add("14");
        aaa.add("15");
        aaa.add("17");
        aaa.add("18");
        aaa.add("19");
        aaa.add("20");
        aaa.add("21");
        aaa.add("121");
        aaa.add("122");
        aaa.add("123");
        aaa.add("125");


        txtString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int Low = 7;
                int High = 21;
                int Result = r.nextInt(High - Low) + Low;
                String numberAsString = Integer.toString(Result);


            }


        });

        //////////////////////////////////////////////////////////////////////////////





        //////////////////////////////////////////////////////////////////////////////////////

        videoView = (VideoView) findViewById(R.id.videoView1);

        // iv=(VideoView) findViewById(R.id.imv);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        //  playvideo("");


        // switchFunction("welcome");
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // playvideo("vidle_one");


                videoView.seekTo(videoView.getDuration());




                //  playvideoTwo();
            }
        });


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

// Attach an listener to read the data at our posts reference


    }

    @Override
    public void onResume() {
        super.onResume();
    }





}
