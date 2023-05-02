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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
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
        MemoWidget.updateViews(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        MemoWidget.updateViews(context);
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
        MemoWidget.updateViews(context);
    }

    public static class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ClickHandlerWork.enqueueWork(context, intent.getAction());
        }
    }

    // Define the Worker requiring input
    public static class ClickHandlerWork extends Worker {

        public ClickHandlerWork(Context appContext, WorkerParameters workerParams) {
            super(appContext, workerParams);
        }

        public static void enqueueWork(Context context, String action) {
            WorkManager workManager = WorkManager.getInstance(context);
            OneTimeWorkRequest myUploadWork =
                    new OneTimeWorkRequest.Builder(ClickHandlerWork.class)
                            .setInputData(
                                    new Data.Builder()
                                            .putString("ACTION", action)
                                            .build()
                            )
                            .build();
            workManager.enqueueUniqueWork("UPDATE_LISTENER", ExistingWorkPolicy.KEEP, myUploadWork);
        }

        @NonNull
        @Override
        public Result doWork() {
            String action = getInputData().getString("ACTION");
            if (Clicks.ACTION_CLICK.equals(action)) {
                Clicks.handleClickAction(getApplicationContext());
            }
            return Result.success();
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
        Toast.makeText(context, R.string.toast_text_123, Toast.LENGTH_LONG).show();
        /*
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View layout = inflater.inflate(R.layout.toast_custom_layout, new FrameLayout(context), false);
            Toast toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText(R.string.toast_text_123);
            //toast.setText(TextUtils.join("\n", Arrays.asList(R.string.toast_text_1, R.string.toast_text_2, R.string.toast_text_3))) ;
            //toast.setView(layout);
            //toast.setView(layout);
            toast.show();
        }
        */
    }

    private static void startActivity(Context context, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
