package fi.mabrosim.memowidget;

import android.content.Context;

public final class SortingType {
    public static final int DEFAULT = 0;
    public static final int BY_NAME = 1;
    public static final int BY_TIME = 2;

    private SortingType() {
    }

    public static void setNextSortingType(Context context) {
        switch (Prefs.getSortingType(context)) {
            case SortingType.BY_NAME: {
                Prefs.putSortingType(context, SortingType.BY_TIME);
                break;
            }
            case SortingType.BY_TIME: {
                Prefs.putSortingType(context, SortingType.DEFAULT);
                break;
            }
            default:
                Prefs.putSortingType(context, SortingType.BY_NAME);
                break;
        }
    }

    public static String toString(Context context, int type) {
        switch (type) {
            case SortingType.BY_TIME: {
                return context.getString(R.string.sorting_by_date);
            }
            case SortingType.BY_NAME: {
                return context.getString(R.string.sorting_by_name);

            }
            default: {
                return context.getString(R.string.sorting_default);

            }
        }
    }
}
