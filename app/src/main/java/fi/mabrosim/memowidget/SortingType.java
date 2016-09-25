package fi.mabrosim.memowidget;

import android.content.Context;

final class SortingType {
    static final int DEFAULT = 0;
    static final int BY_NAME = 1;
    static final int BY_TIME = 2;

    private SortingType() {
    }

    static CharSequence toString(Context context, int type) {
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
