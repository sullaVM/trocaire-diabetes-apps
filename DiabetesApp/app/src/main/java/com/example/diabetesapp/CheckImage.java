package com.example.diabetesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.view.View;

import android.graphics.Matrix;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.android.Utils;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Rect;

import android.os.AsyncTask;

public class CheckImage extends AppCompatActivity {

    ProgressBar spinner;
    Bitmap bmp;
    String foundValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_image);

        bmp = getImage("/Image");
        AsyncTaskExample asyncTask=new AsyncTaskExample();
        asyncTask.execute("");
    }

    private Bitmap getImage(String name){
        String photoPath = this.getFilesDir() + name + ".jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeFile(photoPath, options);
        return bmp;
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
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB);
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
            Imgproc.threshold(mat, mat, 10, 255, Imgproc.THRESH_OTSU);
            Utils.matToBitmap(mat, rotatedBitmap);

            int rect_h = (int)(mat.height() * 0.25);
            int rect_w = (int)(mat.width() * 0.8);
            Rect roi = new Rect(mat.width() / 2 - (rect_w / 2), mat.height() / 2 - (rect_h / 2), rect_w, rect_h);
            Mat crop = new Mat(mat, roi);
            Bitmap bmp = Bitmap.createBitmap(crop.width(), crop.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(crop, bmp);

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
            foundValue = sb.toString();
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
