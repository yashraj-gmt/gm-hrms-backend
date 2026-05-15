package com.gm.hrms.util;

public class TimeUtil {

    public static int toMinutes(String time){

        String[] parts = time.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        return (hours * 60) + minutes;
    }

    public static String toHHmm(int minutes){

        int hours = minutes / 60;
        int remaining = minutes % 60;

        return String.format("%02d:%02d",hours,remaining);
    }

}