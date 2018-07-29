package com.example.administrator.opencvdemo;

import android.media.MediaActionSound;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PreviewCameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    /** 手动装载openCV库文件, 以保证手机无需安装OpenCVManager */
    static {
        System.loadLibrary("opencv_java3");
    }

    private CustomJavaCameraView cameraBridgeViewBase;
    private Mat rgbaMat, grayMat, cannyImgMat, finalChoiceMat;
    private Button captureButton;
    private String pathString, directoryPathString;
    private File directoryPathFile;

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    cameraBridgeViewBase.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_camera);

        /** 判斷指定的文件夾是否存在, 如果不存在 就創建它 */
        pathString = Environment.getExternalStorageDirectory().getAbsolutePath();
        directoryPathString = pathString + "/OpenCVDemo";
        directoryPathFile = new File(directoryPathString);
        if (!directoryPathFile.exists()) {
            directoryPathFile.mkdirs();
        }

        cameraBridgeViewBase = findViewById(R.id.javaCameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** 添加截圖的音效 */
                MediaActionSound sound = new MediaActionSound();
                sound.play(MediaActionSound.SHUTTER_CLICK);

                /** 截圖, 圖片名稱添加上目前的時間 */
                Date date = new Date();                                                             // 目前時間
                SimpleDateFormat simpleDateFormat =                                                 // 設定日期格式
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateAndTimeString = simpleDateFormat.format(date);                    // 進行轉換
                String fileNameString = directoryPathString
                        + "/picture_" + currentDateAndTimeString + ".jpeg";
                cameraBridgeViewBase.takePicture(fileNameString);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(this, "There is problem in opencv", Toast.LENGTH_SHORT).show();
        } else {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraBridgeViewBase.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        /** 定义Mat对象 */
        rgbaMat = new Mat(width, height, CvType.CV_8UC4);
        grayMat = new Mat(width, height, CvType.CV_8UC1);
        cannyImgMat = new Mat(width, height, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        rgbaMat.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        switch (Main2Activity.selectFilter) {
            case 1:
                rgbaMat = inputFrame.rgba();
                finalChoiceMat = rgbaMat;
                break;
            case 2:
                rgbaMat = inputFrame.rgba();
                Imgproc.cvtColor(rgbaMat, grayMat, Imgproc.COLOR_BGR2GRAY);
                finalChoiceMat = grayMat;
                break;
            case 3:
                rgbaMat = inputFrame.rgba();
                Imgproc.cvtColor(rgbaMat, grayMat, Imgproc.COLOR_BGR2GRAY);
                Imgproc.Canny(grayMat, cannyImgMat, 100, 80);
                finalChoiceMat = cannyImgMat;
                break;
        }
        return finalChoiceMat;
    }
}
