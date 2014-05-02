
package xtvapps.retrobox.atari800;

import com.tvi910.android.core.PreferenceActivityUpdateSummary;
import xtvapps.retrobox.atari800.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class Droid800Preferences extends PreferenceActivityUpdateSummary {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.xml.preferences);
    }

}
