package fi.mabrosim.memowidget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

final class Clicks {
    static final String ACTION_CLICK = "fi.mabrosim.memowidget.action.CLICK";

    private static final Clicks sInstance         = new Clicks();
    private static final int    CLICK_DELAY_IN_MS = 380;
    private              int    mClickCount       = 0;

    // the class is singleton, prevent instantiation
    private Clicks() {
    }

    static void handleClickAction(final Context context) {
        final Handler mHandler = new Handler(Looper.getMainLooper());

        int clickCount = sInstance.mClickCount;
        sInstance.mClickCount = ++clickCount;

        if (clickCount == 1) {
            mHandler.postDelayed(() -> handleClicks(context), CLICK_DELAY_IN_MS);
        }
    }

    private static void handleClicks(Context context) {
        switch (sInstance.mClickCount) {
            case 2: {
                MemoWidget.doubleClickHandler(context);
                break;
            }
            case 1:
            default: {
                MemoWidget.singleClickHandler(context);
                break;
            }
        }
        sInstance.mClickCount = 0;
    }
}

