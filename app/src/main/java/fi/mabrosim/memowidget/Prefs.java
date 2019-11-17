package fi.mabrosim.memowidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

final class Prefs {
    static final String PREFS_NAME = "fi.mabrosim.memowidget.prefs";
    static final String KEY_PREFIX_LINE = "LINE_";
    static final String JSON_KEY_TIMESTAMP = "timestamp";
    static final String JSON_KEY_TEXT = "text";

    private static final String KEY_SORTING_TYPE = "SORTING_TYPE";
    private static final String KEY_VERSION_CODE = "VERSION_CODE";
    private static final String PREF_SHOW_HINT = "SHOW_HINT";
    private static final String PREF_IS_WIDGET_ENABLED = "WIDGET_IS_ENABLED";
    private static final String PREF_TEXT_LINE_COUNT = "TEXT_LINE_COUNT";
    private static final int DEFAULT_TEXT_LINE_COUNT = 6;

    private Prefs() {
    }

    static boolean isShowHint(Context context) {
        return getBoolean(context, PREF_SHOW_HINT, true);
    }

    static void setShowHint(Context context, boolean b) {
        putBoolean(context, PREF_SHOW_HINT, b);
    }

    static void putEnabledValue(Context context, boolean v) {
        putBoolean(context, PREF_IS_WIDGET_ENABLED, v);
    }

    static boolean isWidgetEnabled(Context context) {
        return getBoolean(context, PREF_IS_WIDGET_ENABLED, false);
    }

    static int getSortingType(Context context) {
        return sharedPreferences(context).getInt(KEY_SORTING_TYPE, SortingType.DEFAULT);
    }

    static void setNextSortingType(Context context) {
        switch (Prefs.getSortingType(context)) {
            case SortingType.BY_NAME: {
                Prefs.putSortingType(context, SortingType.BY_TIME);
                break;
            }
            case SortingType.DEFAULT: {
                Prefs.putSortingType(context, SortingType.BY_NAME);
                break;
            }
            default:
            case SortingType.BY_TIME: {
                Prefs.putSortingType(context, SortingType.DEFAULT);
                break;
            }
        }
    }

    static void setTextLineCount(Context context, int lineCount) {
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences(context).edit();
        sharedPrefEditor.putInt(PREF_TEXT_LINE_COUNT, lineCount);
        sharedPrefEditor.apply();
    }

    static int getTextLineCount(Context context) {
        return sharedPreferences(context).getInt(PREF_TEXT_LINE_COUNT, DEFAULT_TEXT_LINE_COUNT);
    }

    static long getVersionCode(Context context) {
        return sharedPreferences(context).getLong(KEY_VERSION_CODE, 0L);
    }

    static void putVersionCode(Context context) {
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences(context).edit();
        sharedPrefEditor.putLong(KEY_VERSION_CODE, (long) BuildConfig.VERSION_CODE);
        sharedPrefEditor.apply();
    }

    static void saveTexts(Context context, List<String> texts) {
        final SharedPreferences sharedPreferences = sharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final List<TextLine> storedLines = getTextLines(context);
        final int textLineCount = sharedPreferences.getInt(PREF_TEXT_LINE_COUNT, DEFAULT_TEXT_LINE_COUNT);

        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < textLineCount; i++) {
            String newText = texts.get(i);
            TextLine line = storedLines.get(i);

            if (!(newText.equals(line.getText()))) {
                try {
                    int id = line.getId();
                    editor.remove(KEY_PREFIX_LINE + id);
                    jsonObject.put(JSON_KEY_TEXT, newText);
                    jsonObject.put(JSON_KEY_TIMESTAMP, System.currentTimeMillis());
                    editor.putString(KEY_PREFIX_LINE + id, jsonObject.toString());
                } catch (JSONException e) {
                    Log.e("Prefs", "saveTexts: ", e);
                }
            }
        }
        editor.apply();
    }

    static List<TextLine> getTextLines(Context context) {
        final SharedPreferences sharedPreferences = sharedPreferences(context);
        final List<TextLine> lines = new ArrayList<>();
        final int textLineCount = sharedPreferences.getInt(PREF_TEXT_LINE_COUNT, DEFAULT_TEXT_LINE_COUNT);

        for (int i = 0; i < textLineCount; i++) {
            try {
                String text;
                long timestamp;
                JSONObject jsonObject = new JSONObject(sharedPreferences.getString(KEY_PREFIX_LINE + i, "{}"));
                if (jsonObject.has(JSON_KEY_TIMESTAMP)) {
                    timestamp = jsonObject.getLong(JSON_KEY_TIMESTAMP);
                } else {
                    timestamp = System.currentTimeMillis();
                }
                if (jsonObject.has(JSON_KEY_TEXT)) {
                    text = jsonObject.getString(JSON_KEY_TEXT);
                } else {
                    text = "";
                }
                lines.add(new TextLine(text, timestamp, i));
            } catch (JSONException e) {
                Log.e("Prefs", "getTextLines: ", e);
            }
        }
        TextLine.sort(lines, getSortingType(context));
        return lines;
    }

    private static void putSortingType(Context context, int type) {
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences(context).edit();
        sharedPrefEditor.putInt(KEY_SORTING_TYPE, type);
        sharedPrefEditor.apply();
    }

    private static SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static void putBoolean(Context context, String prefName, boolean value) {
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences(context).edit();
        sharedPrefEditor.putBoolean(prefName, value);
        sharedPrefEditor.apply();
    }

    private static boolean getBoolean(Context context, String prefName, boolean defaultValue) {
        return sharedPreferences(context).getBoolean(prefName, defaultValue);
    }
}
