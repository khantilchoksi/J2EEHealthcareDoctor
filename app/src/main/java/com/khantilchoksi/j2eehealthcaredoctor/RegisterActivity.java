package com.khantilchoksi.j2eehealthcaredoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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


public class RegisterActivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private static int mPatientId;
    private static String mFullName;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserSignUpTask mAuthTask = null;

    private CoordinatorLayout coordinatorLayout;
    private EditText nameEditTextView;
    private EditText emailEditTextView;
    private EditText mobileEditTextView;
    private EditText passwordEditTextView;
    private EditText reEnterPasswordEditTextView;
    private Button signupButton;
    private TextView loginLinkTextView;

    //User Entered Values
    private String name;
    private String email;
    private String mobile;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.registerCoordinatorLayout);
        nameEditTextView = (EditText) findViewById(R.id.full_name);
        emailEditTextView = (EditText) findViewById(R.id.email_id);
        mobileEditTextView = (EditText) findViewById(R.id.mobile_number);
        passwordEditTextView = (EditText) findViewById(R.id.sign_up_password);
        reEnterPasswordEditTextView = (EditText) findViewById(R.id.re_enter_assword);
        signupButton = (Button) findViewById(R.id.btn_signup);
        loginLinkTextView = (TextView) findViewById(R.id.link_login);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and go to main activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }

    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        //signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        mAuthTask = new UserSignUpTask(name, email, mobile, password);
        mAuthTask.execute((Void) null);

        // TODO: Implement your own signup logic here.

/*        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 5000);*/
    }

