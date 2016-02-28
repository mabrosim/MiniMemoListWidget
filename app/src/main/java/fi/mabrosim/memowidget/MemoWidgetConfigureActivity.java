package fi.mabrosim.memowidget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * The configuration screen for the {@link MemoWidget MemoWidget} AppWidget.
 */
public class MemoWidgetConfigureActivity extends Activity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.memowidget_configure);

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
}



