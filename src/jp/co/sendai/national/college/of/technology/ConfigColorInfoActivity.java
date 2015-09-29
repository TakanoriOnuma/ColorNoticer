package jp.co.sendai.national.college.of.technology;

import java.io.IOException;
import java.util.List;

import jp.co.sendai.national.college.of.technology.colortransfar.ColorFilter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class ConfigColorInfoActivity extends Activity
        implements SurfaceHolder.Callback, Camera.PreviewCallback, View.OnClickListener {
    private static final String TAG = "ConfigColorInfoActivity";

    private SurfaceView mSvFacePreview;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera = null;
    private Size mPreviewSize;
    private List<Size> mSupportedPreviewSizes;

    private byte[] mFrameBuffer;
    private int[]  mImageData;
    private Bitmap mBitmap;
    private OverLayView mOverLay;

    private SeekBar mSaturationBar;
    private SeekBar mHueStartBar;
    private SeekBar mHueEndBar;

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_colorinfo);

        // SurfaceViewでカメラが利用できるように設定
        mSvFacePreview = (SurfaceView)findViewById(R.id.FacePreview);
        mSurfaceHolder = mSvFacePreview.getHolder();
        mSurfaceHolder.addCallback(this);

        mSaturationBar = (SeekBar)findViewById(R.id.SaturationBar);
        mHueStartBar   = (SeekBar)findViewById(R.id.HueStartBar);
        mHueEndBar     = (SeekBar)findViewById(R.id.HueEndBar);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        int saturation = mPref.getInt("saturation", 0);
        int hueStart   = mPref.getInt("hueStart", 0);
        int hueEnd     = mPref.getInt("hueEnd", 0);

        mSaturationBar.setProgress(saturation);
        mHueStartBar.setProgress(hueStart);
        mHueEndBar.setProgress(hueEnd);

        Button saveButton = (Button)findViewById(R.id.SaveButton);
        saveButton.setOnClickListener(this);

        mOverLay = (OverLayView)findViewById(R.id.OverLayView);
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(mCamera != null) {
            // byte[]をint[]に変換
            int[] frame = mImageData;

            TextView colorProperties = (TextView)findViewById(R.id.ColorProperties);
            int saturation = mSaturationBar.getProgress() & 0xff;
            int hueStart   = mHueStartBar.getProgress() - 60;
            int hueEnd     = mHueEndBar.getProgress()   - 60;
            colorProperties.setText(String.format("%d, %d, %d", saturation, hueStart, hueEnd));

            // 透明フィルタ
            ColorFilter.colorFilter(frame, 0, 0, 0, 0);
            // マスク処理
            ColorFilter.mask(frame, data, mPreviewSize.width, mPreviewSize.height, saturation, hueStart, hueEnd);

            // Bitmapに描画して、OverLayに再描画を促す
            mBitmap.setPixels(frame, 0, mPreviewSize.width,
                    0, 0, mPreviewSize.width, mPreviewSize.height);
            mOverLay.invalidate();

            // また映像を要求する
            mCamera.addCallbackBuffer(mFrameBuffer);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // カメラの初期化処理
        mCamera = Camera.open();
        if(mCamera != null) {
            try {
                // これがないとはじまらない
                mCamera.setPreviewDisplay(mSurfaceHolder);
            }
            catch(IOException e) {
                e.printStackTrace();
            }

            // 利用可能なプレビューサイズの取得
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mCamera != null) {
            Parameters params = mCamera.getParameters();

            // フォーマットの指定
            params.setPreviewFormat(ImageFormat.NV21);

            if(mSupportedPreviewSizes != null) {
                mCamera.stopPreview();

               // 端末ディスプレイのサイズに最適なプレビューサイズを選択する
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);

                // カメラのプレビューサイズをセット
                params.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

                // パラメータをセット
                mCamera.setParameters(params);

                // バッファの用意
                int size = mPreviewSize.width * mPreviewSize.height *
                        ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
                mFrameBuffer = new byte[size];
                mImageData = new int[mPreviewSize.width * mPreviewSize.height];

                // 透明な画像をセットしておく
                mBitmap = Bitmap.createBitmap(mPreviewSize.width, mPreviewSize.height, Config.ARGB_8888);
                mBitmap.setPixels(mImageData, 0, mPreviewSize.width,
                        0, 0, mPreviewSize.width, mPreviewSize.height);
                mOverLay.setBitmap(mBitmap);

                // フレームバッファを追加
                mCamera.setPreviewCallbackWithBuffer(this);
                mCamera.addCallbackBuffer(mFrameBuffer);

                // プレビュー開始
                mCamera.startPreview();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("MainActivity", "surfaceDestroyed");
        if(mCamera != null) {
            // カメラの終了処理
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // BACKキーが入力されたらサブアクティビティを終了する
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            surfaceDestroyed(mSurfaceHolder);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        // 設定を保存して終了する
        int saturation = mSaturationBar.getProgress();
        int hueStart  = mHueStartBar.getProgress();
        int hueEnd = mHueEndBar.getProgress();

        Editor editor = mPref.edit();

        editor.putInt("saturation", saturation);
        editor.putInt("hueStart", hueStart);
        editor.putInt("hueEnd", hueEnd);
        editor.commit();

        surfaceDestroyed(mSurfaceHolder);
        finish();
    }
}
