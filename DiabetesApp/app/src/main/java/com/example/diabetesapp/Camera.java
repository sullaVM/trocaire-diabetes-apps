package com.example.diabetesapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class Camera extends AppCompatActivity {

    final int RequestCameraPermissionID = 1001;
    ImageView back, done;
    SurfaceView cameraView;
    CameraSource cameraSource;

    Bitmap image;
    Boolean diastole;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Intent i = getIntent();
        diastole = i.getBooleanExtra("diastole", true);

        cameraView = findViewById(R.id.surface_view);

        done = findViewById(R.id.enter);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImage();
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    private void saveAndReturn(String data) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("diastole", diastole);
        resultIntent.putExtra("input", data);
        setResult(InputPressureSugar.RESULT_OK, resultIntent);
        finish();
    }

    private void back() {
        this.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(Camera.this,
                                new String[]{Manifest.permission.CAMERA},
                                RequestCameraPermissionID);
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
            }
        });
    }

    private void save() {
        File photoFile = new File(this.getExternalFilesDir(null), "Image.jpg");
        try {
            FileOutputStream out = new FileOutputStream(photoFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takeImage() {
        cameraSource.takePicture(null, new CameraSource.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] bytes) {
                try {
                    Bitmap loadedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(loadedImage, loadedImage.getWidth(), loadedImage.getHeight(), true);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                    Mat mat = new Mat(rotatedBitmap.getHeight(), rotatedBitmap.getWidth(), CvType.CV_8UC3);

                    Utils.bitmapToMat(rotatedBitmap, mat);
                    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);
                    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
                    Imgproc.threshold(mat, mat, 10, 255, Imgproc.THRESH_OTSU);
                    Utils.matToBitmap(mat, rotatedBitmap);
                    image = rotatedBitmap;

                    int rect_h = (int)(mat.height() * 0.25);
                    int rect_w = (int)(mat.width() * 0.8);
                    Rect roi = new Rect(mat.width() / 2 - (rect_w / 2), mat.height() / 2 - (rect_h / 2), rect_w, rect_h);
                    Mat crop = new Mat(mat, roi);
                    Bitmap bmp = Bitmap.createBitmap(crop.width(), crop.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(crop, bmp);
                    image = bmp;
                    save();

                    StringBuilder sb = new StringBuilder();

                    int new_rect_h = crop.height();
                    int new_rect_w = crop.width() / 3;

                    for(int i = 0; i < 3; i++){
                        roi = new Rect(new_rect_w*i, 0, new_rect_w, new_rect_h);
                        Mat piece = new Mat(crop, roi);

                        double[] result = matching(Imgproc.TM_CCOEFF_NORMED, piece);
                        double value = Double.MIN_VALUE;
                        int index = 0;
                        for (int j = 0; j < 10; j++) {
                            if (result[j] > value) {
                                value = result[j];
                                index = j;
                            }
                        }
                        if (value > .2) sb.append(index);
                    }
                    saveAndReturn(sb.toString());

                } catch (Exception ex) {
                    Log.w("Camera", "Detector dependencies are not yet available");
                }
            }
        });
    }

    public double singleMatching(int match_method, Bitmap bitmap, Mat img){
        Mat templ = new Mat();
        Utils.bitmapToMat(bitmap, templ);
        Imgproc.cvtColor(templ, templ, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.resize(templ, templ, new Size(img.width(), img.height()), 0, 0);

        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        Imgproc.matchTemplate(img, templ, result, match_method);
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        return mmr.maxVal;
    }

    public double[] matching(int match_method, Mat img) {
        double[] per = new double[10];

        int current_num = 0;
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.zero);
        per[current_num] = singleMatching(match_method, bitmap, img);

        current_num++;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.one);
        per[current_num] = singleMatching(match_method, bitmap, img);

        current_num++;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.two);
        per[current_num] = singleMatching(match_method, bitmap, img);

        current_num++;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.three);
        per[current_num] = singleMatching(match_method, bitmap, img);

        current_num++;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.four);
        per[current_num] = singleMatching(match_method, bitmap, img);

        current_num++;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.five);
        per[current_num] = singleMatching(match_method, bitmap, img);

        current_num++;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.six);
        per[current_num] = singleMatching(match_method, bitmap, img);

        current_num++;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.seven);
        per[current_num] = singleMatching(match_method, bitmap, img);

        current_num++;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.eight);
        per[current_num] = singleMatching(match_method, bitmap, img);

        current_num++;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.nine);
        per[current_num] = singleMatching(match_method, bitmap, img);

        return per;
    }
}