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
}
