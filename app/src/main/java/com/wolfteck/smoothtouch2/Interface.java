package com.wolfteck.smoothtouch2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interface {
    private static Interface mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private static SharedPreferences prefs;
    private static String mSendPlayDelete;
    private static String mCommandURL;
    private static String mUploadURL;
    private static StringRequest mRequestDRO;
    private static StringRequest mRequest;
    private static TextView[] mDroViews;

    private static double mMachineX = 0;
    private static double mMachineY = 0;
    private static double mMachineZ = 0;

    private static double mOffsetX = 0;
    private static double mOffsetY = 0;
    private static double mOffsetZ = 0;

    private static boolean mRunDRO = false;
    private static boolean mWorkspaceDRO = false;
    private static boolean mIsPlaying = false;

    private static ProgressDialog mProgressDialog;

    private Interface(Context context) {
        mCtx = context;

        prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String currentMachine = prefs.getString("currentMachine", "Mill");
        prefs = context.getSharedPreferences(currentMachine, Context.MODE_PRIVATE);

        mCommandURL = "http://" + prefs.getString("hostname", "smoothie") + "/command";
        mUploadURL = "http://" + prefs.getString("hostname", "smoothie") + "/upload";

        Toast.makeText(context, mCommandURL, Toast.LENGTH_LONG).show();

        mRequestQueue = getRequestQueue();
    }

    public static synchronized Interface getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new Interface(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void startDRO(final TextView[] droViews) {
        mDroViews = droViews;

        mRequestDRO = new StringRequest(Request.Method.POST, mCommandURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String[] parts = response.split(" ");

                mMachineX = Double.valueOf(parts[2].split(":")[1]);
                mMachineY = Double.valueOf(parts[3].split(":")[1]);
                mMachineZ = Double.valueOf(parts[4].split(":")[1]);

                mDroViews[0].setText(Double.toString(getDRO()[0]));
                mDroViews[1].setText(Double.toString(getDRO()[1]));
                mDroViews[2].setText(Double.toString(getDRO()[2]));

                if(mRunDRO) { addToRequestQueue(mRequestDRO); }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //SystemClock.sleep(5000);
            }
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                Log.d("getBody()","Fetching DRO");
                String cmd = "M114.1\n";
                return(cmd.getBytes());
            }
        };

        if(!mRunDRO) {
            mRunDRO = true;
            addToRequestQueue(mRequestDRO);
            Toast.makeText(mCtx, "DRO was off.  Starting.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mCtx, "DRO is already on.  Redirecting.", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopDRO() {
        mRunDRO = false;
    }

    public void zeroWorkspaceDRO() {
        mOffsetX = mMachineX;
        mOffsetY = mMachineY;
        mOffsetZ = mMachineZ;
        setWorkspaceDRO();
    }

    public void setWorkspaceDRO() {
        mWorkspaceDRO = true;
    }

    public void setMachineDRO() {
        mWorkspaceDRO = false;
    }

    public double[] getMachineDRO() {
        return new double[] {mMachineX, mMachineY, mMachineZ};
    }

    public double[] getWorkspaceDRO() {
        return new double[] {mMachineX - mOffsetX, mMachineY - mOffsetY, mMachineZ - mOffsetZ};
    }

    public double[] getDRO() {
        if(mWorkspaceDRO) {
            return(getWorkspaceDRO());
        } else {
            return(getMachineDRO());
        }
    }

    public void jogX(double length) {
        sendCommand("G91 G0 X" + length + " F" + prefs.getString("x_velocity","0") + " G90");
    }

    public void jogY(double length) {
        sendCommand("G91 G0 Y" + length + " F" + prefs.getString("y_velocity","0") + " G90");
    }

    public void jogZ(double length) {
        sendCommand("G91 G0 Z" + length + " F" + prefs.getString("z_velocity","0") + " G90");
    }

    public void halt() {
        sendCommand("M112");
    }

    public void reset() {
        sendCommand("M999");
    }

    public void selectFile(final String filename) {
        sendCommand("M23 " + filename);
    }

    public void playGcode(final String gcode, Context activityContext) {
        mSendPlayDelete = Long.toString(System.currentTimeMillis()) + ".g";
        sendFile(gcode, mSendPlayDelete, activityContext);
    }

    public void playFile(final String filename, Context activityContext) {
        Toast.makeText(mCtx, "Playing " + filename, Toast.LENGTH_SHORT).show();
        mIsPlaying = true;

        mProgressDialog = new ProgressDialog(activityContext);
        mProgressDialog.setMessage("Transferring File...");
        mProgressDialog.setTitle("Playing G-Code");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();

        sendCommand("M32 " + filename);
    }

    public void deleteFile(final String filename) {
        Toast.makeText(mCtx, "Deleting " + filename, Toast.LENGTH_SHORT).show();
        mSendPlayDelete = "";
        sendCommand("M30 " + filename);
    }

    public void sendFile(final String gcode, final String filename, final Context activityContext) {

        mProgressDialog = new ProgressDialog(activityContext);
        mProgressDialog.setMessage("Transferring File...");
        mProgressDialog.setTitle("Playing G-Code");
        mProgressDialog.show();

        Toast.makeText(mCtx, "Sending " + filename, Toast.LENGTH_SHORT).show();

        StringRequest uploadRequest = new StringRequest(Request.Method.POST, mUploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(mCtx, response, Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                if(mSendPlayDelete.equals(filename)) { playFile(mSendPlayDelete, activityContext); }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mCtx, error.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        }
        ) {
            // Put the Gcode in the POST body
            @Override
            public byte[] getBody() throws AuthFailureError {
                String fullCode = "M17 ; Enable Steppers\nG91 ; Relative Mode\nG X0 Y0 Z0 F" + prefs.getString("x_velocity", "200") + " ; Set Default Speed\nG90 ; Absolute mode" + gcode + "\nM18 ; Disable Steppers";
                return(fullCode.getBytes());
            }

            // Set the X-Filename header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d("uploadRequest","getHeaders()");
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-Filename", filename);
                return params;
            }
        };

        uploadRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        addToRequestQueue(uploadRequest);
    }

    private void sendCommand(final String command) {
        if(!command.equals("M27")) {
            Toast.makeText(mCtx, command, Toast.LENGTH_SHORT).show();
        }

        mRequest = new StringRequest(Request.Method.POST, mCommandURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.contains("Not currently playing")) {
                    mIsPlaying = false;
                    mProgressDialog.dismiss();
                }

                if(mIsPlaying) {
                    mProgressDialog.setMessage("Printing...");
//                    Toast.makeText(mCtx, response, Toast.LENGTH_SHORT).show();
                    if(response.contains("printing byte")) {
                        Pattern p = Pattern.compile("(\\d+)/(\\d+)");
                        Matcher m = p.matcher(response);
                        if(m.find()) {

                            mProgressDialog.setMax(Integer.parseInt(m.group(2)));
                            mProgressDialog.setProgress(Integer.parseInt(m.group(1)));

//                            Toast.makeText(mCtx, m.group(1) + " " + m.group(2), Toast.LENGTH_SHORT).show();
                        }
                    }
                    sendCommand("M27");
                } else if(mSendPlayDelete != null && !mSendPlayDelete.isEmpty()) {
                    deleteFile(mSendPlayDelete);
                } else {
                    Toast.makeText(mCtx, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Should we retry forever or...?  Maybe a user preference?
                Toast.makeText(mCtx, error.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
                if(command.equals("M27")) { sendCommand("M27"); }
            }
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                Log.d("getBody",command);
                return(command.concat("\n").getBytes());
            }
        };

        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,  // 60 second timeout
                0,      // Don't retry.  DefaultRetryPolicy.DEFAULT_MAX_RETRIES == 1
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // 1f
        ));

        addToRequestQueue(mRequest);
    }
}
