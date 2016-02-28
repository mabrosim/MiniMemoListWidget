package fi.mabrosim.memowidget;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

final class DemoTexts {
    private DemoTexts() {
    }

    public static void setDemoTexts(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Prefs.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String[] strings = context.getResources().getStringArray(R.array.demo_texts);
        JSONObject jsonObject;

        for (int i = 0; i < strings.length; i++) {
            editor.remove(Prefs.KEY_PREFIX_LINE + i);
            jsonObject = new JSONObject();
            try {
                jsonObject.put(Prefs.JSON_KEY_TEXT, strings[i]);
                jsonObject.put(Prefs.JSON_KEY_TIMESTAMP, System.currentTimeMillis());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            editor.putString(Prefs.KEY_PREFIX_LINE + i, jsonObject.toString());
        }
        editor.apply();
    }
}
