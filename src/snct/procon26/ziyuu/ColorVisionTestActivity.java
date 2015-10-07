package snct.procon26.ziyuu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ColorVisionTestActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ColorVisionTestActivity";

    private int mQuestionNumber = 0;
    private int[] mQuestionImageIds = new int[3];

    private ImageView mImageView;

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorvision_test);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        // 問題画像の読み込み
        mQuestionImageIds[0] = R.drawable.redvision_test;
        mQuestionImageIds[1] = R.drawable.greenvision_test;
        mQuestionImageIds[2] = R.drawable.bluevision_test;

        // 問題のセット
        mImageView = (ImageView)findViewById(R.id.imageView);
        mImageView.setImageResource(mQuestionImageIds[mQuestionNumber]);

        Button button = (Button)findViewById(R.id.nextButton);
        button.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // BACKキーが入力されたらサブアクティビティを終了する
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        mQuestionNumber += 1;
        // 3問目を超えたら色覚検査を終了する
        if(mQuestionNumber >= 3) {
            finish();
            return;
        }

        mImageView.setImageResource(mQuestionImageIds[mQuestionNumber]);
    }

}
