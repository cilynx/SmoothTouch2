package com.wolfteck.smoothtouch2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Surface extends Fragment {

    public Surface() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.surface, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.surface_dro_container, new DRO()).commit();

        final Interface mSmoothie = Interface.getInstance(getActivity());

        ToggleButton halt = (ToggleButton) view.findViewById(R.id.halt);
        halt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSmoothie.halt();
                } else {
                    mSmoothie.reset();
                }
            }
        });

        Button setUpperLeft = (Button) view.findViewById(R.id.set_upper_left);
        setUpperLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ul_x = (TextView) view.findViewById(R.id.upper_left_x);
                ul_x.setText(Double.toString(mSmoothie.getMachineDRO()[0]));

                TextView ul_y = (TextView) view.findViewById(R.id.upper_left_y);
                ul_y.setText(Double.toString(mSmoothie.getMachineDRO()[1]));

                TextView startDepth = (TextView) view.findViewById(R.id.start_depth);
                startDepth.setText(Double.toString(mSmoothie.getMachineDRO()[2]));
            }
        });

        Button setLowerRight = (Button) view.findViewById(R.id.set_lower_right);
        setLowerRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ul_x = (TextView) view.findViewById(R.id.lower_right_x);
                ul_x.setText(Double.toString(mSmoothie.getMachineDRO()[0]));

                TextView ul_y = (TextView) view.findViewById(R.id.lower_right_y);
                ul_y.setText(Double.toString(mSmoothie.getMachineDRO()[1]));

                TextView startDepth = (TextView) view.findViewById(R.id.end_depth);
                startDepth.setText(Double.toString(mSmoothie.getMachineDRO()[2]));
            }
        });

        Button setStartDepth = (Button) view.findViewById(R.id.set_start_depth);
        setStartDepth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView sd = (TextView) view.findViewById(R.id.start_depth);
                sd.setText(Double.toString(mSmoothie.getMachineDRO()[2]));
            }
        });

        Button setEndDepth = (Button) view.findViewById(R.id.set_end_depth);
        setEndDepth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView sd = (TextView) view.findViewById(R.id.end_depth);
                sd.setText(Double.toString(mSmoothie.getMachineDRO()[2]));
            }
        });

        ToggleButton tool = (ToggleButton) view.findViewById(R.id.tool_unit);
        tool.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                TextView tv = (TextView) view.findViewById(R.id.tool_diameter);
                String text = tv.getText().toString();
                if(!text.isEmpty()) {
                    double value = Double.parseDouble(text);

                    if (isChecked) {
                        tv.setText(String.format("%.4f", value / 25.4));
                    } else {
                        tv.setText(String.format("%.4f", value * 25.4));
                    }
                }
            }
        });

        ToggleButton pass = (ToggleButton) view.findViewById(R.id.pass_unit);
        pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                TextView tv = (TextView) view.findViewById(R.id.pass_depth);
                String text = tv.getText().toString();
                if(!text.isEmpty()) {
                    double value = Double.parseDouble(text);

                    if (isChecked) {
                        tv.setText(String.format("%.4f", value / 25.4));
                    } else {
                        tv.setText(String.format("%.4f", value * 25.4));
                    }
                }
            }
        });

    }
 }