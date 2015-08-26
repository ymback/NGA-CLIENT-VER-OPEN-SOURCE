package gov.anzong.meizi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MeiziDateUtil {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.CHINA);

    /**
     * should be yyyy-MM-dd HH:mm:ss, or throws exception
     *
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date getDate(String dateString) throws ParseException {
        return dateFormat.parse(dateString);
    }

    public static String getDateString(Date date) {
        return dateFormat.format(date);
    }
}
