package fi.mabrosim.memowidget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoWidgetEditActivity extends Activity {
    private final List<EditText> mEditTexts = new ArrayList<>();

    private static final Map<Integer, Integer> BUTTON_TO_EDITTEXT_MAP;

    private OnClickListener mClearOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ((EditText) findViewById(BUTTON_TO_EDITTEXT_MAP.get(v.getId()))).setText("");
        }
    };

    static {
        Map<Integer, Integer> m = new HashMap<>();
        m.put(R.id.buttonClear1, R.id.editTextLine1);
        m.put(R.id.buttonClear2, R.id.editTextLine2);
        m.put(R.id.buttonClear3, R.id.editTextLine3);
        m.put(R.id.buttonClear4, R.id.editTextLine4);
        m.put(R.id.buttonClear5, R.id.editTextLine5);
        BUTTON_TO_EDITTEXT_MAP = Collections.unmodifiableMap(m);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);
        mEditTexts.add((EditText) findViewById(R.id.editTextLine1));
        mEditTexts.add((EditText) findViewById(R.id.editTextLine2));
        mEditTexts.add((EditText) findViewById(R.id.editTextLine3));
        mEditTexts.add((EditText) findViewById(R.id.editTextLine4));
        mEditTexts.add((EditText) findViewById(R.id.editTextLine5));

        for (Integer buttonId : BUTTON_TO_EDITTEXT_MAP.keySet()) {
            findViewById(buttonId).setOnClickListener(mClearOnClickListener);
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
}
