package fi.mabrosim.memowidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemoWidgetEditActivity extends Activity {
    private final List<EditText> mEditTexts = new ArrayList<>();
    private static final int[] EDIT_TEXT_IDS = {R.id.editTextLine1, R.id.editTextLine2,
            R.id.editTextLine3, R.id.editTextLine4, R.id.editTextLine5, R.id.editTextLine6,
            R.id.editTextLine7, R.id.editTextLine8, R.id.editTextLine9};
    private static final List<Integer> BUTTON_IDS = Arrays.asList(R.id.button1, R.id.button2,
            R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9);
    private static final int[] LAYOUT_IDS = {R.layout.activity_memo_edit5,
            R.layout.activity_memo_edit6, R.layout.activity_memo_edit7,
            R.layout.activity_memo_edit8, R.layout.activity_memo_edit9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int textLineCount = Prefs.getTextLineCount(this);
        setContentView(LAYOUT_IDS[textLineCount - 5]);

        for (int i = 0; i < textLineCount; i++) {
            EditText editText = findViewById(EDIT_TEXT_IDS[i]);
            ImageButton imageButton = findViewById(BUTTON_IDS.get(i));

            mEditTexts.add(editText);
            editText.addTextChangedListener(new ButtonStateTextWatcher(imageButton));
            imageButton.setTag(R.id.cachedText, "");
        }

        if (!Prefs.isWidgetEnabled(this)) {
            findViewById(R.id.textViewDisabledHint).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.buttonSettings).setOnClickListener(view -> {
            Intent intent = new Intent(this, MemoWidgetConfigureActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);

            finish();
        });
        setResult(RESULT_OK);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<TextLine> lines = Prefs.getTextLines(this);
        for (int i = 0; i < Prefs.getTextLineCount(this); i++) {
            mEditTexts.get(i).setText(lines.get(i).getText());
        }
        TextView footer = findViewById(R.id.textFooter);
        footer.setText(Utils.getFooterText(this, lines));
    }

    @Override
    protected void onPause() {
        super.onPause();
        List<String> strings = new ArrayList<>();
        for (EditText et : mEditTexts) {
            strings.add(et.getText().toString());
        }
        Prefs.saveTexts(this, strings);
        MemoWidget.updateMemoWidget(this);
    }

    public void onButtonClick(View view) {
        switch ((ButtonStateTextWatcher.STATE) view.getTag(R.id.buttonState)) {
            case CLEAR: {
                EditText editText = findViewById(EDIT_TEXT_IDS[BUTTON_IDS.indexOf(view.getId())]);
                view.setTag(R.id.cachedText, editText.getText().toString());
                editText.setText("");
                break;
            }
            case UNDO: {
                CharSequence text = (CharSequence) view.getTag(R.id.cachedText);
                view.setTag(R.id.cachedText, "");
                ((EditText) findViewById(EDIT_TEXT_IDS[BUTTON_IDS.indexOf(view.getId())])).setText(text);
                break;
            }
            default:
            case INACTIVE: {
                break;
            }
        }
    }
}
