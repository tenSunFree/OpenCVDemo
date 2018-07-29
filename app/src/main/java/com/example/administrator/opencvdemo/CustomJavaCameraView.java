package com.example.administrator.opencvdemo;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

import org.opencv.android.JavaCameraView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CustomJavaCameraView extends JavaCameraView implements Camera.PictureCallback {

    static {
        System.loadLibrary("native-lib");
    }

    private String pictureFileName;

    public CustomJavaCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        /** 產生一個pictureFileName路徑的檔案, 並將byte形式的字串寫入 */
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(pictureFileName);              // 建立FileOutputStream物件
            fileOutputStream.write(data);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 截取CustomJavaCameraView的圖片 */
    public void takePicture(final String pictureFileName) {
        this.pictureFileName = pictureFileName;
        mCamera.setPreviewCallback(null);
        mCamera.takePicture(null, null, this);                                   // PictureCallback is implements by the current class
    }
}
