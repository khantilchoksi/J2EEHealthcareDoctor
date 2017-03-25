package com.khantilchoksi.arztdoctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
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

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    private final String LOG_TAG = LoginActivity.class.getSimpleName();
    private static int mPatientId;
    private static String mFullName;
    private ProgressDialog progressDialog;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private CoordinatorLayout coordinatorLayout;
    private TextView mMobileNoView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mSignUpLinkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.login_pref), 0);

        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean(getString(R.string.pref_logged_in), false);

        if(hasLoggedIn)
        {
            //mPatientId = settings.getInt(getString(R.string.pref_patient_Id),0);
            //Go directly to main activity.
            successfulLoggedIn();

        }else {
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.loginCoordinatorLayout);
            // Set up the login form.
            mMobileNoView = (TextView) findViewById(R.id.mobileNo);
            //populateAutoComplete();

            mSignUpLinkView = (TextView) findViewById(R.id.link_signup);

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mSignUpLinkView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Start the Signup activity
                    Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(registerIntent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
        }

    }


    /*private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mMobileNoView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    *//**
     * Callback received when a permissions request has been completed.
     *//*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }*/


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mMobileNoView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String mobileNo =  mMobileNoView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid mobileNo address.
        if (TextUtils.isEmpty(mobileNo)) {
             mMobileNoView.setError(getString(R.string.error_field_required));
            focusView = mMobileNoView;
            cancel = true;
        } else if (!isMobileValid(mobileNo)) {
             mMobileNoView.setError(getString(R.string.error_invalid_mobileNo));
            focusView = mMobileNoView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();



            mAuthTask = new UserLoginTask(mobileNo, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isMobileValid(String mobileNo) {

        return ( mobileNo.length() == 10 && (mobileNo.matches("[1-9][0-9]+")));
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 3;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
   /* @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });


        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }*/

/*    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

         mMobileNoView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }*/

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mMobile;
        private final String mPassword;
        private boolean isMobileInvalid = false;
        private boolean isPasswordInvalid = false;

        UserLoginTask(String mobile, String password) {
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

                final String CLIENT_BASE_URL = getResources().getString(R.string.base_url).concat("authenticateDoctor");
                URL url = new URL(CLIENT_BASE_URL);


                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);


                Uri.Builder builder = new Uri.Builder();
                Map<String, String> parameters = new HashMap<>();
                parameters.put("mobile", mMobile);
                parameters.put("password", mPassword);


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
                Log.d(LOG_TAG,"URL Connection Response Code "+status);

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


                return validatePassword(clientCredStr);

            } catch (IOException e) {
                Log.d(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return false;
            } catch (JSONException e){
                Log.d(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return false;
            }
            finally {
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
                editor.putInt(getString(R.string.pref_doctor_Id), mPatientId);
                editor.putString(getString(R.string.pref_mobile_no), mMobile);
                editor.putString(getString(R.string.pref_full_name), mFullName);

                // Commit the edits!
                editor.commit();
                progressDialog.dismiss();
                successfulLoggedIn();

            } else {
                SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.login_pref), MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();

                //Set "hasLoggedIn" to true
                editor.putBoolean(getString(R.string.pref_logged_in), false);
                editor.commit();
                progressDialog.dismiss();
                if(isMobileInvalid){
                    mMobileNoView.setError(getString(R.string.error_mobile_not_registered));
                    mMobileNoView.requestFocus();
                    Snackbar.make(coordinatorLayout, R.string.error_mobile_not_registered,
                            Snackbar.LENGTH_LONG)
                            .show();
                }else if(isPasswordInvalid){
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();

                    Snackbar.make(coordinatorLayout, R.string.error_incorrect_password,
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

        private boolean validatePassword(String clientCredStr) throws JSONException{

            final String isMobileRegisteredString = "isMobileRegistered";
            final String isPasswordValidString = "isPasswordValid";
            final String doctorIdString = "did";
            final String fullNameString = "fullName";

            JSONObject clientJson = new JSONObject(clientCredStr);

            String isMobileRegistered = clientJson.getString(isMobileRegisteredString);
            if(isMobileRegistered.contains("true")){
                //Email is registred with server
                //Check for the password
                String isValidPassword = clientJson.getString(isPasswordValidString);

                if(isValidPassword.contains("true")){
                    //Password is also valid
                    mPatientId = clientJson.getInt(doctorIdString);
                    mFullName = clientJson.getString(fullNameString);
                    Log.d(LOG_TAG,"Got Patient Id : "+ mPatientId+" Full Name: "+mFullName);
                    return true;
                } else{
                    //Password is not valid
                    isPasswordInvalid = true;
                }
            }else{
                //Email is not registered
                //Log.d(LOG_TAG,"Email Not Valid");
                //Set the error
                isMobileInvalid = true;
            }
            return false;
        }
    }

    private void successfulLoggedIn(){

        Intent homeActivity = new Intent(LoginActivity.this,HomeActivity.class);
//                Bundle extras = new Bundle();           //parcelable
//                extras.putInt("Client ID", clientId);  //key - value pair
//                ticketHomepage.putExtras(extras);
        startActivity(homeActivity);
        finish();
    }
}

