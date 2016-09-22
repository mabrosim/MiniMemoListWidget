package fi.mabrosim.memowidget;

import java.io.Serializable;
import java.util.Comparator;

class TextLine {
    private String mText      = "";
    private long   mTimestamp = 0L;

    TextLine(String text, long timestamp) {
        mText = text;
        mTimestamp = timestamp;
    }

    String getText() {
        return mText;
    }

    long getTimestamp() {
        return mTimestamp;
    }

    /**
     * Comparator implements simple alphanumeric sorting from a to z,<br>
     * empty text line is shifted to the list end.
     */
    static class NameComparator implements Comparator<TextLine>, Serializable {

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
    static class TimestampComparator implements Comparator<TextLine>, Serializable {

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