/*
    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        //setResult(RESULT_OK, null);
        finish();
    }*/

    public void onSignupFailed() {
        //Toast.makeText(getBaseContext(), "Sign Up failed", Toast.LENGTH_SHORT).show();

        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        nameEditTextView.setError(null);
        mobileEditTextView.setError(null);
        emailEditTextView.setError(null);
        passwordEditTextView.setError(null);
        reEnterPasswordEditTextView.setError(null);

        name = nameEditTextView.getText().toString();
        email = emailEditTextView.getText().toString();
        mobile = mobileEditTextView.getText().toString();
        password = passwordEditTextView.getText().toString();
        String reEnterPassword = reEnterPasswordEditTextView.getText().toString();

        View focusView = null;

        if (name.isEmpty()) {
            nameEditTextView.setError(getString(R.string.error_field_required));
            focusView = nameEditTextView;
            valid = false;
        } else if (name.length() < 3) {
            nameEditTextView.setError(getString(R.string.error_invalid_name));
            focusView = nameEditTextView;
            valid = false;
        } else if (mobile.isEmpty()) {
            mobileEditTextView.setError(getString(R.string.error_field_required));
            focusView = mobileEditTextView;
            valid = false;
        } else if (isMobileValid(mobile)) {
            mobileEditTextView.setError(getString(R.string.error_invalid_mobileNo));
            focusView = mobileEditTextView;
            valid = false;
        } else if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditTextView.setError(getString(R.string.error_invalid_email));
            focusView = emailEditTextView;
            valid = false;
        } else if (password.isEmpty() || password.length() < 4) {
            passwordEditTextView.setError(getString(R.string.error_invalid_password));
            focusView = passwordEditTextView;
            valid = false;
        } else if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || !(reEnterPassword.equals(password))) {
            reEnterPasswordEditTextView.setError(getString(R.string.error_password_doesnot_match));
            focusView = reEnterPasswordEditTextView;
            valid = false;
        }

        if (!valid) {
            //not valid data
            focusView.requestFocus();
        }

        return valid;
    }

    private boolean isMobileValid(String mobileNo) {

        return ( (mobileNo.contains("[1-9][0-9]+")) && (mobileNo.length() == 10));
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserSignUpTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mEmail;
        private final String mMobile;
        private final String mPassword;
        private boolean isEmailInvalid = false;
        private boolean isMobileInvalid = false;
        private boolean isUnknownError = false;

        UserSignUpTask(String name, String email, String mobile, String password) {
            mName = name;
            mEmail = email;
            mMobile = mobile;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            /*try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;*/

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String clientCredStr;

            try {

                final String CLIENT_BASE_URL = getResources().getString(R.string.base_url).concat("registerUser");
                URL url = new URL(CLIENT_BASE_URL);


                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);


                Uri.Builder builder = new Uri.Builder();
                Map<String, String> parameters = new HashMap<>();
                parameters.put("authKey", "avk");
                parameters.put("user_name", mName);
                parameters.put("primary_mobile", mMobile);
                parameters.put("email_id", mEmail);
                parameters.put("password", mPassword);
                parameters.put("acctype", "2"); //doctor acctype is 1

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


                return validateUser(clientCredStr);

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

            // TODO: register the new account here.
            //return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            Log.d(LOG_TAG, "Success Boolean Tag: " + success.toString());
            if (success) {
                //Log.d(LOG_TAG,"Inside Success: "+success.toString());

                //User has successfully logged in, save this information
                // We need an Editor object to make preference changes.
                SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.login_pref), MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = settings.edit();

                //Set "hasLoggedIn" to true
                editor.putBoolean(getString(R.string.pref_logged_in), true);
                editor.putString(getString(R.string.pref_mobile_no), mMobile);
                editor.putInt(getString(R.string.pref_doctor_Id), mPatientId);
                editor.putString(getString(R.string.pref_full_name), mFullName);
                // Commit the edits!
                editor.commit();

                successfulRegistered();

            } else {
                SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.login_pref), MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();

                //Set "hasLoggedIn" to true
                editor.putBoolean(getString(R.string.pref_logged_in), false);
                editor.commit();

                progressDialog.dismiss();

                if(isMobileInvalid){
                    mobileEditTextView.setError(getString(R.string.error_mobile_already_registered));
                    mobileEditTextView.requestFocus();

                    Snackbar.make(coordinatorLayout, R.string.error_mobile_already_registered,
                            Snackbar.LENGTH_LONG)
                            .show();

                }else if(isEmailInvalid){
                    emailEditTextView.setError(getString(R.string.error_email_already_registered));
                    emailEditTextView.requestFocus();
                    Snackbar.make(coordinatorLayout, R.string.error_email_already_registered,
                            Snackbar.LENGTH_LONG)
                            .show();
                }else if(isUnknownError){
                    Snackbar.make(coordinatorLayout, R.string.error_unknown_error,
                            Snackbar.LENGTH_LONG)
                            .show();
                }else{
                    Snackbar.make(coordinatorLayout, R.string.error_unknown_error,
                            Snackbar.LENGTH_LONG)
                            .show();
                }

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            progressDialog.dismiss();
        }

        private boolean validateUser(String clientCredStr) throws JSONException {

            final String accountCreated = "accountCreated";
            final String clientEmailValid = "emailExists";
            final String clientMobileValid = "mobileExists";
            final String unknownErrorServer = "unknownError";
            final String doctorIdString = "did";
            final String fullNameString = "fullName";

            JSONObject clientJson = new JSONObject(clientCredStr);
            String isAccountCreated = clientJson.getString(accountCreated);
            if (isAccountCreated.contains("true")) {
                //Account successfully created
                mPatientId = clientJson.getInt(doctorIdString);
                mFullName = clientJson.getString(fullNameString);
                Log.d(LOG_TAG, "Got Patient Id : " + mPatientId +" Patient Name: "+mFullName);
                return true;
            } else {
                //Account not created successfully

                String isMobileExists = clientJson.getString(clientMobileValid);
                if (isMobileExists.contains("false")) {
                    //Mobile is valid
                    String isEmailExists = clientJson.getString(clientEmailValid);
                    if (isEmailExists.contains("false")) {
                        String isUnknownErrorExists = clientJson.getString(unknownErrorServer);
                        if(isUnknownErrorExists.contains("false")){

                        }else{
                            isUnknownError = true;
                        }
                    } else {
                        //Password is not valid
                        isEmailInvalid = true;
                    }
                } else {
                    //Email is not registered
                    //Log.d(LOG_TAG, "Mobile Not Valid");
                    //Set the error
                    isMobileInvalid = true;

                }

            }

            return false;
        }
    }

    private void successfulRegistered() {
        progressDialog.dismiss();
        Intent homeActivity = new Intent(RegisterActivity.this, HomeActivity.class);
//                Bundle extras = new Bundle();           //parcelable
//                extras.putInt("Client ID", clientId);  //key - value pair
//                ticketHomepage.putExtras(extras);
        startActivity(homeActivity);
        finish();
    }

}
