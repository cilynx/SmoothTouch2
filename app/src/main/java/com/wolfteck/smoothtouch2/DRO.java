package com.wolfteck.smoothtouch2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

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

        final Interface mSmoothie = Interface.getInstance(getActivity());

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

        Button jogXP = (Button) view.findViewById(R.id.jog_xp);
        jogXP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothie.jogX(mJogScale);
            }
        });

        Button jogXN = (Button) view.findViewById(R.id.jog_xn);
        jogXN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothie.jogX(-mJogScale);
            }
        });

        Button jogYP = (Button) view.findViewById(R.id.jog_yp);
        jogYP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothie.jogY(mJogScale);
            }
        });

        Button jogYN = (Button) view.findViewById(R.id.jog_yn);
        jogYN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothie.jogY(-mJogScale);
            }
        });

        Button jogZP = (Button) view.findViewById(R.id.jog_zp);
        jogZP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothie.jogZ(mJogScale);
            }
        });

        Button jogZN = (Button) view.findViewById(R.id.jog_zn);
        jogZN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothie.jogZ(-mJogScale);
            }
        });

        final ToggleButton relative = (ToggleButton) view.findViewById(R.id.relative_toggle);
        relative.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSmoothie.setWorkspaceDRO();
                } else {
                    mSmoothie.setMachineDRO();
                }
            }
        });

        Button zero = (Button) view.findViewById(R.id.zero);
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothie.zeroWorkspaceDRO();
                relative.setChecked(true);
            }
        });

        return view;
    }
}
