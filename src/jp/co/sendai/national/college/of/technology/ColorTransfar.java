package jp.co.sendai.national.college.of.technology;

public class ColorTransfar {
    /**
     * Converts YUV420 NV21 to RGB8888
     *
     * @param rgb a RGB8888 pixels int array. Where each int is a pixels ARGB.
     * @param data byte array on YUV420 NV21 format.
     * @param width pixels width
     * @param height pixels height
     */
    // 多分正しい
    // Y:0～255
    // U/V:0～255 => -128～127
    public static void convertYUV420_NV21toRGB8888(int[] rgb, byte [] data, int width, int height) {
        int size = width*height;
        int offset = size;
        int u, v, y1, y2, y3, y4;

        // i percorre os Y and the final pixels
        // k percorre os pixles U e V
        for(int i=0, k=0; i < size; i+=2, k+=2) {
            y1 = data[i  ]&0xff;
            y2 = data[i+1]&0xff;
            y3 = data[width+i  ]&0xff;
            y4 = data[width+i+1]&0xff;

            u = data[offset+k  ]&0xff;
            v = data[offset+k+1]&0xff;
            u = u-128;
            v = v-128;

            rgb[i  ] = convertYUVtoRGB(y1, u, v);
            rgb[i+1] = convertYUVtoRGB(y2, u, v);
            rgb[width+i  ] = convertYUVtoRGB(y3, u, v);
            rgb[width+i+1] = convertYUVtoRGB(y4, u, v);

            if (i!=0 && (i+2)%width==0)
                i+=width;
        }
    }

    private static int convertYUVtoRGB(int y, int u, int v) {
        int r,g,b;

        r = y + (int)1.402f*v;
        g = y - (int)(0.344f*u +0.714f*v);
        b = y + (int)1.772f*u;
        r = r>255? 255 : r<0 ? 0 : r;
        g = g>255? 255 : g<0 ? 0 : g;
        b = b>255? 255 : b<0 ? 0 : b;
        return 0xff000000 | (b<<16) | (g<<8) | r;
    }

    // 値域的に少し変
    // Y:16～235
    // Cb/Cr:16～240
    public static void YUV_NV21_TO_RGB(int[] argb, byte[] yuv, int width, int height) {
        final int frameSize = width * height;

        final int ii = 0;
        final int ij = 0;
        final int di = +1;
        final int dj = +1;

        int a = 0;
        for (int i = 0, ci = ii; i < height; ++i, ci += di) {
            for (int j = 0, cj = ij; j < width; ++j, cj += dj) {
                int y = (0xff & ((int) yuv[ci * width + cj]));
                int v = (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 0]));
                int u = (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = (int) (1.164f * (y - 16) + 1.596f * (v - 128));
                int g = (int) (1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = (int) (1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                argb[a++] = 0xff000000 | (r << 16) | (g << 8) | b;
            }
        }
    }

    // 若干係数が違う変換式
    public static void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
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

                if(r < 0) {
                    r = 0;
                }
                // 262143 = 2^18 - 1 = 16^4 * 2^2 - 1
                else if(r > 262143) {
                    r = 262143;
                }
                if(g < 0) {
                    g = 0;
                }
                else if(g > 262143) {
                    g = 262143;
                }
                if(b < 0) {
                    b = 0;
                }
                else if(b > 262143) {
                    b = 262143;
                }

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }
}
