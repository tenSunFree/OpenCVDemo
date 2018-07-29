package com.example.administrator.opencvdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Main2Activity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    public static int selectFilter = 1;
    private static final int REQUEST_CODE = 1000;
    private String[] requestPermissionStrings = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Button rgbaCameraButton, grayCameraButton, cannyCameraButton, pickButton;
    private ImageView imageView;
    private Bitmap bitmap, processImgBitmap;
    private Mat srcMat, destMat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        /** 確認如果沒有取得相關權限, 會跳出請求權限 */
        if (ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Main2Activity.this, requestPermissionStrings, REQUEST_CODE);
        }

        rgbaCameraButton = findViewById(R.id.rgbaCameraButton);
        rgbaCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** 設定成一般模式, 然後跳轉到PreviewCameraActivity */
                selectFilter = 1;
                Intent intent = new Intent(Main2Activity.this, PreviewCameraActivity.class);
                startActivity(intent);
            }
        });

        grayCameraButton = findViewById(R.id.grayCameraButton);
        grayCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** 設定成黑白模式, 然後跳轉到PreviewCameraActivity */
                selectFilter = 2;
                Intent intent = new Intent(Main2Activity.this, PreviewCameraActivity.class);
                startActivity(intent);
            }
        });

        cannyCameraButton = findViewById(R.id.cannyCameraButton);
        cannyCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** 設定成邊緣檢測模式, 然後跳轉到PreviewCameraActivity */
                selectFilter = 3;
                Intent intent = new Intent(Main2Activity.this, PreviewCameraActivity.class);
                startActivity(intent);
            }
        });

        imageView = findViewById(R.id.imageView);
        pickButton = findViewById(R.id.pickButton);
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** 取得imageView的bitmap, 對bitmap進行高斯模糊的處理後, 再把bitmap賦予imageView */
                bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                gaussianBlur(bitmap);
            }
        });
    }

    /**
     * 監聽是否成功取得權限, 以及想做些什麼的對應行為
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:

                /** 只要拒絕某一個權限, 就會強制結束APP */
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
        }
    }

    /**
     * 高斯模糊的效果
     */
    public void gaussianBlur(Bitmap bitmap) {
        srcMat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        destMat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, srcMat);
        Imgproc.GaussianBlur(srcMat, destMat, new Size(21, 21), 0);
        processImgBitmap = Bitmap.createBitmap(destMat.cols(), destMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(destMat, processImgBitmap);
        imageView.setImageBitmap(processImgBitmap);
    }

    public native String stringFromJNI();
}
