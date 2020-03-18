package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ProgressBar;

import android.os.AsyncTask;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class CheckSugarImage extends AppCompatActivity {

    ProgressBar spinner;
    Bitmap bmp;
    String foundValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sugar_image);

        bmp = getImage("/Image");
        CheckSugarImage.AsyncTaskExample asyncTask=new CheckSugarImage.AsyncTaskExample();
        asyncTask.execute("");
    }

    private Bitmap getImage(String name){
        String photoPath = this.getFilesDir() + name + ".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeFile(photoPath, options);
        return bmp;
    }

    private class AsyncTaskExample extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinner = findViewById(R.id.progressBar);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            Mat mat = new Mat(rotatedBitmap.getHeight(), rotatedBitmap.getWidth(), CvType.CV_8UC3);

            Utils.bitmapToMat(rotatedBitmap, mat);

            double rect_h = mat.height() * 0.2;
            double rect_w = mat.width() * 0.6;
            int w = (int) Math.round(mat.width() / 2 - (rect_w / 2));
            int h = (int) Math.round(mat.height() / 2 - (rect_h / 2));
            Rect roi = new Rect(w, h, (int) rect_w, (int) rect_h);
            Mat crop = new Mat(mat, roi);

            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            if (!textRecognizer.isOperational()) {
                Log.w("Camera", "Detector dependencies are not yet available");
            } else {
                Bitmap image = Bitmap.createBitmap(crop.width(), crop.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(crop, image);

                Frame frame = new Frame.Builder().setBitmap(image).build();

                SparseArray<TextBlock> items = textRecognizer.detect(frame);

                StringBuilder sb = new StringBuilder();

                if (items.size() != 0) {
                    TextBlock myItems = items.valueAt(0);
                    sb.append(myItems.getValue());
                }
                foundValue = sb.toString();
            }
            return scaledBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //spinner.setVisibility(View.GONE);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("result", foundValue);
            setResult(Camera.RESULT_OK, resultIntent);
            finish();
        }
    }
}
