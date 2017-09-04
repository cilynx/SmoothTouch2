package com.wolfteck.smoothtouch2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

public class DepthMap extends Fragment {

    private static int RESULT_LOAD_IMAGE = 1;

    public DepthMap() {
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
        return inflater.inflate(R.layout.depth_map, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView realWidthView = (TextView) view.findViewById(R.id.dmImageWidth);
        realWidthView.addTextChangedListener(new TextWatcher() {
            Float oldWidth;
            Float newWidth;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(realWidthView.getText().length() > 0) {
                    oldWidth = Float.parseFloat(realWidthView.getText().toString());
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(realWidthView.hasFocus() && null != oldWidth && realWidthView.getText().length() > 0) {
                    newWidth = Float.parseFloat(realWidthView.getText().toString());
                    TextView realHeightTV = (TextView) view.findViewById(R.id.dmImageHeight);
                    Float oldHeight = Float.parseFloat(realHeightTV.getText().toString());
                    Float newHeight = oldHeight / oldWidth * newWidth;
                    realHeightTV.setText(newHeight.toString());
                }
            }
        });

        final TextView realHeightView = (TextView) view.findViewById(R.id.dmImageHeight);
        realHeightView.addTextChangedListener(new TextWatcher() {
            Float oldHeight;
            Float newHeight;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(realHeightView.getText().length() > 0) {
                    oldHeight = Float.parseFloat(realHeightView.getText().toString());
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(realHeightView.hasFocus() && null != oldHeight && realHeightView.getText().length() > 0) {
                    newHeight = Float.parseFloat(realHeightView.getText().toString());
                    TextView realWidthTV = (TextView) view.findViewById(R.id.dmImageWidth);
                    Float oldWidth = Float.parseFloat(realWidthTV.getText().toString());
                    Float newWidth = oldWidth / oldHeight * newHeight;
                    realWidthTV.setText(newWidth.toString());
                }
            }
        });

        Button loadPicture = (Button) view.findViewById(R.id.buttonLoadPicture);
        loadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        Button printPicture = (Button) view.findViewById(R.id.buttonRunGcode);
        printPicture.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 ImageView imageView = (ImageView) getView().findViewById(R.id.dmImageView);
                 Bitmap originalBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                 int originalPixHeight = originalBitmap.getHeight();
                 int originalPixWidth = originalBitmap.getWidth();

                 Float spotSize = Float.parseFloat(((TextView) getView().findViewById(R.id.dmSpot)).getText().toString());

                 Float originalRealHeight = originalPixHeight * spotSize;

                 Float desiredRealHeight = Float.parseFloat(((TextView) getView().findViewById(R.id.dmImageHeight)).getText().toString());

                 Float scaleFactor = desiredRealHeight / originalRealHeight;

                 int scaledPixHeight = (int) (originalPixHeight * scaleFactor);
                 int scaledPixWidth = (int) (originalPixWidth * scaleFactor);

                 Bitmap imgBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledPixWidth, scaledPixHeight, false);

                 StringBuilder gcode = new StringBuilder();
                 gcode.append("G90 ; Absolute mode\nG21 ; Metric mode\nM03 ; Arm laser\nM17 ; Steppers on\n");

                 for(int y = 0; y < scaledPixHeight; y++) {
                     if(y % 2 == 0) {
                         gcode.append("\nG0 X0 Y" + (scaledPixHeight - y - 1)*spotSize + "\n\n");
                         for(int x = 0; x < scaledPixWidth; x++) {
                             int color = imgBitmap.getPixel(x, y);
                             int brightness = (int) (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color));
                             gcode.append("G1 X" + (x + 1)*spotSize + " Y" + (scaledPixHeight - y - 1)*spotSize + " S" + (1.0 - brightness / 255.0) + "\n");
                         }
                     } else {
                         gcode.append("\nG0 X" + scaledPixWidth*spotSize + " Y" + (scaledPixHeight - y - 1)*spotSize + "\n\n");
                         for(int x = scaledPixWidth - 1; x >= 0; x--) {
                             int color = imgBitmap.getPixel(x, y);
                             int brightness = (int) (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color));
                             gcode.append("G1 X" + x*spotSize + " Y" + (scaledPixHeight - y - 1)*spotSize + " S" + (1.0 - brightness / 255.0) + "\n");
                         }

                     }
                 }

                 gcode.append("\nM05 ; Disarm laser\nG0 X0 Y0 ; Go home\nM18 ; Steppers off");

                 final Interface mSmoothie = Interface.getInstance(getActivity());
                 mSmoothie.playGcode(gcode.toString());
             }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Toast.makeText(getContext(), selectedImage.toString(), Toast.LENGTH_LONG).show();

            Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            Bitmap imgBitmap = BitmapFactory.decodeFile(cursor.getString(columnIndex));
            cursor.close();

            ImageView imageView = (ImageView) getView().findViewById(R.id.dmImageView);
            imageView.setImageBitmap(imgBitmap);

            Float spotSize = Float.parseFloat(((TextView) getView().findViewById(R.id.dmSpot)).getText().toString());

            Float realHeight = imgBitmap.getHeight() * spotSize;
            Float realWidth = imgBitmap.getWidth() * spotSize;

            ((TextView) getView().findViewById(R.id.dmImageHeight)).setText(realHeight.toString());
            ((TextView) getView().findViewById(R.id.dmImageWidth)).setText(realWidth.toString());
        }
    }

}