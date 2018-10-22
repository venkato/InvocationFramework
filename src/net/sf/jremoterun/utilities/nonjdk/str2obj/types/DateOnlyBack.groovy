package net.sf.jremoterun.utilities.nonjdk.str2obj.types

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.joda.time.LocalDate;

import java.util.logging.Logger;

@CompileStatic
class DateOnlyBack implements Serializable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public LocalDate date;

    DateOnlyBack(LocalDate date) {
        this.date = date
        assert date!=null
    }


    /**
     * @return If month < 10 , contains leading 0
     */
    String getMonthOfYear() {
        return convertNumber(date.getMonthOfYear())
    }

    /**
     * @return If day < 10 , contains leading 0
     */
    String getDayOfYear() {
        return convertNumber(date.getDayOfMonth())
    }

    static String convertNumber(int number1) {
        String result = number1 + ''
        if (number1 < 10) {
            result = '0' + result;
        }
        return result

    }

    @Override
    String toString() {
        return date.toString()
    }
}
