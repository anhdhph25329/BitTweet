package fpt.anhdhph.bittweet.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class UserUtil {
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_USER_ID = "user_id";

    public static String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, null);

        if (userId == null) {
            userId = UUID.randomUUID().toString();
            prefs.edit().putString(KEY_USER_ID, userId).apply();
        }

        return userId;
    }
}
