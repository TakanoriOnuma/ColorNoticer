package snct.procon26.ziyuu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.RadioGroup;

public class ColorVisionTestActivity extends Activity {
    private static final String TAG = "ColorVisionTestActivity";

    private RadioGroup mSelectNumGroup;

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorvision_test);

        mSelectNumGroup = (RadioGroup)findViewById(R.id.selectNumGroup);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
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

}
