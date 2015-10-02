package snct.procon26.ziyuu;

import java.io.IOException;
import java.util.List;

import snct.procon26.ziyuu.colortransfar.ColorFilter;
import snct.procon26.ziyuu.colortransfar.ColorTransfar;
import snct.procon26.ziyuu.colortransfar.ColorValueTransfar;
import snct.procon26.ziyuu.imageviewer.ColorInfoDrawer;
import snct.procon26.ziyuu.imageviewer.ImageViewer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
            SurfaceHolder.Callback, Camera.PreviewCallback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private boolean mIsFirstLaunch = true;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private ColorTransfar mColorTransfar = new ColorTransfar();

    private SurfaceView mSvFacePreview;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera = null;
    private Size mPreviewSize;
    private List<Size> mSupportedPreviewSizes;

    private byte[] mFrameBuffer;
    private int[]  mImageData;
    private Bitmap mBitmap;
    private ImageViewer mOverLay;
    private ColorInfoDrawer mColorInfoDrawer = null;

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // SurfaceViewでカメラが利用できるように設定
        mSvFacePreview = (SurfaceView)findViewById(R.id.FacePreview);
        mSurfaceHolder = mSvFacePreview.getHolder();
        mSurfaceHolder.addCallback(this);

        mOverLay = (ImageViewer)findViewById(R.id.OverLayView);

        // リファレンスマネージャの取得
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        // 起動時から選択されるので初回のみはアクティビティ遷移を回避する
        if(mIsFirstLaunch) {
            mIsFirstLaunch = false;
            return;
        }

        Intent intent;
        switch (number) {
        case 1:
            mTitle = getString(R.string.title_section1);
            surfaceDestroyed(mSurfaceHolder);

            intent = new Intent(MainActivity.this, ConfigColorFilterActivity.class);
            startActivity(intent);
            break;
        case 2:
            mTitle = getString(R.string.title_section2);
            surfaceDestroyed(mSurfaceHolder);

            intent = new Intent(MainActivity.this, ConfigColorFilterActivity.class);
            startActivity(intent);
            break;
        case 3:
            mTitle = getString(R.string.title_section3);
            surfaceDestroyed(mSurfaceHolder);

            intent = new Intent(MainActivity.this, ConfigColorInfoActivity.class);
            startActivity(intent);
            break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // セッティングの未使用
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
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
            // byte[]をint[]に変換（明度のみ）
            int[] frame = mImageData;

            mColorTransfar.decodeYUV420SP(frame, data, mPreviewSize.width, mPreviewSize.height);

            if(mColorInfoDrawer != null) {
                // 色情報の取得
                Point pos = mOverLay.getCursorPoint();
                Rect  viewRect = mOverLay.getViewRect();
                int color = ColorTransfar.getColor(data, mPreviewSize.width, mPreviewSize.height,
                        mPreviewSize.width * pos.x / viewRect.right, mPreviewSize.height * pos.y / viewRect.bottom);
                mColorInfoDrawer.setColorInfo(color);
            }

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

                // 各種設定の読み込み
                if(mPref.getBoolean("isColorValueTransfarFunction", false)) {
                    ColorValueTransfar colorValueTransfar = new ColorValueTransfar();
                    colorValueTransfar.setRedRate(mPref.getInt("redColor", 50));
                    colorValueTransfar.setGreenRate(mPref.getInt("greenColor", 50));
                    colorValueTransfar.setBlueRate(mPref.getInt("blueColor", 50));
                    mColorTransfar.setColorValueTransfar(colorValueTransfar);
                }
                else {
                    mColorTransfar.setColorValueTransfar(null);
                }
                if(mPref.getBoolean("isFlashingFunction", false)) {
                    ColorFilter colorFilter = new ColorFilter();
                    colorFilter.setHueStart(mPref.getInt("hueStart", 0));
                    colorFilter.setHueEnd(mPref.getInt("hueEnd", 0));
                    colorFilter.setSaturation(mPref.getInt("saturation", 50));
                    mColorTransfar.setColorFilter(colorFilter);
                }
                else {
                    mColorTransfar.setColorFilter(null);

                }
                if(mPref.getBoolean("isColorInfoFunction", false)) {
                    mColorInfoDrawer = new ColorInfoDrawer();
                }
                else {
                    mColorInfoDrawer = null;
                }
                mOverLay.setColorInfoDrawer(mColorInfoDrawer);

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
}
