package com.khantilchoksi.arztdoctor.ArztAsyncCalls;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.khantilchoksi.arztdoctor.HomeActivity;
import com.khantilchoksi.arztdoctor.R;
import com.khantilchoksi.arztdoctor.Utility;

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

public class SavePatientProfileTask extends AsyncTask<Void, Void, Boolean> {

    private static final String LOG_TAG = SavePatientProfileTask.class.getSimpleName();
    Context context;
    Activity activity;
    String fullName;
    int gender;
    int bloodGroup;
    String birthdate;
    String emergencyMobileNumber;
    String latitude;
    String longitude;
    String fullAddress;
    String pincode;
    ProgressDialog progressDialog;

    public SavePatientProfileTask(Context context, Activity activity, String fullName, int gender, int bloodGroup, String birthdate,
                                  String emergencyMobileNumber,
                                  String latitude, String longitude, String fullAddress, String pincode, ProgressDialog progressDialog){
        this.context = context;
        this.activity = activity;
        this.fullName = fullName;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.birthdate = birthdate;
        this.emergencyMobileNumber = emergencyMobileNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fullAddress = fullAddress;
        this.pincode = pincode;
        this.progressDialog = progressDialog;


    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String clientCredStr;

        try {

            final String CLIENT_BASE_URL = context.getResources().getString(R.string.base_url).concat("setPatientProfileDetails");
            URL url = new URL(CLIENT_BASE_URL);


            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


            Uri.Builder builder = new Uri.Builder();
            Map<String, String> parameters = new HashMap<>();
            parameters.put("pid", String.valueOf(Utility.getPatientId(context)));
            parameters.put("fullName", fullName);
            parameters.put("gender", String.valueOf(gender));
            parameters.put("bloodGroup", String.valueOf(bloodGroup));
            parameters.put("birthdate", birthdate);
            parameters.put("emergencyMobileNumber", emergencyMobileNumber);
            parameters.put("latitude",latitude);
            parameters.put("longitude",longitude);
            parameters.put("fullAddress",fullAddress);
            parameters.put("pincode",pincode);

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

            Log.d(LOG_TAG, "Client Credential JSON String : " + clientCredStr);


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
        progressDialog.dismiss();
    }

    @Override
    protected void onPostExecute(Boolean success) {
        Log.d(LOG_TAG, "Success Boolean Tag: " + success.toString());
        if (success) {

            successfullyUpdated();

        } else {

            progressDialog.dismiss();


                /*Snackbar.make(, R.string.error_unknown_error,
                        Snackbar.LENGTH_LONG)
                        .show();*/
            Toast.makeText(context,context.getResources().getString(R.string.error_unknown_error),Toast.LENGTH_SHORT).show();

        }
    }

    private boolean isSuccessfullyUpdate(String clientCredStr) throws JSONException {

        final String successfullyUpdatedString = "successfullyUpdated";


        JSONObject clientJson = new JSONObject(clientCredStr);
        String isSuccessfullyUpdated = clientJson.getString(successfullyUpdatedString);
        if (isSuccessfullyUpdated.contains("true")) {
            //Profile details successfully created

            return true;
        } else {
            //Profile details not created successfully

        }

        return false;
    }

    public void successfullyUpdated(){
        Toast.makeText(context,context.getResources().getString(R.string.profile_updated),Toast.LENGTH_SHORT).show();
        Intent homeActivity = new Intent(activity, HomeActivity.class);
        activity.startActivity(homeActivity);
        activity.finish();
    }
}
