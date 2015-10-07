package snct.procon26.ziyuu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

public class ColorVisionTestActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ColorVisionTestActivity";

    private int mQuestionNumber = 0;
    private int[] mQuestionImageIds = new int[3];
    private ArrayList<Integer>[] mQuestionAnswers = new ArrayList[3];
    private int[] mResults = new int[3];     // 回答数のずれを記録する

    private CheckBox[] mCheckBoxes = new CheckBox[10];
    private ImageView mImageView;

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorvision_test);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        // チェックボックスの読み込み
        mCheckBoxes[0] = (CheckBox)findViewById(R.id.checkBox0);
        mCheckBoxes[1] = (CheckBox)findViewById(R.id.checkBox1);
        mCheckBoxes[2] = (CheckBox)findViewById(R.id.checkBox2);
        mCheckBoxes[3] = (CheckBox)findViewById(R.id.checkBox3);
        mCheckBoxes[4] = (CheckBox)findViewById(R.id.checkBox4);
        mCheckBoxes[5] = (CheckBox)findViewById(R.id.checkBox5);
        mCheckBoxes[6] = (CheckBox)findViewById(R.id.checkBox6);
        mCheckBoxes[7] = (CheckBox)findViewById(R.id.checkBox7);
        mCheckBoxes[8] = (CheckBox)findViewById(R.id.checkBox8);
        mCheckBoxes[9] = (CheckBox)findViewById(R.id.checkBox9);

        // 問題と回答の読み込み
        mQuestionImageIds[0] = R.drawable.redvision_test;
        mQuestionAnswers[0]  = new ArrayList<Integer>();

        mQuestionImageIds[1] = R.drawable.greenvision_test;
        mQuestionAnswers[1]  = new ArrayList<Integer>();
        mQuestionAnswers[1].add(4);
        mQuestionAnswers[1].add(0);

        mQuestionImageIds[2] = R.drawable.bluevision_test;
        mQuestionAnswers[2]  = new ArrayList<Integer>();

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
        // 回答をチェックする
        ArrayList<Integer> answer = (ArrayList<Integer>) mQuestionAnswers[mQuestionNumber].clone();
        for(CheckBox checkBox : mCheckBoxes) {
            if(checkBox.isChecked()) {
                int number = Integer.parseInt((String)checkBox.getText());
                int idx = answer.indexOf(number);
                // 正解なら
                if(idx != -1) {
                    answer.remove(idx);
                }
                // 不正解なら
                else {
                    mResults[mQuestionNumber] += 1;
                }
            }
        }
        // 回答できなかったものもポイントに入れる
        mResults[mQuestionNumber] += answer.size();

        // チェックボックスのクリア
        for(CheckBox checkBox : mCheckBoxes) {
            checkBox.setChecked(false);
        }

        mQuestionNumber += 1;
        // 3問目を超えたら色覚検査を終了する
        if(mQuestionNumber >= 3) {
            Log.d("debug", mResults[0] + ", " + mResults[1] + ", " + mResults[2]);
            finish();
            return;
        }

        mImageView.setImageResource(mQuestionImageIds[mQuestionNumber]);
    }

}
