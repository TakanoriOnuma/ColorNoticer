package jp.co.sendai.national.college.of.technology.colortransfar;

public class ColorTransfar {
    private ColorFilter mColorFilter = null;

    private int mFrameNum       = 0;
    private int mPeriodFrameNum = 10;

    public ColorTransfar() {
    }
    public ColorTransfar(ColorFilter colorFilter) {
        mColorFilter = colorFilter;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        mColorFilter = colorFilter;
    }
    // NV21からRGBに変換する関数
    // 参考URL: http://www.41post.com/3470/programming/android-retrieving-the-camera-preview-as-a-pixel-array
    public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        // フレーム数のカウントアップ
        mFrameNum = (mFrameNum + 1) % mPeriodFrameNum;

        int frameSize = width * height;
        for(int j = 0, yp = 0; j < height; j++) {
            int u = 0;
            int v = 0;
            int uvp = frameSize + (j >> 1) * width;

            for(int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int)yuv420sp[yp])) - 16;
                if(y < 0) {
                    y = 0;
                }
                if((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = y1192 + 1634 * v;
                int g = y1192 - 833 * v - 400 * u;
                int b = y1192 + 2066 * u;

                if(mColorFilter != null) {
                    // マスクする必要があるなら、黒にして次へ
                    if(mColorFilter.isMask(r, g, b, mFrameNum, mPeriodFrameNum)) {
                        rgb[yp] = 0xff000000;
                        continue;
                    }
                }

                // 262143 = 2^18 - 1 = 16^4 * 2^2 - 1
                r = (r < 0) ? 0 : (r < 262143) ? r : 262143;
                g = (g < 0) ? 0 : (g < 262143) ? g : 262143;
                b = (b < 0) ? 0 : (b < 262143) ? b : 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }
}
