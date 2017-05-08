package com.wolfteck.smoothtouch2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.util.ArraySet;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Set;

public class MachineList extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArraySet<String> defaultMachines =  new ArraySet<String>(Arrays.asList("Mill", "Lathe", "Laser", "3D Printer"));
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        Set<String> machines = sharedPref.getStringSet("machines", defaultMachines);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, machines.toArray(new String[machines.size()]));
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView machineList, View machineItem, int position, long id) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("currentMachine", (String) machineList.getItemAtPosition(position));
        editor.apply();
        getFragmentManager().beginTransaction().replace(R.id.machine_detail, new MachineDetail()).commit();
    }
}
