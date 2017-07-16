package com.airhome.sportsrecord;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Calendar;

/**
 * Created by airhome on 2017/7/16.
 */

public class Utils {
    private static final String IMAGE_FILE = "sportsImage";

    public static File getFile() {
        String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + IMAGE_FILE;
        File file = new File(filePath);
        if (!file.exists()) {
            boolean result = file.mkdirs();
            Log.d("wangyh", "getFile: " + result);
        }
        File imageFile = new File(filePath + File.separator + timeToString());
        return imageFile;
    }

    public static String timeToString() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "." +
                calendar.get(Calendar.MONTH) + "." +
                calendar.get(Calendar.DAY_OF_MONTH);
    }
}
