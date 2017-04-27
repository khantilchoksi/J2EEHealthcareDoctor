package com.khantilchoksi.j2eehealthcaredoctor.ArztAsyncCalls;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.khantilchoksi.j2eehealthcaredoctor.HomeActivity;
import com.khantilchoksi.j2eehealthcaredoctor.R;
import com.khantilchoksi.j2eehealthcaredoctor.Slot;
import com.khantilchoksi.j2eehealthcaredoctor.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by Khantil on 22-03-2017.
 */

public class InsertClinicTimeSlotTask extends AsyncTask<Void, Void, Boolean> {

    public static void setSetTaskCounts(int setTaskCounts) {
        InsertClinicTimeSlotTask.setTaskCounts = setTaskCounts;
    }

    public static int setTaskCounts = 0;

    public static void setCurrentTaskCount(int currentTaskCount) {
        InsertClinicTimeSlotTask.currentTaskCount = currentTaskCount;
    }

    private static int currentTaskCount = 0;
    private static final String LOG_TAG = InsertClinicTimeSlotTask.class.getSimpleName();
    Context context;
    Activity activity;
    Slot mSlot;
    String mClinicId;
    int mFees;
    int mMaxPatients;
    String issue;
    ProgressDialog progressDialog;




    public InsertClinicTimeSlotTask(Context context, Activity activity, String clinicId, Slot slot, int fees,int maxPatients,ProgressDialog progressDialog){
        this.context = context;
        this.activity = activity;
        this.mClinicId = clinicId;
        this.mSlot = slot;
        this.mFees = fees;
        this.mMaxPatients = maxPatients;
        this.progressDialog = progressDialog;
        issue = context.getResources().getString(R.string.error_unknown_error);

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String clientCredStr;

        try {

            final String CLIENT_BASE_URL = context.getResources().getString(R.string.base_url).concat("insertClinicTimeSlot");
            URL url = new URL(CLIENT_BASE_URL);


            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


            Uri.Builder builder = new Uri.Builder();
            Map<String, String> parameters = new HashMap<>();
            parameters.put("authKey", "avk");
            parameters.put("clinicId", mClinicId);
            parameters.put("doctorId", String.valueOf(Utility.getDoctorId(context)));
            parameters.put("day",mSlot.getDay());
            parameters.put("startTime", mSlot.getStartTime());
            parameters.put("endTime",mSlot.getEndTime());
            parameters.put("fees",String.valueOf(mFees));
            parameters.put("maxPatients",String.valueOf(mMaxPatients));

            // encode parameters
            Iterator entries = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                entries.remove();
            }
            String requestBody = builder.build().getEncodedQuery();
            Log.d(LOG_TAG, "Service Call URL : " + CLIENT_BASE_URL);
            Log.d(LOG_TAG, "Post parameters : " + requestBody);

            //OutputStream os = urlConnection.getOutputStream();
            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(requestBody);

            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect();

            // Read the input stream into a String
            //InputStream inputStream = urlConnection.getInputStream();
            InputStream inputStream;
            int status = urlConnection.getResponseCode();
            Log.d(LOG_TAG, "URL Connection Response Code " + status);

            //if(status >= 400)
            //  inputStream = urlConnection.getErrorStream();
            //else
            inputStream = urlConnection.getInputStream();


            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return false;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return false;
            }

            clientCredStr = buffer.toString();

            Log.d(LOG_TAG, "Insert Clinic Time Slot Credential JSON String : " + clientCredStr);


            return isSuccessfullyUpdate(clientCredStr);

        } catch (IOException e) {
            Log.d(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return false;
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.d(LOG_TAG, "Error closing stream", e);
                }
            }
            //return false;
        }
    }

    @Override
    protected void onCancelled() {
        //progressDialog.dismiss();
    }

    @Override
    protected void onPostExecute(Boolean success) {
        Log.d(LOG_TAG, "Success Boolean Tag: " + success.toString());
        if (success) {
            //do nothing
            currentTaskCount++;
            Log.d(LOG_TAG,"CurrentTaskCount : "+currentTaskCount+" SetTaskCount:"+setTaskCounts);
            if(currentTaskCount == setTaskCounts){
                //all done
                //go to next activity
                progressDialog.dismiss();

                Intent intent = new Intent(activity,HomeActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }

        } else {

            progressDialog.dismiss();


            Toast.makeText(context,issue,Toast.LENGTH_LONG).show();

            Intent intent = new Intent(activity,HomeActivity.class);
            activity.startActivity(intent);
            activity.finish();

        }
    }

    private boolean isSuccessfullyUpdate(String clientCredStr) throws JSONException {

        final String successfullyAddedString = "successfullyAdded";
        final String errorMessageString = "errorMessage";

        JSONObject clientJson = new JSONObject(clientCredStr);
        String successful = clientJson.getString(successfullyAddedString);
        if(successful.contains("true")){
            return true;
        }else {
            issue = clientJson.getString(errorMessageString);
            return false;
        }

    }


}
