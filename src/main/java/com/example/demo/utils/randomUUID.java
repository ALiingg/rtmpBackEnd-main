package com.example.demo.utils;

import java.util.UUID;

public class randomUUID {
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
