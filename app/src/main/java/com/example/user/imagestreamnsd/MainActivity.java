package com.example.user.imagestreamnsd;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button registerServiceButton;
    Button sendButton;
    NsdHelper mNsdHelper;
    ServerConection serverConection;
    String TAG = "ImageStreamNsd";
    byte[] dati;
    LinearLayout kamerasSkats;
    MyCamera myCamera;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initialize();
        mNsdHelper.initializeNsd();
        //dati = nolasitAtteluNoFaila();
        //textView.setText("masīva garums " + dati.length);
        //FrameLayout frameLayout = (FrameLayout) findViewById(R.id.AttelaLogs);
        // Canvas canvas = new Canvas();
        //Bitmap bitmap = BitmapFactory.decodeByteArray(dati, 0, dati.length);
        // AttelaSkats skats = new AttelaSkats(this);
        // skats.bitmap = bitmap;
        // frameLayout.addView(skats);
        // frameLayout.setBackground(bitmap);
        //  canvas.drawPicture(dati);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        myCamera.releaseCamera();

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, " onDestroy");


//nsdHelper.tearDown();
        if (mNsdHelper.mRegistrationListener != null) {
            if (mNsdHelper.isServiceRegistred)
                mNsdHelper.mNsdManager.unregisterService(mNsdHelper.mRegistrationListener);
        }
        serverConection.tearDown();
        myCamera.releaseCamera();

        super.onDestroy();

        // mNsdHelper = null;

    }

    @Override
    public void onResume() {


        super.onResume();
    }

    void initialize() {
        textView = (TextView) findViewById(R.id.textView);
        registerServiceButton = (Button) findViewById(R.id.buttonAdvertisize);
        sendButton = (Button) findViewById(R.id.button);
        registerServiceButton.setOnClickListener(registerServiceButtonListener);
        kamerasSkats = (LinearLayout) findViewById(R.id.kamerasSkats);
        mNsdHelper = new NsdHelper(this);

        serverConection = new ServerConection(mLogUpdateHandler);
        // DataOutputStream out = serverConection.getOutputStream();
        //if (out == null)
        //  Log.e(TAG, "out = null: ");

        myCamera = new MyCamera(this, mLogUpdateHandler);
        kamerasSkats.addView(myCamera.mPreview);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        textView.bringToFront();
        registerServiceButton.bringToFront();
        sendButton.bringToFront();
        seekBar.bringToFront();
        ((Button) findViewById(R.id.buttonSaakt)).bringToFront();
    }

    public byte[] nolasitAtteluNoFaila() {
        byte[] attels = new byte[1];
        File mediaStorageDir = new File("/sdcard/", "JCG Camera");

        File file = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + "20160428_215213" + ".jpg");
        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(file));
            attels = new byte[(int) file.length()];
            inputStream.readFully(attels);
            inputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("ImgStrmNsd", "Neatrod failu: ", e);
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return attels;

    }

    View.OnClickListener registerServiceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Register service
            if (serverConection.getLocalPort() > -1) {
                mNsdHelper.registerService(serverConection.getLocalPort());
                Log.d(TAG, "Izsauc registerService metodi");

            } else {
                Log.d(TAG, "ServerSocket isn't bound.");
            }
        }
    };

    private Handler mLogUpdateHandler = new Handler() {//a ui tredaa
        @Override
        public void handleMessage(Message msg) {
            if (msg.getData().getString("msg").equals("SocketInitialized")) {
                //myCamera.dao.close();
                myCamera.dao = serverConection.getOutputStream();
                if (myCamera.dao != null)
                    Log.e(TAG, "dao_ok ");

            }
            if (msg.getData().getString("msg").equals("daoErr")) {
                if (!serverConection.mSocketIsConnected()) {
//myCamera.dao.close();
                    Log.e(TAG, "dao disconnected at other side ");

                    myCamera.dao = null;

                }
            }


            if (msg.getData().getString("msg").equals("int")) {
                textView.setText("inc: " + msg.getData().getInt(null));
           myCamera.noOfFramesToSkip=msg.getData().getInt(null);
            }

        }
    };


    public void clickSend(View v) {


        String messageString = "Tests no Servera";
        if (!messageString.isEmpty()) {
            dati = myCamera.data1;
            if (dati == null)
                Log.e(TAG, "dati no preview ir null, nav ko nosūtīt");

            serverConection.sendMessage(dati);
        }
    }

    public void clickSaakt(View v) {


        if (myCamera != null) {
            myCamera.mCamera.setPreviewCallback(myCamera.getPrevCallback());
        }
    }

    SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            myCamera.jpgQuality = progress;
            textView.setText("Jpeg Quality: " + progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
