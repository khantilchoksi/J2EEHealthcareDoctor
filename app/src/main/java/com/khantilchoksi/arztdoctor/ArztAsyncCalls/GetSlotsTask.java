package com.khantilchoksi.arztdoctor.ArztAsyncCalls;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.khantilchoksi.arztdoctor.R;
import com.khantilchoksi.arztdoctor.Slot;
import com.khantilchoksi.arztdoctor.Utility;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by Khantil on 22-03-2017.
 */

public class GetSlotsTask extends AsyncTask<Void, Void, Boolean> {

    private static final String LOG_TAG = GetSlotsTask.class.getSimpleName();
    Context context;
    String mClinicId;
    ArrayList<Slot> slotsList;


    public interface AsyncResponse {
        void processSlotFinish(ArrayList<Slot> slotsList);
    }

    public AsyncResponse delegate = null;

    public GetSlotsTask(String clinicId, Context context, AsyncResponse delegate){
        this.mClinicId = clinicId;
        this.context = context;
        this.delegate = delegate;
        slotsList = new ArrayList<Slot>();

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String clientCredStr;

        try {

            final String CLIENT_BASE_URL = context.getResources().getString(R.string.base_url).concat("getDoctorClinicSlotDetails");
            URL url = new URL(CLIENT_BASE_URL);


            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


            Uri.Builder builder = new Uri.Builder();
            Map<String, String> parameters = new HashMap<>();
            parameters.put("doctorId", String.valueOf(Utility.getDoctorId(context)));
            parameters.put("clinicId", mClinicId);

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
            writer.write(requestBody);    //bcz no parameters to be sent

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

            Log.d(LOG_TAG, "Doctor Clinics Slots Credential JSON String : " + clientCredStr);


            return fetchDoctorClinics(clientCredStr);

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

    }

    @Override
    protected void onPostExecute(Boolean success) {
        Log.d(LOG_TAG, "Success Boolean Tag: " + success.toString());
        if (success) {

            delegate.processSlotFinish(slotsList);

        } else {




                /*Snackbar.make(, R.string.error_unknown_error,
                        Snackbar.LENGTH_LONG)
                        .show();*/
            Toast.makeText(context,context.getResources().getString(R.string.error_unknown_error),Toast.LENGTH_SHORT).show();

        }
    }

    private boolean fetchDoctorClinics(String clientCredStr) throws JSONException {

        final String slotsListString = "doctorClinicSlotList";

        final String slotIdString  ="dcId";
        final String slotDayString = "slotDay";
        final String slotStartTimeString = "slotStartTime";
        final String slotEndTimeString = "slotEndTime";
        final String slotFeesString = "slotFees";



        String slotId;
        String slotDay;
        String slotStartTime;
        String slotEndTime;
        int slotFees;


        JSONObject clientJson = new JSONObject(clientCredStr);


            JSONArray slotsJsonArray = clientJson.getJSONArray(slotsListString);

            for(int j=0;j<slotsJsonArray.length();j++) {
                JSONObject slotJSONObject = slotsJsonArray.getJSONObject(j);

                slotId = slotJSONObject.getString(slotIdString);
                slotDay = slotJSONObject.getString(slotDayString);
                slotStartTime = slotJSONObject.getString(slotStartTimeString);
                slotEndTime = slotJSONObject.getString(slotEndTimeString);
                slotFees = Integer.parseInt(slotJSONObject.getString(slotFeesString));


                Log.d(LOG_TAG, "Slot id : "+slotId+"Slot Day: " + slotDay+"Slot Start TIme:"+slotStartTime);

                slotsList.add(new Slot(slotId,slotDay,slotStartTime,slotEndTime,slotFees));
            }


        return true;
    }


}
