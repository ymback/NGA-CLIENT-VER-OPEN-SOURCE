package gov.anzong.meizi;

public class MeiziStringUtils {

    /**
     * If the input string is null, make it a empty string.
     *
     * @param str
     * @return
     */
    public static String wrap(String str) {
        return (str == null) ? "" : str;
    }

    /**
     * Util to do the job of "StringA" + "StringB", but with the benifit of
     * avoiding the NullPointer trouble
     *
     * @param args
     * @return
     */
    public static String connect(String... args) {
        String result = "";

        for (String arg : args) {
            result += MeiziStringUtils.wrap(arg);
        }

        return result;
    }
}
