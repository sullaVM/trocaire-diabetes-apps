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

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

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
    String data;
    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    Bitmap bitmap;
    ImageView imageView;
    View view;

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
        textView = findViewById(R.id.text_view);

        view = findViewById(R.id.myRectangleView);

        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = getIntent();
                //String type = intent.getStringExtra("type");
                //saveData(type);
                takeImage();
            }
        });

        /*
        String photoPath = this.getFilesDir() + "/Image.jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = BitmapFactory.decodeFile(photoPath, options);

        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);

         */

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        /*
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("Camera", "Detector dependencies are not yet available");
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(frame);

            StringBuilder sb = new StringBuilder();

            if (items.size() != 1)
                for (int i = 0; i < items.size() - 1; i++) {
                    TextBlock myItems = items.valueAt(i);
                    sb.append(myItems.getValue());
                    sb.append("\n");
                }
            TextBlock myItems = items.valueAt(items.size() - 1);
            sb.append(myItems.getValue());
            data = sb.toString();
            textView.setText(data);
        }

         */

    }

    private void saveData(String type) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(type, data);
        setResult(DataEnter.RESULT_OK, resultIntent);
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
            }
        });
    }
    private void takeImage() {
            cameraSource.takePicture(null, new CameraSource.PictureCallback() {
                //private File imageFile;

                @Override
                public void onPictureTaken(byte[] bytes) {
                    try {
                        // convert byte array into bitmap
                        //Bitmap loadedImage = null;
                        //Bitmap rotatedBitmap = null;
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

                        Size kernelSize = new Size(11, 11);
                        Point anchor = new Point(-1, -1);
                        int iterations = 2;

                        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, kernelSize);
                        Imgproc.erode(firstHalf, firstHalf, kernel, anchor, iterations);
                        Imgproc.erode(firstHalf, firstHalf, kernel, anchor, iterations);
                        Imgproc.erode(firstHalf, firstHalf, kernel, anchor, iterations);

                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                        if (!textRecognizer.isOperational()) {
                            Log.w("Camera", "Detector dependencies are not yet available");
                        } else {
                            //Mat newMat = new Mat(new Size(1000,1000), CvType.CV_8UC3, new Scalar(255,255,255));
                            //newMat.copyTo(firstHalf);
                            Bitmap halfOne = Bitmap.createBitmap(new_rect_w, new_rect_h, Bitmap.Config.ARGB_8888);
                            Imgproc.cvtColor(firstHalf,firstHalf,Imgproc.COLOR_GRAY2RGB);
                            Utils.matToBitmap(firstHalf, halfOne);

                            Frame frame = new Frame.Builder().setBitmap(halfOne).build();

                            SparseArray<TextBlock> items = textRecognizer.detect(frame);

                            StringBuilder sb = new StringBuilder();

                            if(items.size()!=0) {
                                TextBlock myItems = items.valueAt(0);
                                sb.append(myItems.getValue());
                                data = sb.toString();
                                textView.setText(data);
                            }
                            textView.setText("");
                        }

                        Bitmap bitmap = Bitmap.createBitmap(rect_w,rect_h, Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(crop, bitmap);

                        Bitmap halfTwo = Bitmap.createBitmap(new_rect_w, new_rect_h, Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(secondHalf, halfTwo);

                        int thing = 0;
                    } catch( Exception ex) {
                    Log.w("Camera", "Detector dependencies are not yet available");
                    }
                }
            });
        }
    }