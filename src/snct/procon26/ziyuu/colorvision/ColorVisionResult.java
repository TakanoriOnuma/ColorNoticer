package snct.procon26.ziyuu.colorvision;

public class ColorVisionResult {
    // 色弱のタイプ
    public static final int TYPE_NONE       = 0;
    public static final int TYPE_RED_WEAK   = 1;
    public static final int TYPE_GREEN_WEAK = 2;
    public static final int TYPE_BLUE_WEAK  = 3;

    // 度合い
    public static final int LEVEL_NONE   = 10;
    public static final int LEVEL_MILD   = 11;
    public static final int LEVEL_SEVERE = 12;


    private int mType  = TYPE_NONE;
    private int mLevel = LEVEL_NONE;

    public ColorVisionResult() {
    }
    public ColorVisionResult(int type, int level) {
        mType  = type;
        mLevel = level;
    }

    // セッター
    public void setType(int type) {
        mType = type;
    }
    public void setLevel(int level) {
        mLevel = level;
    }

    // ゲッター
    public int getType() {
        return mType;
    }
    public int getLevel() {
        return mLevel;
    }
    public String getTypeName() {
        switch(mType) {
        case TYPE_NONE:
            return "一般色覚";
        case TYPE_RED_WEAK:
            return "赤色弱";
        case TYPE_GREEN_WEAK:
            return "緑色弱";
        case TYPE_BLUE_WEAK:
            return "青色弱";
        }
        return "？";
    }
    public String getLevelName() {
        switch(mLevel) {
        case LEVEL_NONE:
            return "";
        case LEVEL_MILD:
            return "軽度";
        case LEVEL_SEVERE:
            return "重度";
        }
        return "？";
    }


    @Override
    public String toString() {
        return getTypeName() + " " + getLevelName();
    }
}
