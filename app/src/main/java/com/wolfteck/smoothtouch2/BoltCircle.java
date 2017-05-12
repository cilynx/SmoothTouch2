package com.wolfteck.smoothtouch2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class BoltCircle extends Fragment {

    public BoltCircle() {
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
        return inflater.inflate(R.layout.bolt_circle, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.bolt_circle_dro_container, new DRO()).commit();

        final Interface mSmoothie = Interface.getInstance(getActivity());

        Button setHoleDepth = (Button) view.findViewById(R.id.set_bc_depth);
        setHoleDepth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView surfaceDepth = (TextView) view.findViewById(R.id.bc_depth);
                surfaceDepth.setText(Double.toString(mSmoothie.getMachineDRO()[2]));
            }
        });

        Button setSurfaceDepth = (Button) view.findViewById(R.id.set_bc_surface);
        setSurfaceDepth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView surfaceDepth = (TextView) view.findViewById(R.id.bc_surface);
                surfaceDepth.setText(Double.toString(mSmoothie.getMachineDRO()[2]));
            }
        });

        Button setCenter = (Button) view.findViewById(R.id.set_bc_center);
        setCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView center_x = (TextView) view.findViewById(R.id.bc_center_x);
                center_x.setText(Double.toString(mSmoothie.getMachineDRO()[0]));

                TextView center_y = (TextView) view.findViewById(R.id.bc_center_y);
                center_y.setText(Double.toString(mSmoothie.getMachineDRO()[1]));

                TextView surfaceDepth = (TextView) view.findViewById(R.id.bc_surface);
                surfaceDepth.setText(Double.toString(mSmoothie.getMachineDRO()[2]));
            }
        });

        Button drillSomeHoles = (Button) view.findViewById(R.id.bc_go);
        drillSomeHoles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv;

                tv = (TextView) view.findViewById(R.id.bc_surface);
                double surfaceDepth = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.bc_depth);
                double holeDepth = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.bc_A);
                double firstAngle = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.bc_N);
                double holeCount = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.bc_D);
                double diameter = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.bc_center_x);
                double centerX = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.bc_center_y);
                double centerY = Double.parseDouble(tv.getText().toString());

                StringBuilder message = new StringBuilder();

                if(surfaceDepth <= holeDepth) {
                    message.append("Hole Depth must be deeper than Surface Depth.\n");
                }

                if(holeCount < 1) {
                    message.append("Number of Holes must be at least one.");
                }

                if(diameter <= 0) {
                    message.append("Diameter must be greater than zero.");
                }

                if(0 > firstAngle || firstAngle > 360) {
                    message.append("First Angle must be between 0 and 360 inclusive.");
                }

                if(message.toString().isEmpty()) {
                    double safeDepth = surfaceDepth + 2;
                    double radius = diameter / 2;
                    double theta = firstAngle;
                    double delta_theta = 360 / holeCount;

                    final StringBuilder gcode = new StringBuilder();

                    gcode.append("G90; Absolute mode\nG21; Millimeter mode\nM17; Enable steppers\n");
                    gcode.append("G0 Z").append(safeDepth).append("; Go to safe depth\n");
                    gcode.append("M3; Start the spindle\n");

                    int count = 1;
                    while(theta - 360 < firstAngle) {
                        gcode.append("G0 X").append(centerX + (radius * Math.cos(Math.toRadians(theta))));
                        gcode.append(" Y").append(centerY + (radius * Math.sin(Math.toRadians(theta))));
                        gcode.append("; Hole ").append(count++).append("\n");
                        gcode.append("G1 Z").append(holeDepth).append("; Drill\n");
                        gcode.append("G1 Z").append(safeDepth).append("; Retract\n");
                        theta += delta_theta;
                    }

                    gcode.append("M5; Stop the spindle\n");
                    gcode.append("G0 X").append(centerX).append(" Y").append(centerY).append("; Go to center\n");
                    gcode.append("M18; Disable steppers\n");

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
