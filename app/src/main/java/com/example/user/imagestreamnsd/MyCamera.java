package com.example.user.imagestreamnsd;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 2017.01.08..
 */

/**
 * Created by user on 2016.04.16..
 */

public class MyCamera {
    Camera mCamera;
    CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Context myContext;

    private static final String TAG = "MyCamera";

    private boolean cameraFront = false;
    private Camera.Parameters mCameraParam;
    private int previewSizeWidth = 200;
    private TextView tekstaLauks;
    private int previewSizeHeight = 300;
    private Intent intent;
    byte[] data1;
    int frameCounter = 0;
    int noOfFramesToSkip=2;
    Rect rect;
    DataOutputStream dao;
    ByteArrayOutputStream bao;
    byte[] callbackBuffer;
    int jpgQuality = 50;
    Handler mUpdateHandler;
    // private final RawPictureCallback mRawPictureCallback = new RawPictureCallback();

    public MyCamera(Context context, Handler handler) {
        myContext = context;
        mUpdateHandler = handler;
        initialize();
    }


    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }


    public void initialize() {

        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();

        }
        mCamera = Camera.open(findBackFacingCamera());
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        previewSizeHeight = parameters.getPreviewSize().height;
        previewSizeWidth = parameters.getPreviewSize().width;
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
        int buferaIzmers = previewSizeHeight * previewSizeWidth;
        buferaIzmers = buferaIzmers / 2 + buferaIzmers;
        Toast toast = Toast.makeText(myContext, Integer.toString(previewSizeWidth) + "x" + Integer.toString(previewSizeHeight), Toast.LENGTH_LONG);
        toast.show();
        callbackBuffer = new byte[buferaIzmers];
        //callbackBuffer2 = new byte[buferaIzmers];
        rect = new Rect(0, 0, previewSizeWidth, previewSizeHeight);


        mPreview = new CameraPreview(myContext, mCamera);
        // mCamera.addCallbackBuffer(callbackBuffer);


        mPreview.refreshCamera(mCamera);

        Camera.PreviewCallback previewCallback = getPrevCallback();
        if (previewCallback == null)
            Log.e(TAG, "Callback = null ");
        else
            Log.e(TAG, "prew Callback ok ");
        mCamera.addCallbackBuffer(callbackBuffer);
        // mCamera.setPreviewCallbackWithBuffer(previewCallback);
        bao = new ByteArrayOutputStream();
    }


    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Toast toast = Toast.makeText(myContext, "autofocus done", Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    Camera.PreviewCallback getPrevCallback() {
        Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                // Log.d(TAG, " onPreview called ");

                frameCounter++;
                if (frameCounter >= noOfFramesToSkip) {
                    frameCounter = 0;
                    YuvImage img = new YuvImage(data, ImageFormat.NV21, previewSizeWidth, previewSizeHeight, null);

                    if (dao != null) {
                        try {
                            bao.reset();
                            img.compressToJpeg(rect, jpgQuality, bao);
                            bao.flush();
                            dao.writeInt(bao.size());
                            bao.writeTo(dao);
                            //Log.d(TAG, "compressing to dao ");
                            dao.flush();
                        } catch (IOException e) {
                            Log.e(TAG, "flushing from onPreview Frame err");
                            updateMessages("daoErr");
                        }
                    } else
                        Log.e(TAG, "dao = null ");


                }
                data1 = callbackBuffer;
                // Camera.Parameters parameters = camera.getParameters();
                // int imageFormat = parameters.getPreviewFormat();
                // if (imageFormat == ImageFormat.NV21) {
                // Rect rect = new Rect(0, 0, PreviewSizeWidth, PreviewSizeHeight);
                //YuvImage img = new YuvImage(data, ImageFormat.NV21, PreviewSizeWidth, PreviewSizeHeight, null);
                // mCamera.addCallbackBuffer(callbackBuffer);
                //  callbackBuffer = data;
                // if(izsauktsSaglabat){
                //     saglabatRaw(data);
                //    izsauktsSaglabat = false;
                // }
                //intent.putExtra("vards", data);
                //skats = new LinijuPrieksskats(myContext);
                // bitmapThread.setDati(callbackBuffer2);

                //skats.refreshDrawableState();

                //startActivity(intent);
                // LinijuMekletajs mekletajs = new LinijuMekletajs();
                // int ekstremuSkaits = mekletajs.skaitiitEkstremus(data);
                // tekstaLauks.setText("Nevienmērība "+ekstremuSkaits);

                // Toast toast = Toast.makeText(myContext, "Nevienmerība "+ ekstremuSkaits, Toast.LENGTH_SHORT);
                // toast.show();
/*
                 OutputStream outStream = null;
                // File file = new File(String.valueOf(getOutputMediaFile()));
                 try {
                     outStream = new FileOutputStream(getOutputMediaFile());
                    //img.compressToJpeg(rect, 100, outStream);
                    outStream.write(data);
                     outStream.flush();
                     outStream.close();

                     Toast toast = Toast.makeText(myContext, "Picture saved: ", Toast.LENGTH_LONG);
                     toast.show();

                 } catch (FileNotFoundException e) {
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
*/
                //  }
                //  mPreview.refreshCamera(mCamera);

            }
        };
        return previewCallback;
    }
/*
    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file
                File pictureFile = getOutputMediaFile();

                if (pictureFile == null) {
                    return;
                }
                try {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Toast toast = Toast.makeText(myContext, "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
                    toast.show();

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }
*/


    //make picture and save to a folder
    private File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "JCG Camera");

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".raw");

        return mediaFile;
    }

    private void saglabatRaw(byte[] data) {

        OutputStream outStream = null;
        // File file = new File(String.valueOf(getOutputMediaFile()));
        try {
            outStream = new FileOutputStream(getOutputMediaFile());
            //img.compressToJpeg(rect, 100, outStream);
            outStream.write(data);
            outStream.flush();
            outStream.close();

            Toast toast = Toast.makeText(myContext, "Picture saved: ", Toast.LENGTH_LONG);
            toast.show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    public synchronized void updateMessages(String msg) {
        Log.e(TAG, "Updating message: " + msg);


        Bundle messageBundle = new Bundle();
        messageBundle.putString("msg", msg);

        Message message = new Message();
        message.setData(messageBundle);
        mUpdateHandler.sendMessage(message);

    }
}


