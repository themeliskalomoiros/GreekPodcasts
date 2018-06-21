package gr.kalymnos.sk3m3l10.greekpodcasts.utils;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class DateUtils {

    private static Calendar calendar = Calendar.getInstance();

    private DateUtils() {
    }

    public static String getStringDateFromMilli(long milli, @NonNull Resources resources, boolean monthFirstThreeLettersOnly) {
        int day = getDayFromMilli(milli);
        String month = getStringMonth(getMonthFromMilli(milli), resources, monthFirstThreeLettersOnly);
        int year = getYearFromMilli(milli);
        return String.format("%d %s %d", day, month, year);
    }

    //  Returns the joined date of a podcaster in a nice format.
    public static String getJoinedDate(long milli, Resources resources) {
        int day = getDayFromMilli(milli);
        String month = getStringMonth(getMonthFromMilli(milli), resources, false);
        int year = getYearFromMilli(milli);
        return String.format("%s %d, %d", month, day, year);   //  example:    March 23, 2018
    }

    private static String getStringMonth(int month, Resources resources, boolean firstThreeLettersOnly) {
        String returnedString;

        switch (month) {
            case Calendar.JANUARY:
                returnedString = resources.getString(R.string.month_jan);
                break;
            case Calendar.FEBRUARY:
                returnedString = resources.getString(R.string.month_feb);
                break;
            case Calendar.MARCH:
                returnedString = resources.getString(R.string.month_mar);
                break;
            case Calendar.APRIL:
                returnedString = resources.getString(R.string.month_apr);
                break;
            case Calendar.MAY:
                returnedString = resources.getString(R.string.month_may);
                break;
            case Calendar.JUNE:
                returnedString = resources.getString(R.string.month_jun);
                break;
            case Calendar.JULY:
                returnedString = resources.getString(R.string.month_jul);
                break;
            case Calendar.AUGUST:
                returnedString = resources.getString(R.string.month_aug);
                break;
            case Calendar.SEPTEMBER:
                returnedString = resources.getString(R.string.month_sep);
                break;
            case Calendar.OCTOBER:
                returnedString = resources.getString(R.string.month_oct);
                break;
            case Calendar.NOVEMBER:
                returnedString = resources.getString(R.string.month_nov);
                break;
            case Calendar.DECEMBER:
                returnedString = resources.getString(R.string.month_dec);
                break;
            default:
                throw new UnsupportedOperationException(DateUtils.class.getSimpleName() + ": " + month + " is not a valid month!");
        }

        if (firstThreeLettersOnly) {
            returnedString = returnedString.substring(0, 3);
        }

        return returnedString;
    }

    private static int getDayFromMilli(long milli) {
        calendar.setTimeInMillis(milli);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    private static int getMonthFromMilli(long milli) {
        calendar.setTimeInMillis(milli);
        return calendar.get(Calendar.MONTH);
    }

    private static int getYearFromMilli(long milli) {
        calendar.setTimeInMillis(milli);
        return calendar.get(Calendar.YEAR);
    }

    public static String dateRFC3339(long millis) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                .format(new Date(millis));
    }
}
