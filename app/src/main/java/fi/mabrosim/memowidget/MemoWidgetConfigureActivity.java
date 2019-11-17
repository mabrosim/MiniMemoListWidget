package fi.mabrosim.memowidget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

/**
 * The configuration screen for the {@link MemoWidget MemoWidget} AppWidget.
 */
public class MemoWidgetConfigureActivity extends Activity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.memowidget_configure);

        final EditText editText = (EditText) findViewById(R.id.editListSize);
        editText.setText(String.valueOf(Prefs.getTextLineCount(this)));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    Prefs.setTextLineCount(editText.getContext(), Integer.parseInt(editText.getText().toString()));
                }
            }
        });
        findViewById(R.id.button_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Switch switchHint = (Switch) findViewById(R.id.switch1);
        switchHint.setChecked(Prefs.isShowHint(this));
        switchHint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Prefs.setShowHint(compoundButton.getContext(), b);

                if (BuildConfig.DEBUG) {
                    DemoTexts.setDemoTexts(getApplicationContext());
                }
            }
        });

        setResult(RESULT_OK);
    }

    @Override
    protected void onPause() {
        MemoWidget.updateMemoWidget(this);
        super.onPause();
    }
}



