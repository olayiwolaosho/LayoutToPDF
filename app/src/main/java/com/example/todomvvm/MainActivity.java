package com.example.todomvvm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    Bitmap bitmap;
    // declaring width and height
    // for our PDF file.
    int pageHeight = 1550;
    int pageWidth = 1090;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LayoutInflater inflater = LayoutInflater.from(this);
        constraintLayout = (ConstraintLayout) findViewById(R.id.layout_print);
                //(ConstraintLayout) inflater.inflate(R.layout.activity_main, null);
        findViewById(R.id.layout_print);

    }

    public void goToDashboard(View view) {

    }

    public void printReceipt(View view) {
        view.setVisibility(View.GONE);
        constraintLayout.setDrawingCacheEnabled(true);

        DisplayMetrics displayMetrics = new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.getDisplay().getRealMetrics(displayMetrics);
            Integer displayMet =  displayMetrics.densityDpi;
        }
        else{
            WindowManager windowManager =  (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        view.measure(
                View.MeasureSpec.makeMeasureSpec(
                        displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(
                        displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
                )
        );


        //Create a bitmap with the measured width and height. Attach the bitmap to a canvas object and draw the view inside the canvas
        constraintLayout.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        bitmap = Bitmap.createBitmap(constraintLayout.getWidth(), constraintLayout.getHeight(), Bitmap.Config.ARGB_8888);
        //bitmap = constraintLayout.getDrawingCache();
        Canvas canvas = new Canvas(bitmap);
        constraintLayout.draw(canvas);

        //Since the bitmap has been created based on the screen dimensions, a scaled-down version is necessary while generating A4 sized PDF.
        Bitmap.createScaledBitmap(bitmap, pageWidth, pageHeight, true);

        PdfDocument document = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

        PdfDocument.Page page = document.startPage(pageInfo);



        page.getCanvas().drawBitmap(bitmap, 0F, 0F, null);

        document.finishPage(page);

        // below line is used to set the name of
        // our PDF file and its path.
        //File file = new File(Environment.getExternalStorageDirectory(), "dotpayReceipt.pdf");

        File picturesDirectory = new File(this.getExternalFilesDir(null), "dotpayReceipt.pdf");
              //  getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        try {
            // after creating a file name we will
            // write our PDF file to that location.
            //FileOutputStream fos = new FileOutputStream(new File(picturesDirectory,"dotpayReceipt.pdf"));
            document.writeTo(new FileOutputStream(picturesDirectory));

            document.close();
           // fos.flush();
           // fos.close();

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(MainActivity.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // below line is used
            // to handle error
            e.printStackTrace();
            view.setVisibility(View.VISIBLE);
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
       // renderPdf(this,picturesDirectory);
        view.setVisibility(View.VISIBLE);
    }

    private void renderPdf(Context context , File filePath) {
        try {
        Uri uri = FileProvider.getUriForFile(
                context,
                context.getApplicationContext().getPackageName() + ".provider",
                filePath
        );
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/pdf");


            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}