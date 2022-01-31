
package xtvapps.retrobox.atari800;

import com.tvi910.android.core.PreferenceActivityUpdateSummary;

import android.os.Bundle;
import xtvapps.retrobox.v2.atari800.R;

public class Droid800Preferences extends PreferenceActivityUpdateSummary {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.xml.preferences);
    }

}
