package com.manan.appointment.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Manan on 8/6/2016.
 */
public class emailValidate {

    private static Pattern pattern;
    private static Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Validate email with regular expression
     *
     */
    public static boolean validate(final String hex) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(hex);
        return matcher.matches();

    }

}
