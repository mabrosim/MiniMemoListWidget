package fi.mabrosim.memowidget;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Prefs {
    static final String PREFS_NAME         = "fi.mabrosim.memowidget.prefs";
    static final String KEY_PREFIX_LINE    = "LINE_";
    static final String JSON_KEY_TIMESTAMP = "timestamp";
    static final String JSON_KEY_TEXT      = "text";

    private static final String KEY_SORTING_TYPE       = "SORTING_TYPE";
    private static final String KEY_VERSION_CODE       = "VERSION_CODE";
    private static final String PREF_SHOW_HINT         = "SHOW_HINT";
    private static final String PREF_IS_WIDGET_ENABLED = "WIDGET_IS_ENABLED";

    private final static int TEXT_LINE_COUNT = 5;

    private Prefs() {
    }

    public static boolean isShowHint(Context context) {
        return getBoolean(context, PREF_SHOW_HINT, true);
    }

    public static void setShowHint(Context context, boolean b) {
        putBoolean(context, PREF_SHOW_HINT, b);
    }

    public static void putEnabledValue(Context context, boolean v) {
        putBoolean(context, PREF_IS_WIDGET_ENABLED, v);
    }

    public static boolean isWidgetEnabled(Context context) {
        return getBoolean(context, PREF_IS_WIDGET_ENABLED, false);
    }

    public static void saveTexts(Context context, List<String> lines) {
        final SharedPreferences sharedPreferences = sharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final List<TextLine> storedLines = getTextLines(context);
        String newText;
        JSONObject jsonObject;

        for (int i = 0; i < TEXT_LINE_COUNT; i++) {
            newText = lines.get(i);
            if (!(storedLines.get(i).getText().equals(newText))) {
                jsonObject = new JSONObject();
                try {
                    editor.remove(KEY_PREFIX_LINE + i);
                    jsonObject.put(JSON_KEY_TEXT, newText);
                    jsonObject.put(JSON_KEY_TIMESTAMP, System.currentTimeMillis());
                    editor.putString(KEY_PREFIX_LINE + i, jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        editor.apply();
    }

    public static List<String> getTexts(Context context) {
        final List<String> texts = new ArrayList<>();
        for (TextLine line : getTextLines(context)) {
            texts.add(line.getText());
        }
        return texts;
    }

    public static List<String> getTexts(Context context, int sortingType) {
        final List<String> texts = new ArrayList<>();
        List<TextLine> lines = getTextLines(context);

        switch (sortingType) {
            case SortingType.BY_NAME: {
                Collections.sort(lines, new TextLine.NameComparator());
                break;
            }
            case SortingType.BY_TIME: {
                Collections.sort(lines, new TextLine.TimestampComparator());
                break;
            }
            case SortingType.DEFAULT: {
                break;
            }
        }

        for (TextLine line : lines) {
            texts.add(line.getText());
        }
        return texts;
    }

    public static long getLastEditedTimestamp(Context context) {
        List<TextLine> lines = getTextLines(context);
        Collections.sort(lines, new TextLine.TimestampComparator());
        return lines.get(0).getTimestamp();
    }

    public static int getSortingType(Context context) {
        return sharedPreferences(context).getInt(KEY_SORTING_TYPE, SortingType.DEFAULT);
    }

    public static void putSortingType(Context context, int type) {
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences(context).edit();
        sharedPrefEditor.putInt(KEY_SORTING_TYPE, type);
        sharedPrefEditor.apply();
    }

    public static long getVersionCode(Context context) {
        return sharedPreferences(context).getLong(KEY_VERSION_CODE, 0L);
    }

    public static void putVersionCode(Context context) {
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences(context).edit();
        sharedPrefEditor.putLong(KEY_VERSION_CODE, (long) BuildConfig.VERSION_CODE);
        sharedPrefEditor.apply();
    }

    private static List<TextLine> getTextLines(Context context) {
        final SharedPreferences sharedPreferences = sharedPreferences(context);
        final List<TextLine> lines = new ArrayList<>();
        JSONObject jsonObject;
        long timestamp;
        String text;

        for (int i = 0; i < TEXT_LINE_COUNT; i++) {
            try {
                jsonObject = new JSONObject(sharedPreferences.getString(KEY_PREFIX_LINE + i, new JSONObject().toString()));
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
                lines.add(new TextLine(text, timestamp));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return lines;
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
