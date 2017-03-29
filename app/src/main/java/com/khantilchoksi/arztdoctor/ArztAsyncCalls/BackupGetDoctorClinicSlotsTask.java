package com.khantilchoksi.arztdoctor.ArztAsyncCalls;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.khantilchoksi.arztdoctor.Clinic;
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

public class BackupGetDoctorClinicSlotsTask extends AsyncTask<Void, Void, Boolean> {

    private static final String LOG_TAG = BackupGetDoctorClinicSlotsTask.class.getSimpleName();
    Context context;
    Activity activity;
    ArrayList<Clinic> clinicsList;
    ArrayList<Slot> slotsList;

    ProgressDialog progressDialog;

    public interface AsyncResponse {
        void processFinish(ArrayList<Clinic> clinicsList, ArrayList<Slot> slotsList, ProgressDialog progressDialog);
    }

    public AsyncResponse delegate = null;

    public BackupGetDoctorClinicSlotsTask(Context context, Activity activity, AsyncResponse delegate, ProgressDialog progressDialog){
        this.context = context;
        this.activity = activity;
        this.delegate = delegate;
        this.progressDialog = progressDialog;
        clinicsList = new ArrayList<Clinic>();
        slotsList = new ArrayList<Slot>();

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String clientCredStr;

        try {

            final String CLIENT_BASE_URL = context.getResources().getString(R.string.base_url).concat("getClinicSlotsDetails");
            URL url = new URL(CLIENT_BASE_URL);


            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


            Uri.Builder builder = new Uri.Builder();
            Map<String, String> parameters = new HashMap<>();
            parameters.put("doctorId", String.valueOf(Utility.getDoctorId(context)));

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
        progressDialog.dismiss();
    }

    @Override
    protected void onPostExecute(Boolean success) {
        Log.d(LOG_TAG, "Success Boolean Tag: " + success.toString());
        if (success) {

            delegate.processFinish(clinicsList,slotsList,progressDialog);

        } else {

            progressDialog.dismiss();


                /*Snackbar.make(, R.string.error_unknown_error,
                        Snackbar.LENGTH_LONG)
                        .show();*/
            Toast.makeText(context,context.getResources().getString(R.string.error_unknown_error),Toast.LENGTH_SHORT).show();

        }
    }

    private boolean fetchDoctorClinics(String clientCredStr) throws JSONException {

        final String clinicsListString = "clinicList";
        final String slotsListString = "slotList";

        final String clinicIdString = "clinicId";
        final String clinicNameString = "clinicName";
        final String clinicAddressString = "clinicAddress";
        final String clinicPincodeString = "clinicPincode";
        final String clinicLongitudeString = "clinicLongitude";
        final String clinicLatitudeString = "clinicLatitude";

        final String slotIdString  ="dcId";
        final String slotDayString = "slotDay";
        final String slotStartTimeString = "slotStartTime";
        final String slotEndTimeString = "slotEndTime";
        final String slotFeesString = "slotFees";


        String clinicId;
        String clinicName;
        String clinicAddress;
        int clinicPincode;
        float clinicLongitude;
        float clinicLatitude;

        String slotId;
        String slotDay;
        String slotStartTime;
        String slotEndTime;
        int slotFees;


        JSONObject clientJson = new JSONObject(clientCredStr);

        JSONArray clinicsJsonArray = clientJson.getJSONArray(clinicsListString);

        if(clinicsJsonArray.length()==0){
            return true; //no clinics found //send empty lists
        }

        for(int i=0;i<clinicsJsonArray.length();i++){
            JSONObject clinicJSONObject = clinicsJsonArray.getJSONObject(i);

            clinicId = clinicJSONObject.getString(clinicIdString);
            clinicName = clinicJSONObject.getString(clinicNameString);
            clinicAddress = clinicJSONObject.getString(clinicAddressString);
            clinicPincode = Integer.parseInt(clinicJSONObject.getString(clinicPincodeString));
            clinicLatitude = Float.parseFloat(clinicJSONObject.getString(clinicLatitudeString));
            clinicLongitude = Float.parseFloat(clinicJSONObject.getString(clinicLongitudeString));


            Log.d(LOG_TAG,"Clinic Name: "+clinicName);

            clinicsList.add(new Clinic(clinicId,clinicName,clinicAddress,clinicPincode,clinicLatitude,clinicLongitude));

            JSONArray slotsJsonArray = clinicJSONObject.getJSONArray(slotsListString);

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


        }



        return true;
    }


}
