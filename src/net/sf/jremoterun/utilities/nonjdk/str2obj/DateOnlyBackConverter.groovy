package net.sf.jremoterun.utilities.nonjdk.str2obj

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.str2obj.types.DateOnlyBack
import org.joda.time.LocalDate

import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class DateOnlyBackConverter implements StringToObjectConverterI2<DateOnlyBack> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    DateOnlyBack convert(String str, Type genericArg) {
        int day;
        if (str.length() == 8) {
            // assume date fully specified in format YYYYMMDD
            int year = str.substring(0, 4) as int;
            int month = str.substring(4, 6) as int;
            day = str.substring(6) as int;
            if (day > 31) {
                throw new Exception("day need be less 32 : ${day}")
            }
            if (month > 12) {
                throw new Exception("month need be less 13 : ${day}")
            }

            return new DateOnlyBack(new LocalDate(year, month, day));
        }
        day = Integer.parseInt(str);
        return new DateOnlyBack(getBackDay(day, new LocalDate()));
    }

    static LocalDate getBackDay(int day, LocalDate now) {
        if (day < 1) {
            throw new Exception("day need be positive : ${day}")
        }
        if (day > 31) {
            throw new Exception("day need be less 31 : ${day}")
        }
        int month = now.getMonthOfYear();
        int year = now.getYear();
        if (day > now.getDayOfMonth()) {
            if (month == 1) {
                year = year - 1;
                month = 12;
            } else {
                month = month - 1;
            }
        }
        return new LocalDate(year, month, day);
    }
}
