package fi.mabrosim.memowidget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.List;

/**
 * Implementation of MemoWidget functionality.
 * App Widget Configuration implemented in {@link MemoWidgetConfigureActivity
 * MemoWidgetConfigureActivity}
 */
public class MemoWidget extends AppWidgetProvider {
    private static final int[] VIEW_IDS = {R.id.tvLine1, R.id.tvLine2, R.id.tvLine3, R.id.tvLine4,
            R.id.tvLine5, R.id.tvLine6, R.id.tvLine7, R.id.tvLine8, R.id.tvLine9};

    private static final int[] LAYOUT_IDS = {R.layout.memowidget5, R.layout.memowidget6,
            R.layout.memowidget7, R.layout.memowidget8, R.layout.memowidget9};

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateMemoWidget(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        updateMemoWidget(context);
    }

    @Override
    public void onEnabled(Context context) {
        Prefs.putEnabledValue(context, true);
    }

    @Override
    public void onDisabled(Context context) {
        Prefs.putEnabledValue(context, false);
    }

    public static void updateMemoWidget(Context context) {
        UpdateService.enqueueWork(context, new Intent());
    }

    public static class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            UpdateService.enqueueWork(context, intent);
        }
    }

    public static class UpdateService extends JobIntentService {
        private final Handler mHandler = new Handler(Looper.getMainLooper());

        public static void enqueueWork(Context context, Intent work) {
            enqueueWork(context, UpdateService.class, 1, work);
        }

        @Override
        protected void onHandleWork(@NonNull Intent intent) {
            if (Clicks.ACTION_CLICK.equals(intent.getAction())) {
                Clicks.handleClickAction(this, mHandler);
            } else {
                MemoWidget.updateViews(this);
            }
        }
    }

    static void singleClickHandler(Context context) {
        startActivity(context, MemoWidgetEditActivity.class);
    }

    static void doubleClickHandler(Context context) {
        if (Prefs.isShowHint(context)) {
            showToast(context);
        }
        Prefs.setNextSortingType(context);
        updateMemoWidget(context);
    }

    static void tripleClickHandler(Context context) {
        startActivity(context, MemoWidgetConfigureActivity.class);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private static void updateViews(Context context) {
        final int textLineCount = Prefs.getTextLineCount(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), LAYOUT_IDS[textLineCount - 5]);
        List<TextLine> lines = Prefs.getTextLines(context);

        for (int i = 0; i < textLineCount; i++) {
            views.setTextViewText(VIEW_IDS[i], lines.get(i).getText());
        }

        views.setTextViewText(R.id.textFooter, Utils.getFooterText(context, lines));

        Intent intent = new Intent(Clicks.ACTION_CLICK, null, context, Receiver.class);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        }
        views.setOnClickPendingIntent(R.id.layoutWidget, pendingIntent);

        // Instruct the widget manager to update the widget
        ComponentName thisWidget = new ComponentName(context, MemoWidget.class);
        AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, views);
    }

    private static void showToast(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View layout = inflater.inflate(R.layout.toast_custom_layout, new FrameLayout(context), false);
            Toast toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }

    private static void startActivity(Context context, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
