package com.kingleystudio.remarket.utils;

import java.text.MessageFormat;

public class StringUtils {
    public static String beautifyPhone(String rawPhone) {
        if (rawPhone.startsWith("8") || rawPhone.startsWith("7")) {
            MessageFormat phoneMsgFormat = new MessageFormat("+7 ({0}) {1}-{2}");
            String[] phoneNumArr = {rawPhone.substring(1,4),rawPhone.substring(4,7),rawPhone.substring(7)};
            return phoneMsgFormat.format(phoneNumArr);
        } else if (rawPhone.startsWith("+7")) {
            MessageFormat phoneMsgFormat = new MessageFormat("+7 ({0}) {1}-{2}");
            String[] phoneNumArr = {rawPhone.substring(2,5),rawPhone.substring(5,8),rawPhone.substring(8)};
            return phoneMsgFormat.format(phoneNumArr);
        } else if (rawPhone.startsWith("+375")) {
            MessageFormat phoneMsgFormat = new MessageFormat("+375 {0} {1} {2}");
            String[] phoneNumArr = {rawPhone.substring(4,6),rawPhone.substring(6,9),rawPhone.substring(9)};
            return phoneMsgFormat.format(phoneNumArr);
        } else if (rawPhone.startsWith("375")) {
            MessageFormat phoneMsgFormat = new MessageFormat("+375 {0} {1} {2}");
            String[] phoneNumArr = {rawPhone.substring(3,5),rawPhone.substring(5,8),rawPhone.substring(8)};
            return phoneMsgFormat.format(phoneNumArr);
        }
        return "";
    }
}
