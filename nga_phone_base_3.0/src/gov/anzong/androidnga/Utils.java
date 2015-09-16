package gov.anzong.androidnga;

/**
 * Created by liuboyu on 2015/8/25.
 */
public class Utils {

    private static final String DOMAIN = "bbs.ngacn.cc";

    public static String getNGAHost() {
        return "http://" + getNGADomain() + "/";
    }

    public static String getNGADomain() {
        return DOMAIN;
    }
}
