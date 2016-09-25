package fi.mabrosim.memowidget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;

public class ButtonStateTextWatcher implements TextWatcher {
    private final ImageButton mImgBtn;

    enum STATE {
        INACTIVE,
        CLEAR,
        UNDO
    }

    ButtonStateTextWatcher(ImageButton imgBtn) {
        mImgBtn = imgBtn;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (String.valueOf(mImgBtn.getTag(R.id.cachedText)).isEmpty()) {
            setButtonState(mImgBtn, s.toString().isEmpty() ? STATE.INACTIVE : STATE.CLEAR);
        } else {
            setButtonState(mImgBtn, s.toString().isEmpty() ? STATE.UNDO : STATE.CLEAR);
        }
    }

    private static void setButtonState(ImageButton imageButton, STATE state) {
        if (state == imageButton.getTag(R.id.buttonState)) {
            return;
        }
        imageButton.setTag(R.id.buttonState, state);
        switch (state) {
            case CLEAR: {
                imageButton.setImageResource(R.drawable.ic_clear_black_24dp);
                imageButton.setTag(R.id.cachedText, "");
                break;
            }
            case UNDO: {
                imageButton.setImageResource(R.drawable.ic_undo_black_24dp);
                break;
            }
            default:
            case INACTIVE: {
                imageButton.setImageResource(R.drawable.ic_clear_grey_400_24dp);
                break;
            }
        }
    }
}
