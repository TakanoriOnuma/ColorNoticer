package snct.procon26.ziyuu.colortransfar;

public class ColorValueTransfar implements IColorValueTransfar {
    private int mRedRate   = 100;
    private int mGreenRate = 100;
    private int mBlueRate  = 100;

    // セッター
    public void setRedRate(int redRate) {
        mRedRate = redRate;
    }
    public void setGreenRate(int greenRate) {
        mGreenRate = greenRate;
    }
    public void setBlueRate(int blueRate) {
        mBlueRate = blueRate;
    }

    @Override
    public void colorTranslate(int[] rgb) {
        rgb[0] = rgb[0] * mRedRate   / 100;
        rgb[1] = rgb[1] * mGreenRate / 100;
        rgb[2] = rgb[2] * mBlueRate  / 100;
    }

}
