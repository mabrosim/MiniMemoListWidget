package fi.mabrosim.memowidget;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

final class Utils {

    private Utils() {
    }

    static CharSequence getFooterText(Context context, List<TextLine> lines) {
        // the footer is either sorting type or last edited time
        int sortingType = Prefs.getSortingType(context);
        if (sortingType == SortingType.DEFAULT) {
            return Utils.timestampToString(TextLine.getLastEditedTimestamp(lines));
        } else {
            return SortingType.toString(context, sortingType);
        }
    }

    private static CharSequence timestampToString(long ts) {
        Date resultDate = new Date(ts);
        return new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(resultDate);
    }
}
