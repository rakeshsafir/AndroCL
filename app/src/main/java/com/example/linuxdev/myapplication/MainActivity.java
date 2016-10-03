package com.example.linuxdev.myapplication;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "AndroCLActivity";

    private void copyFile(final String f) {
        InputStream in;
        try {
            in = getAssets().open(f);
            final File of = new File(getDir("execdir", MODE_PRIVATE), f);

            final OutputStream out = new FileOutputStream(of);

            final byte b[] = new byte[65535];
            int sz = 0;
            while ((sz = in.read(b)) > 0) {
                out.write(b, 0, sz);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Used to load the 'native-lib' library on application startup.
    static boolean sfoundLibrary = true;
    static {
        try {
            System.loadLibrary("androcl");
        }
        catch (UnsatisfiedLinkError e) {
            sfoundLibrary = false;
        }
    }

    public static native int runOpenCL(Bitmap bmpIn, Bitmap bmpOut, int info[]);
    public static native int runNativeC(Bitmap bmpIn, Bitmap bmpOut, int info[]);

    final int info[] = new int[3]; // Width, Height, Execution time (ms)
    LinearLayout layout;
    Bitmap bmpOrig, bmpOpenCL, bmpNativeC;
    ImageView imageView;
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageHere);
        textView = (TextView) findViewById(R.id.resultText);

        copyFile("bilateralKernel.cl"); //copy cl kernel file from assets to /data/data/...assets

        bmpOrig = BitmapFactory.decodeResource(this.getResources(), R.drawable.brusigablommor);
        info[0] = bmpOrig.getWidth();
        info[1] = bmpOrig.getHeight();

        bmpOpenCL = Bitmap.createBitmap(info[0], info[1], Config.ARGB_8888);
        bmpNativeC = Bitmap.createBitmap(info[0], info[1], Config.ARGB_8888);
        textView.setText("Original");
        imageView.setImageBitmap(bmpOrig);
    }

    public void showOriginalImage(View v) {
        textView.setText("Original");
        imageView.setImageBitmap(bmpOrig);
    }

    public void showOpenCLImage(View v) {
        runOpenCL(bmpOrig, bmpOpenCL, info);
        textView.setText("Bilateral Filter, OpenCL, Processing time is " + info[2] + " ms");
        imageView.setImageBitmap(bmpOpenCL);
    }

    public void showNativeCImage(View v) {
        runNativeC(bmpOrig, bmpNativeC, info);
        textView.setText("Bilateral Filter, NativeC, Processing time is " + info[2] + " ms");
        imageView.setImageBitmap(bmpNativeC);
    }
}
