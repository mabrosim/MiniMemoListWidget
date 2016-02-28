package fi.mabrosim.memowidget;

import android.app.Application;

public class MemoWidgetApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.VERSION_CODE != Prefs.getVersionCode(this)) {
            // consider to add preferences upgrade logic here
            Prefs.putVersionCode(this);
        }
    }
}
