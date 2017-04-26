package com.khantilchoksi.j2eehealthcaredoctor.ArztAsyncCalls;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.khantilchoksi.j2eehealthcaredoctor.R;

import org.json.JSONArray;
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
import java.util.ArrayList;


/**
 * Created by Khantil on 22-03-2017.
 */

public class GetDoctorMainSpecialitiesTask extends AsyncTask<Void, Void, Boolean> {

    private static final String LOG_TAG = GetDoctorMainSpecialitiesTask.class.getSimpleName();
    Context context;
    Activity activity;
    ArrayList<String> specialityNameList;
    ArrayList<String> specialityDescriptionList;
    ArrayList<String> specialityIconUrlList;
    ProgressDialog progressDialog;

    public interface AsyncResponse {
        void processFinish(ArrayList<String> specialityNameList, ArrayList<String> specialityDescriptionList, ArrayList<String> specialityIconUrlList, ProgressDialog progressDialog);
    }

    public AsyncResponse delegate = null;

    public GetDoctorMainSpecialitiesTask(Context context, Activity activity, AsyncResponse delegate, ProgressDialog progressDialog){
        this.context = context;
        this.activity = activity;
        this.delegate = delegate;
        this.progressDialog = progressDialog;
        specialityNameList = new ArrayList<String>();
        specialityDescriptionList = new ArrayList<String>();
        specialityIconUrlList = new ArrayList<String>();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String clientCredStr;

        try {

            final String CLIENT_BASE_URL = context.getResources().getString(R.string.base_url).concat("doctorSpecialities");
            URL url = new URL(CLIENT_BASE_URL);


            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


            /*Uri.Builder builder = new Uri.Builder();
            Map<String, String> parameters = new HashMap<>();
            parameters.put("pid", String.valueOf(Utility.getDoctorId(context)));

            // encode parameters
            Iterator entries = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                entries.remove();
            }
            String requestBody = builder.build().getEncodedQuery();*/
            Log.d(LOG_TAG, "Service Call URL : " + CLIENT_BASE_URL);
            //Log.d(LOG_TAG, "Post parameters : " + requestBody);

            //OutputStream os = urlConnection.getOutputStream();
            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            //writer.write(requestBody);    //bcz no parameters to be sent

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

            Log.d(LOG_TAG, "Doctor Main Specialities Credential JSON String : " + clientCredStr);


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

            delegate.processFinish(specialityNameList,specialityDescriptionList,specialityIconUrlList,progressDialog);

        } else {

            progressDialog.dismiss();


                /*Snackbar.make(, R.string.error_unknown_error,
                        Snackbar.LENGTH_LONG)
                        .show();*/
            Toast.makeText(context,context.getResources().getString(R.string.error_unknown_error),Toast.LENGTH_SHORT).show();

        }
    }

    private boolean isSuccessfullyUpdate(String clientCredStr) throws JSONException {

        final String specialityListString = "specialityList";
        final String specialityNameString = "specialityName";
        final String specialityDescriptionString = "specialityDescription";
        final String specialityIconString = "specialityIcon";
        final String iconPrePathString = "prePath";

        String tempName;
        String tempDescription;
        String tempUrl;


        JSONObject clientJson = new JSONObject(clientCredStr);
        String prePath = clientJson.getString(iconPrePathString);
        JSONArray specialityJsonArray = clientJson.getJSONArray(specialityListString);

        for(int i=0;i<specialityJsonArray.length();i++){
            JSONObject specilaityJSONObject = specialityJsonArray.getJSONObject(i);
            tempName = specilaityJSONObject.getString(specialityNameString);
            tempDescription = specilaityJSONObject.getString(specialityDescriptionString);
            tempUrl = prePath.
                    concat(specilaityJSONObject.getString(specialityIconString));

            Log.d(LOG_TAG,"Speciality: "+tempName+" Des: "+tempDescription+" Url: "+tempUrl);
            specialityNameList.add(tempName);
            specialityDescriptionList.add(tempDescription);
            specialityIconUrlList.add(tempUrl);
        }



        return true;
    }


}
