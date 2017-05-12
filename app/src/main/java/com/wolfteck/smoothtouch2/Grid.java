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

public class Grid extends Fragment {
    public Grid() {
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
        return inflater.inflate(R.layout.grid, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.grid_dro_container, new DRO()).commit();

        final Interface mSmoothie = Interface.getInstance(getActivity());

        ToggleButton halt = (ToggleButton) view.findViewById(R.id.grid_halt);
        halt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSmoothie.halt();
                } else {
                    mSmoothie.reset();
                }
            }
        });

        Button setHoleDepth = (Button) view.findViewById(R.id.set_grid_depth);
        setHoleDepth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView surfaceDepth = (TextView) view.findViewById(R.id.grid_depth);
                surfaceDepth.setText(Double.toString(mSmoothie.getMachineDRO()[2]));
            }
        });

        Button setSurfaceDepth = (Button) view.findViewById(R.id.set_grid_safe);
        setSurfaceDepth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView surfaceDepth = (TextView) view.findViewById(R.id.grid_safe);
                surfaceDepth.setText(Double.toString(mSmoothie.getMachineDRO()[2]));
            }
        });

        Button setFirst = (Button) view.findViewById(R.id.set_grid_first);
        setFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView center_x = (TextView) view.findViewById(R.id.grid_first_x);
                center_x.setText(Double.toString(mSmoothie.getMachineDRO()[0]));

                TextView center_y = (TextView) view.findViewById(R.id.grid_first_y);
                center_y.setText(Double.toString(mSmoothie.getMachineDRO()[1]));

                TextView safeDepth = (TextView) view.findViewById(R.id.grid_safe);
                safeDepth.setText(Double.toString(mSmoothie.getMachineDRO()[2] + 2));
            }
        });

        Button drillSomeHoles = (Button) view.findViewById(R.id.grid_go);
        drillSomeHoles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv;

                tv = (TextView) view.findViewById(R.id.grid_safe);
                double safeDepth = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.grid_depth);
                double holeDepth = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.grid_A);
                double angle = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.grid_dx);
                double dX = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.grid_dy);
                double dY = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.grid_first_x);
                double firstX = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.grid_first_y);
                double firstY = Double.parseDouble(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.grid_rows);
                int rows = Integer.parseInt(tv.getText().toString());
                tv = (TextView) view.findViewById(R.id.grid_cols);
                int cols = Integer.parseInt(tv.getText().toString());

                StringBuilder message = new StringBuilder();

                if(safeDepth <= holeDepth) {
                    message.append("Hole Depth must be deeper than Safe Depth.\n");
                }

                if(rows < 1) {
                    message.append("Need at least one row.");
                }

                if(cols < 1) {
                    message.append("Need at least one col.");
                }

                if(message.toString().isEmpty()) {

                    final StringBuilder gcode = new StringBuilder();

                    gcode.append("G90; Absolute mode\nG21; Millimeter mode\nM17; Enable steppers\n");
                    gcode.append("G0 Z").append(safeDepth).append("; Go to safe depth\n");
                    gcode.append("M3; Start the spindle\n");

                    double first_x;
                    double first_y;
                    int row_index = 0;
                    int col_index = 0;
                    boolean col_right = true;

                    while(row_index < rows) {
                        first_x = firstX + row_index * dX * Math.cos(Math.toRadians(angle + 90));
                        first_y = firstY + row_index * dY * Math.sin(Math.toRadians(angle + 90));

                        while (0 <= col_index && col_index < cols) {

                            gcode.append("G0 X").append(first_x + col_index * dX * Math.cos(Math.toRadians(angle)));
                            gcode.append(" Y").append(first_y + col_index * dY * Math.sin(Math.toRadians(angle)));
                            gcode.append("; Hole ").append(row_index).append(",").append(col_index).append("\n");
                            gcode.append("G1 Z").append(holeDepth).append("; Drill\n");
                            gcode.append("G1 Z").append(safeDepth).append("; Retract\n");
                            if(col_right) { col_index++; } else { col_index--; }
                        }

                        if(col_right) { col_index--; } else { col_index++; }
                        col_right = !col_right;
                        row_index++;
                    }

                    gcode.append("M5; Stop the spindle\n");
                    gcode.append("G0 X").append(firstX).append(" Y").append(firstY).append("; Go to center\n");
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
