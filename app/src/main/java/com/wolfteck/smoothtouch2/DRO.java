package com.wolfteck.smoothtouch2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DRO extends Fragment {

    private double mJogScale = 1;

    public DRO() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dro, container, false);

        Button jogAmount = (Button) view.findViewById(R.id.jog_amount);
        jogAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if(mJogScale == 0.1) {
                    mJogScale = 100;
                    b.setText("10cm");
                } else if(mJogScale == 100) {
                    mJogScale = 10;
                    b.setText("1cm");
                } else if(mJogScale == 10) {
                    mJogScale = 1;
                    b.setText("1mm");
                } else if(mJogScale == 1) {
                    mJogScale = 0.1;
                    b.setText("0.1mm");
                }
            }
        });

        return view;
    }
}
