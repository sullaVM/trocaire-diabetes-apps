package com.example.diabetesapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.view.View;
import android.graphics.Canvas;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.vision.Frame;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import android.graphics.Matrix;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.Point;

public class Camera extends AppCompatActivity {

    final int RequestCameraPermissionID = 1001;
    ImageView back, done;
    SurfaceView cameraView;
    CameraSource cameraSource;
    Button saveButton;

    Bitmap image;
    int n = 0;

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

        cameraView = findViewById(R.id.surface_view);

        done = findViewById(R.id.done);
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

        saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    private void saveAndReturn(String data1, String data2) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("input1", data1);
        resultIntent.putExtra("input2", data2);
        setResult(InputPressureSugar.RESULT_OK, resultIntent);
        finish();
    }

    private void save(){
        File photoFile = new File(this.getExternalFilesDir(null), "Image" + n + ".jpg");
        try {
            FileOutputStream out = new FileOutputStream(photoFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        n++;
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

                /*
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < items.size(); ++i) {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                            }
                            textView.setText(stringBuilder.toString());
                        }
                    });
                }

                 */
            }
        });
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
                        Mat mat = new Mat(rotatedBitmap.getHeight(),rotatedBitmap.getWidth(),CvType.CV_8UC3);

                        Utils.bitmapToMat(rotatedBitmap, mat);
                        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);
                        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);
                        Imgproc.threshold( mat, mat, 150,255, Imgproc.THRESH_OTSU );
                        Utils.matToBitmap(mat, rotatedBitmap);
                        image = rotatedBitmap;

                        int rect_h = mat.height()/3;
                        int rect_w = mat.width()/2;
                        Rect roi = new Rect(mat.width()/2-(rect_w/2), mat.height()/2-(rect_h/2), rect_w,rect_h);
                        Mat crop = new Mat(mat, roi);

                        int new_rect_h = crop.height()/2;
                        int new_rect_w = crop.width();
                        roi = new Rect(0, 0, new_rect_w, new_rect_h);
                        Mat firstHalf = new Mat(crop, roi);
                        roi = new Rect(0, new_rect_h, new_rect_w, new_rect_h);
                        Mat secondHalf = new Mat(crop, roi);

                        Bitmap bimp = Bitmap.createBitmap(firstHalf.width(), firstHalf.height(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(firstHalf, bimp);
                        image = bimp;

                        bimp = Bitmap.createBitmap(secondHalf.width(), secondHalf.height(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(secondHalf, bimp);
                        image = bimp;

                        StringBuilder sb = new StringBuilder();

                        new_rect_h = firstHalf.height();
                        new_rect_w = firstHalf.width()/3;
                        roi = new Rect(0, 0, new_rect_w, new_rect_h);
                        Mat piece = new Mat(firstHalf, roi);

                        double[] result = matching(Imgproc.TM_CCOEFF_NORMED, piece);
                        double value = Double.MIN_VALUE;
                        int index = 0;
                        for(int i = 0; i < 10; i++){
                            if(result[i] > value) {
                                value = result[i];
                                index = i;
                            }
                        }
                        if(value > .2) sb.append(index);

                        roi = new Rect(new_rect_w, 0, new_rect_w, new_rect_h);
                        piece = new Mat(firstHalf, roi);

                        result = matching(Imgproc.TM_CCOEFF_NORMED, piece);
                        value = Double.MIN_VALUE;
                        index = 0;
                        for(int i = 0; i < 10; i++){
                            if(result[i] > value) {
                                value = result[i];
                                index = i;
                            }
                        }
                        if(value > .2) sb.append(index);

                        roi = new Rect(new_rect_w*2, 0, new_rect_w, new_rect_h);
                        piece = new Mat(firstHalf, roi);

                        result = matching(Imgproc.TM_CCOEFF_NORMED, piece);
                        value = Double.MIN_VALUE;
                        index = 0;
                        for(int i = 0; i < 10; i++){
                            if(result[i] > value) {
                                value = result[i];
                                index = i;
                            }
                        }
                        if(value > .2) sb.append(index);


                        StringBuilder sb2 = new StringBuilder();

                        new_rect_h = secondHalf.height();
                        new_rect_w = secondHalf.width()/3;
                        roi = new Rect(0, 0, new_rect_w, new_rect_h);
                        piece = new Mat(secondHalf, roi);

                        result = matching(Imgproc.TM_CCOEFF_NORMED, piece);
                        value = Double.MIN_VALUE;
                        index = 0;
                        for(int i = 0; i < 10; i++){
                            if(result[i] > value) {
                                value = result[i];
                                index = i;
                            }
                        }
                        if(value > .2) sb2.append(index);

                        roi = new Rect(new_rect_w, 0, new_rect_w, new_rect_h);
                        piece = new Mat(secondHalf, roi);

                        result = matching(Imgproc.TM_CCOEFF_NORMED, piece);
                        value = Double.MIN_VALUE;
                        index = 0;
                        for(int i = 0; i < 10; i++){
                            if(result[i] > value) {
                                value = result[i];
                                index = i;
                            }
                        }
                        if(value > .2) sb2.append(index);

                        roi = new Rect(new_rect_w*2, 0, new_rect_w, new_rect_h);
                        piece = new Mat(secondHalf, roi);

                        bimp = Bitmap.createBitmap(piece.width(), piece.height(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(piece, bimp);
                        image = bimp;

                        result = matching(Imgproc.TM_CCOEFF_NORMED, piece);
                        value = Double.MIN_VALUE;
                        index = 0;
                        for(int i = 0; i < 10; i++){
                            if(result[i] > value) {
                                value = result[i];
                                index = i;
                            }
                        }
                        if(value > .2) sb2.append(index);


                        saveAndReturn(sb.toString(), sb2.toString());

                    } catch( Exception ex) {
                    Log.w("Camera", "Detector dependencies are not yet available");
                    }
                }
            });
        }

    public double[] matching(int match_method, Mat img) {

        double[] x = new double[10];
        double[] per = new double[10];

        int current_num = 0;
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.zero);
        Mat templ = new Mat();
        Utils.bitmapToMat(bitmap, templ);
        Imgproc.cvtColor(templ,templ,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.resize(templ, templ, new Size(img.width(), img.height()), 0, 0);

        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        Imgproc.matchTemplate(img, templ, result, match_method);
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc = mmr.maxLoc;
        x[current_num] = matchLoc.x;
        per[current_num] = mmr.maxVal;

        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0), 8);

        current_num = 1;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.one);
        templ = new Mat();
        Utils.bitmapToMat(bitmap, templ);
        Imgproc.cvtColor(templ,templ,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.resize(templ, templ, new Size(img.width(), img.height()), 0, 0);

        Imgproc.matchTemplate(img, templ, result, match_method);
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        mmr = Core.minMaxLoc(result);
        matchLoc = mmr.maxLoc;
        x[current_num] = matchLoc.x;
        per[current_num] = mmr.maxVal;

        // / Show me what you got
        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0), 8);

        current_num = 2;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.two);
        templ = new Mat();
        Utils.bitmapToMat(bitmap, templ);
        Imgproc.cvtColor(templ,templ,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.resize(templ, templ, new Size(img.width(), img.height()), 0, 0);

        Imgproc.matchTemplate(img, templ, result, match_method);
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        mmr = Core.minMaxLoc(result);
        matchLoc = mmr.maxLoc;
        x[current_num] = matchLoc.x;
        per[current_num] = mmr.maxVal;

        // / Show me what you got
        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0), 8);

        current_num = 3;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.three);
        templ = new Mat();
        Utils.bitmapToMat(bitmap, templ);
        Imgproc.cvtColor(templ,templ,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.resize(templ, templ, new Size(img.width(), img.height()), 0, 0);

        Imgproc.matchTemplate(img, templ, result, match_method);
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        mmr = Core.minMaxLoc(result);
        matchLoc = mmr.maxLoc;
        x[current_num] = matchLoc.x;
        per[current_num] = mmr.maxVal;

        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0), 8);

        current_num = 4;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.four);
        templ = new Mat();
        Utils.bitmapToMat(bitmap, templ);
        Imgproc.cvtColor(templ,templ,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.resize(templ, templ, new Size(img.width(), img.height()), 0, 0);

        Imgproc.matchTemplate(img, templ, result, match_method);
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        mmr = Core.minMaxLoc(result);
        matchLoc = mmr.maxLoc;
        x[current_num] = matchLoc.x;
        per[current_num] = mmr.maxVal;

        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0), 8);

        current_num = 5;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.five);
        templ = new Mat();
        Utils.bitmapToMat(bitmap, templ);
        Imgproc.cvtColor(templ,templ,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.resize(templ, templ, new Size(img.width(), img.height()), 0, 0);

        Imgproc.matchTemplate(img, templ, result, match_method);
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        mmr = Core.minMaxLoc(result);
        matchLoc = mmr.maxLoc;
        x[current_num] = matchLoc.x;
        per[current_num] = mmr.maxVal;

        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0), 8);

        current_num = 6;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.six);
        templ = new Mat();
        Utils.bitmapToMat(bitmap, templ);
        Imgproc.cvtColor(templ,templ,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.resize(templ, templ, new Size(img.width(), img.height()), 0, 0);

        Imgproc.matchTemplate(img, templ, result, match_method);
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        mmr = Core.minMaxLoc(result);
        matchLoc = mmr.maxLoc;
        x[current_num] = matchLoc.x;
        per[current_num] = mmr.maxVal;

        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0), 8);

        current_num = 7;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.seven);
        templ = new Mat();
        Utils.bitmapToMat(bitmap, templ);
        Imgproc.cvtColor(templ,templ,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.resize(templ, templ, new Size(img.width(), img.height()), 0, 0);

        Imgproc.matchTemplate(img, templ, result, match_method);
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        mmr = Core.minMaxLoc(result);
        matchLoc = mmr.maxLoc;
        x[current_num] = matchLoc.x;
        per[current_num] = mmr.maxVal;

        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0), 8);

        current_num = 8;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.eight);
        templ = new Mat();
        Utils.bitmapToMat(bitmap, templ);
        Imgproc.cvtColor(templ,templ,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.resize(templ, templ, new Size(img.width(), img.height()), 0, 0);

        Imgproc.matchTemplate(img, templ, result, match_method);
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        mmr = Core.minMaxLoc(result);
        matchLoc = mmr.maxLoc;
        x[current_num] = matchLoc.x;
        per[current_num] = mmr.maxVal;

        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0), 8);

        current_num = 9;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.nine);
        templ = new Mat();
        Utils.bitmapToMat(bitmap, templ);
        Imgproc.cvtColor(templ,templ,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.resize(templ, templ, new Size(img.width(), img.height()), 0, 0);

        Imgproc.matchTemplate(img, templ, result, match_method);
        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        mmr = Core.minMaxLoc(result);
        matchLoc = mmr.maxLoc;
        x[current_num] = matchLoc.x;
        per[current_num] = mmr.maxVal;

        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0), 8);


        Bitmap bimp = Bitmap.createBitmap(img.width(), img.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img, bimp);
        return per;
    }
    }