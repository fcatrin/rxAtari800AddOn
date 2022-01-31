
package xtvapps.retrobox.atari800;

import com.tvi910.android.core.PreferenceActivityUpdateSummary;
import xtvapps.prg.atari800.R;

import android.os.Bundle;

public class Droid800Preferences extends PreferenceActivityUpdateSummary {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.xml.preferences);
    }

}
