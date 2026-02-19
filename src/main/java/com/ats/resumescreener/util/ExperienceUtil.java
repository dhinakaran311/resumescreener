package com.ats.resumescreener.util;

import java.util.regex.*;

public class ExperienceUtil {

    public static int extractYears(String text){

        Pattern pattern =
            Pattern.compile("(\\d+)\\s*(\\+)?\\s*(years|yrs)");

        Matcher matcher = pattern.matcher(text.toLowerCase());

        int maxYears = 0;

        while(matcher.find()){
            int years =
                Integer.parseInt(matcher.group(1));

            if(years > maxYears)
                maxYears = years;
        }

        return maxYears;
    }
}
