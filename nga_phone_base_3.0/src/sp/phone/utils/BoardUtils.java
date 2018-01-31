package sp.phone.utils;

/**
 * Created by Justwen on 2018/1/29.
 */

public class BoardUtils {

    /**
     * @param statusCode
     * @return 返回子板块是否被订阅
     */
    public static boolean isBoardSubscribed(int statusCode) {
        // 3,810 返回false
        return statusCode == 7 || statusCode == 558;
    }

    public static boolean isDaxuanwoBoard(int fid) {
        return fid == -7;
    }
}
