package fi.mabrosim.memowidget;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

final class TextLine {
    private final int    mId;
    private final String mText;
    private final long   mTimestamp;

    TextLine(String text, long timestamp, int i) {
        mText = text;
        mTimestamp = timestamp;
        mId = i;
    }

    CharSequence getText() {
        return mText;
    }

    int getId() {
        return mId;
    }

    private long getTimestamp() {
        return mTimestamp;
    }

    static void sort(List<TextLine> lines, int sortingType) {
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
    }

    static long getLastEditedTimestamp(List<TextLine> lines) {
        Collections.sort(lines, new TextLine.TimestampComparator());
        return lines.get(0).getTimestamp();
    }

    @Override
    public String toString() {
        return "TextLine: id[ " + mId + " ] ,timestamp[ " + mTimestamp + " ] ,text[ " + mText + " ]";
    }

    /**
     * Comparator implements simple alphanumeric sorting from a to z,<br>
     * empty text line is shifted to the list end.
     */
    private static class NameComparator implements Comparator<TextLine>, Serializable {

        @Override
        public int compare(TextLine lhs, TextLine rhs) {
            if (lhs.mText.isEmpty()) {
                return 1;
            } else if (rhs.mText.isEmpty()) {
                return -1;
            } else {
                return lhs.mText.compareTo(rhs.mText);
            }
        }
    }

    /**
     * Comparator implements timestamp sorting from last to first,<br>
     * empty text line is shifted to the list end.
     */
    private static class TimestampComparator implements Comparator<TextLine>, Serializable {

        @Override
        public int compare(TextLine lhs, TextLine rhs) {
            if (lhs.mText.isEmpty()) {
                return 1;
            } else if (rhs.mText.isEmpty()) {
                return -1;
            } else {
                return rhs.mTimestamp < lhs.mTimestamp ? -1 : (rhs.mTimestamp == lhs.mTimestamp ? 0 : 1);
            }
        }
    }
}
