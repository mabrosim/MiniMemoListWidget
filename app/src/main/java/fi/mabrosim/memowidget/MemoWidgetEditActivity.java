package fi.mabrosim.memowidget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemoWidgetEditActivity extends Activity {
    private final        List<EditText> mEditTexts  = new ArrayList<>();
    private static final int[]          editTextIds = {R.id.editTextLine1, R.id.editTextLine2, R.id.editTextLine3, R.id.editTextLine4, R.id.editTextLine5};
    private static final List<Integer>  buttonIds   = Arrays.asList(R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        for (int i = 0; i < editTextIds.length; i++) {
            EditText editText = (EditText) findViewById(editTextIds[i]);
            ImageButton imageButton = (ImageButton) findViewById(buttonIds.get(i));

            mEditTexts.add(editText);
            editText.addTextChangedListener(new ButtonStateTextWatcher(imageButton));
            imageButton.setTag(R.id.cachedText, "");
        }

        if (!Prefs.isWidgetEnabled(this)) {
            findViewById(R.id.textViewDisabledHint).setVisibility(View.VISIBLE);
        }
        setResult(RESULT_OK);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> texts = Prefs.getTexts(this);
        int count = texts.size();
        for (int i = 0; i < count; i++) {
            mEditTexts.get(i).setText(texts.get(i));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        final List<String> strings = new ArrayList<>();
        for (EditText et : mEditTexts) {
            strings.add(et.getText().toString());
        }
        Prefs.saveTexts(this, strings);
        MemoWidget.updateMemoWidget(this);
    }

    public void onButtonClick(View view) {
        switch ((ButtonStateTextWatcher.STATE) view.getTag(R.id.buttonState)) {
            case CLEAR: {
                EditText editText = (EditText) findViewById(editTextIds[buttonIds.indexOf(view.getId())]);
                view.setTag(R.id.cachedText, editText.getText().toString());
                editText.setText("");
                break;
            }
            case UNDO: {
                String text = (String) view.getTag(R.id.cachedText);
                view.setTag(R.id.cachedText, "");
                ((EditText) findViewById(editTextIds[buttonIds.indexOf(view.getId())])).setText(text);
                break;
            }
            default:
            case INACTIVE: {
                break;
            }
        }
    }
}
