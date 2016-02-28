package fi.mabrosim.memowidget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MemoWidgetEditActivity extends Activity {
    private final List<EditText> editTexts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);
        editTexts.add((EditText) findViewById(R.id.editTextLine1));
        editTexts.add((EditText) findViewById(R.id.editTextLine2));
        editTexts.add((EditText) findViewById(R.id.editTextLine3));
        editTexts.add((EditText) findViewById(R.id.editTextLine4));
        editTexts.add((EditText) findViewById(R.id.editTextLine5));

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
            editTexts.get(i).setText(texts.get(i));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        final List<String> strings = new ArrayList<>();

        for (EditText et : editTexts) {
            strings.add(et.getText().toString());
        }

        Prefs.saveTexts(this, strings);
        MemoWidget.updateMemoWidget(this);
    }
}
