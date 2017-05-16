package com.wolfteck.smoothtouch2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayGcode extends Fragment {
    public PlayGcode() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.play_gcode, container, false);
    }

    private static String mFilename;

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Interface mSmoothie = Interface.getInstance(getActivity());

        Toast.makeText(getActivity(), "Getting file list...", Toast.LENGTH_LONG);

        mSmoothie.listFiles(new Interface.VolleyArrayCallback() {
            @Override
            public void onSuccess(ArrayList<String> files) {
                final TextView editGcode = (TextView) getView().findViewById(R.id.edit_gcode);
                editGcode.setText("");
                final ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, files.toArray(new String[files.size()]));
                ListView listView = (ListView) getView().findViewById(R.id.gcode_file_list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mFilename = (String) adapter.getItem(i);
                        editGcode.setTextSize(50);
                        editGcode.setText("Loading " + mFilename + " preview (up to 100 lines) ...");
                        mSmoothie.loadFile(mFilename, editGcode);
                        mSmoothie.selectFile(mFilename);
                    }
                });
            }
        });

        Button playFile = (Button) view.findViewById(R.id.playFile);
        playFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFilename == null) {
                    Toast.makeText(getActivity(), "Select a file to play!", Toast.LENGTH_SHORT).show();
                } else {
                    mSmoothie.playFile(mFilename);
                }
            }
        });

        Button deleteFile = (Button) view.findViewById(R.id.deleteFile);
        deleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFilename == null) {
                    Toast.makeText(getActivity(), "Select a file to delete!", Toast.LENGTH_SHORT).show();
                } else {
                    mSmoothie.deleteFile(mFilename);
                }
            }
        });

    }
}