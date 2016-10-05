package com.example.linuxdev.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "AndroCLActivity";
    private final Context mContext = MainActivity.this;
    private final String mCpuABI = System.getProperty("os.arch");
    enum AclStatus {
        ACL_STATUS_SUCCESS,
        ACL_STATUS_OCL_SO_LOAD_FAILED,
        ACL_STATUS_ACL_SO_LOAD_FAILED,
        ACL_STATUS_ACL_CL_LOAD_FAILED,
    }
    private AclStatus mStatus;

    private void showMessage(final AclStatus st) {
        Toast.makeText(mContext, st.toString(), Toast.LENGTH_SHORT).show();
    }

    /* Loads OpenCL kernel into GPU */
    private boolean copyFile(final String f) {
        boolean ret = true;
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
            ret = false;
        }
        return ret;
    }

    private boolean loadNativeLib(final String lib) {
        boolean ret = true;
        try {
            System.load(lib);
            Log.d( TAG, "Loaded file =" + lib);
        } catch (UnsatisfiedLinkError err) {
            ret = false;
            Log.e( TAG, "Failed to load file =" + lib);
        }
        return ret;
    }

    private boolean loadNativeOpenCL() {
        return (loadNativeLib("/system/vendor/lib/libOpenCL.so") ||
                loadNativeLib("/system/vendor/lib/libGLES_mali.so") ||
                loadNativeLib("/system/vendor/lib/libPVROCL.so"));
    }

    /*
        private void searchFile(final File path, final File fName) {
        Log.d( TAG, "Searching file =" + path.getAbsolutePath());
        if(path.isFile()) {
            if(path.getName().equalsIgnoreCase(fName.getName())) {
                Log.d( TAG, "Found file=" + path.getAbsolutePath());
            }
        } else if (path.isDirectory()) {
            File[] fList = path.listFiles();
            for (int i = 0; i < fList.length; i++) {
                Log.d( TAG, "Recursing into" + path.getAbsolutePath());
                searchAndLoadFile(fList[i], fName);
            }
        }
    }
    */

    static boolean bCanProcess = true;
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

        do {
            /* Load vendor libOpenCL.so */
            if (true != loadNativeOpenCL()) {
                mStatus = AclStatus.ACL_STATUS_OCL_SO_LOAD_FAILED;
                bCanProcess = false;
                break;
            }
            /* Load App Jni lib */
            String appLib = mContext.getApplicationInfo().nativeLibraryDir;
            appLib += "/libandrocl.so";
            if (true != loadNativeLib(appLib)) {
                mStatus = AclStatus.ACL_STATUS_ACL_SO_LOAD_FAILED;
                bCanProcess = false;
                break;
            }
            if (true != copyFile("bilateralKernel.cl")) { //copy cl kernel file from assets to /data/data/...assets
                mStatus = AclStatus.ACL_STATUS_ACL_CL_LOAD_FAILED;
                bCanProcess = false;
                break;
            }
            bCanProcess = true;
        }while(false);

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
        if(false == bCanProcess) {
            showMessage(mStatus);
        } else {
            Toast.makeText(mContext, "runOpenCL", Toast.LENGTH_SHORT).show();
            runOpenCL(bmpOrig, bmpOpenCL, info);
            textView.setText("Bilateral Filter, OpenCL, Processing time is " + info[2] + " ms");
            imageView.setImageBitmap(bmpOpenCL);
        }
    }

    public void showNativeCImage(View v) {
        if(false == bCanProcess) {
            showMessage(mStatus);
        } else {
            Toast.makeText(mContext, "runNativeC", Toast.LENGTH_SHORT).show();
            runNativeC(bmpOrig, bmpNativeC, info);
            textView.setText("Bilateral Filter, NativeC, Processing time is " + info[2] + " ms");
            imageView.setImageBitmap(bmpNativeC);
        }
    }
}
