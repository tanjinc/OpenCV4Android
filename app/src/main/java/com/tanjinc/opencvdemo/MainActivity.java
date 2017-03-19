package com.tanjinc.opencvdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private String mImagePath = "/sdcard/DCIM/Selfie/111.jpg";
    private Uri mUri;
    private Bitmap mRGBBitmap;
    private Bitmap mGrayBitmap;

    private Button mTranslateBtn;
    private ImageView mImageView;

    private boolean flag;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "video onManagerConnected: success");
                    break;
                case BaseLoaderCallback.INIT_FAILED:
                    Log.i(TAG, "video onManagerConnected: init_failed");
                    break;
                default:
                    Log.e(TAG, "video onManagerConnected: error");
                    break;
            }
        }

        @Override
        public void onPackageInstall(int operation, InstallCallbackInterface callback) {
            super.onPackageInstall(operation, callback);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mUri = getIntent().getData();
        mUri = Uri.parse(mImagePath);
        mImageView = (ImageView) findViewById(R.id.image_iv);
        mTranslateBtn = (Button) findViewById(R.id.translate_btn);
        mTranslateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procSrc2Gray();
                if(flag){
                    mImageView.setImageBitmap(mGrayBitmap);
                    mTranslateBtn.setText("查看原图");
                    flag = false;
                }
                else{
                    mImageView.setImageBitmap(mRGBBitmap);
                    mTranslateBtn.setText("灰度化");
                    flag = true;
                }
            }
        });

        mImageView.setImageURI(mUri);
    }

    public void procSrc2Gray(){
        if (mGrayBitmap != null) {
            return;
        }
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        mRGBBitmap = BitmapFactory.decodeFile(mUri.getPath());
        mGrayBitmap = Bitmap.createBitmap(mRGBBitmap.getWidth(), mRGBBitmap.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(mRGBBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, mGrayBitmap); //convert mat to bitmap
        Log.i(TAG, "procSrc2Gray sucess...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
