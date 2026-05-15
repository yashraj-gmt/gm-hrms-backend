package com.gm.hrms.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeProvider {

    public static LocalDate today(){
        return LocalDate.now();
    }

    public static LocalDateTime now(){
        return LocalDateTime.now();
    }
}