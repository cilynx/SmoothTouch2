package com.wolfteck.smoothtouch2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

        final ToggleButton toolUnit = (ToggleButton) view.findViewById(R.id.tool_unit);
        toolUnit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        ToggleButton passUnit = (ToggleButton) view.findViewById(R.id.pass_unit);
        passUnit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        Button surfaceThePart = (Button) view.findViewById(R.id.surface_the_part);
        surfaceThePart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv;

                tv = (TextView) view.findViewById(R.id.upper_left_x);
                double ulx = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.upper_left_y);
                double uly = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.lower_right_x);
                double lrx = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.lower_right_y);
                double lry = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.start_depth);
                double sd = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.end_depth);
                double ed = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.tool_diameter);
                double td = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.pass_depth);
                double dpp = Double.parseDouble(tv.getText().toString());

                StringBuilder message = new StringBuilder();

                if(!(ed < sd)) {
                    message.append("End Depth must be deeper than Start Depth.\n");
                }

                if(!(ulx < lrx)) {
                    message.append("Upper Left must be to the left of Lower Right.\n");
                }

                if(!(uly > lry)) {
                    message.append("Upper Left must be higher than Lower Right.\n");
                }

                if(message.toString().isEmpty()) {

                    if(toolUnit.isChecked()) { td *= 25.4; }
                    double shift = (uly-lry)/(Math.ceil(((uly-lry)-td)/td)+1);
                    double y;
                    double z = sd;
                    int count;

                    final StringBuilder gcode = new StringBuilder();
                    gcode.append("G90 G21\nM17\n");
                    gcode.append("G0 Z").append(sd+10).append("\n");
                    gcode.append("G0 X").append(ulx).append(" Y").append(uly).append("\n");
                    gcode.append("M3\n");

                    while(z >= ed) {
                        gcode.append("G1 Z").append(z).append("\n");
                        count = 0;
                        y = uly;
                        while (y >= lry) {
                            gcode.append("G1 Y").append(y).append("\n");
                            gcode.append("G1 X");
                            if (count % 2 == 0) {
                                gcode.append(lrx);
                            } else {
                                gcode.append(ulx);
                            }
                            gcode.append("\n");
                            y -= shift;
                            count++;
                        }
                        if(z == ed) { break; }
                        z -= dpp;
                        if(z < ed) { z = ed; }
                        gcode.append("G0 Z").append(sd).append("\n");
                        gcode.append("G0 X").append(ulx).append(" Y").append(uly).append("\n");
                    }

                    gcode.append("M5\n");
                    gcode.append("G0 Z").append(sd).append("\n");
                    gcode.append("G0 X").append(ulx).append(" Y").append(uly).append("\n");
                    gcode.append("M18\n");

                    mSmoothie.playGcode(gcode.toString());

                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Invalid Parameters!")
                            .setMessage(message.toString())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            }
        });

    }
 }