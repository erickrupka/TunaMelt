package org.krupkas.tunamelt;


import android.os.Bundle;
import android.preference.PreferenceFragment;

public class UserPrefsFrag extends PreferenceFragment {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     addPreferencesFromResource(R.xml.userpreferences);
    }
}