package fi.mabrosim.memowidget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of MemoWidget functionality.
 * App Widget Configuration implemented in {@link MemoWidgetConfigureActivity
 * MemoWidgetConfigureActivity}
 */
public class MemoWidget extends AppWidgetProvider {
    private static final int VIEW_IDS[] = {R.id.tvLine1, R.id.tvLine2, R.id.tvLine3, R.id.tvLine4, R.id.tvLine5};

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
        context.startService(new Intent(context, UpdateService.class));
    }

    public static class UpdateService extends IntentService {
        private static final String  TAG      = "MemoWidgetUpdateService";
        private final        Handler mHandler = new Handler();

        public UpdateService() {
            super(TAG);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        protected void onHandleIntent(Intent intent) {
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
        SortingType.setNextSortingType(context);
        updateMemoWidget(context);
    }

    static void tripleClickHandler(Context context) {
        startActivity(context, MemoWidgetConfigureActivity.class);
    }

    private static void updateViews(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.memowidget);

        List<String> texts = Prefs.getTexts(context, Prefs.getSortingType(context));
        int count = texts.size();
        for (int i = 0; i < count; i++) {
            views.setTextViewText(VIEW_IDS[i], texts.get(i));
        }
        // the footer is either sorting type or last edited time
        String sortingType = SortingType.toString(context, Prefs.getSortingType(context));
        if (sortingType.isEmpty()) {
            views.setTextViewText(R.id.textFooter, timestampToString(Prefs.getLastEditedTimestamp(context)));
        } else {
            views.setTextViewText(R.id.textFooter, sortingType);
        }

        Intent intent = new Intent(Clicks.ACTION_CLICK, null, context, UpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.layoutWidget, pendingIntent);

        // Instruct the widget manager to update the widget
        ComponentName thisWidget = new ComponentName(context, MemoWidget.class);
        AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, views);
    }

    private static CharSequence timestampToString(long ts) {
        SimpleDateFormat sdf;
        Date resultDate = new Date(ts);
        sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        return sdf.format(resultDate);
    }

    private static void showToast(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_custom_layout, new FrameLayout(context), false);
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private static void startActivity(Context context, Class activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
