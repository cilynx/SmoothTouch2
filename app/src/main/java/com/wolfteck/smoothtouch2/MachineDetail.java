package com.wolfteck.smoothtouch2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;


public class MachineDetail extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

      //  SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(sharedPref.getString("currentMachine", "Mill"));
        manager.setSharedPreferencesMode(Context.MODE_PRIVATE);
        sharedPref = manager.getSharedPreferences();

        setPreferencesFromResource(R.xml.machine_detail, rootKey);

        onSharedPreferenceChanged(sharedPref, "machine_type");
        onSharedPreferenceChanged(sharedPref, "hostname");
        onSharedPreferenceChanged(sharedPref, "x_velocity");
        onSharedPreferenceChanged(sharedPref, "y_velocity");
        onSharedPreferenceChanged(sharedPref, "z_velocity");
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if(preference instanceof CheckBoxPreference) {
            // The only CheckBox is "Connect"
            Interface mInterface = Interface.getInstance(getContext());
        } else {
            preference.setSummary(sharedPreferences.getString(key, ""));
        }
    }

    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
