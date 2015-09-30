package snct.procon26.ziyuu.functions;

public class MyFunction {
    public static int max(int a, int b, int c) {
        if(a > b) {
            return (a > c) ? a : c;
        }
        return (b > c) ? b : c;
    }
    public static int min(int a, int b, int c) {
        if(a < b) {
            return (a < c) ? a : c;
        }
        return (b < c) ? b : c;
    }
}
