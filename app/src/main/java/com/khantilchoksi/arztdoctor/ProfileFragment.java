package com.khantilchoksi.arztdoctor;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.khantilchoksi.arztdoctor.ArztAsyncCalls.GetPatientProfileTask;
import com.khantilchoksi.arztdoctor.ArztAsyncCalls.SavePatientProfileTask;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements GetPatientProfileTask.AsyncResponse,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText birthdayEditTextView;
    final Calendar myCalendar = Calendar.getInstance();

    //private OnFragmentInteractionListener mListener;

    private static final int REQUEST_LOCATION = 1;  // The request code for location
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String LOG_TAG = getClass().getSimpleName();

    private View mRootView;

    private EditText mFullNameEditText;
    private EditText mMobileEditText;
    private RadioGroup mGenderRadioGroup;
    private Spinner mBloodGroupSpinner;
    private EditText mEmergencyMobileEditText;
    private TextView mAddressText;
    private TextView mPostalCode;
    private TextView mArea;
    private TextView mCity;
    private TextView mState;
    private Button mSaveButton;

    private double mLatitude = 0;
    private double mLongitude = 0;

    private ProgressDialog progressDialog;

    //user entered values
    String fullName = null;
    int gender = 0;
    int bloodGroup = 0;
    String birthdate = null;
    String emergencyMobileNumber = null;
    String fullAddress = "";
    String mPinCode = null;
    String cityName = null;
    String areaName = null;
    String stateName = null;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_my_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            case R.id.save_profile:
                //Toast.makeText(getContext(),"Saved!",Toast.LENGTH_SHORT).show();
                saveButtonClick();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);
        //Toast.makeText(getActivity(),"Profile Fragment Created!",Toast.LENGTH_LONG).show();

        //making rounded profile picture
        ImageButton userProfilePhoto = (ImageButton) mRootView.findViewById(R.id.user_profile_photo);
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.default_profile);
        RoundedBitmapDrawable drawable =
                RoundedBitmapDrawableFactory.create(res, bitmap);
        drawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
        drawable.setCircular(true);
        userProfilePhoto.setImageDrawable(drawable);

        setUpBloodGroupSpinner();


        mFullNameEditText = (EditText) mRootView.findViewById(R.id.full_name);
        mFullNameEditText.setText(Utility.getPatientFullName(getContext()));

        mMobileEditText = (EditText) mRootView.findViewById(R.id.mobile_number);
        mMobileEditText.setText(Utility.getPatientMobileNumber(getContext()));

        mGenderRadioGroup = (RadioGroup) mRootView.findViewById(R.id.radio_group_gender);

        mEmergencyMobileEditText = (EditText) mRootView.findViewById(R.id.emergency_mobile);
        mAddressText = (TextView) mRootView.findViewById(R.id.address);
        mPostalCode = (TextView) mRootView.findViewById(R.id.pincode);
        mArea = (TextView) mRootView.findViewById(R.id.area);
        mCity = (TextView) mRootView.findViewById(R.id.city);
        mState = (TextView) mRootView.findViewById(R.id.state);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateBirthdayTextView();
            }

        };

        birthdayEditTextView = (EditText) mRootView.findViewById(R.id.birthday);
        birthdayEditTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(),date,1995,0, 1).show();

            }
        });

        //setUpCitySpinner();
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Profile Info...");
        progressDialog.show();
        GetPatientProfileTask getPatientProfileTask = new GetPatientProfileTask(getContext(),getActivity(),this,progressDialog);
        getPatientProfileTask.execute((Void) null);

        Button detectCurrentLocationButton = (Button) mRootView.findViewById(R.id.detect_current_location_button);
        detectCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        });

        buildGoogleApiClient();

        mSaveButton = (Button) mRootView.findViewById(R.id.btn_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButtonClick();
            }
        });

        return mRootView;
    }

    @Override
    public void processFinish(String fullName, int gender, int bloodGroup, String birthdate,
                              String emergencyMobileNumber,
                              String latitude, String longitude, String fullAddress, String pincode, ProgressDialog progressDialog) {
        //GetPatientProfileTask finished
        mFullNameEditText.setText(fullName);
        setGenderRadioButton(gender);
        mBloodGroupSpinner.setSelection(bloodGroup);
        setBirthdate(birthdate);
        mEmergencyMobileEditText.setText(emergencyMobileNumber);
        mLatitude = Double.valueOf(latitude);
        mLongitude = Double.valueOf(longitude);
        mAddressText.setText(fullAddress);
        mPostalCode.setText(pincode);

        progressDialog.dismiss();
    }

    private void setGenderRadioButton(int gender){
        switch (gender){
            case 1:
                mGenderRadioGroup.check(R.id.radio_male);
                break;
            case 2:
                mGenderRadioGroup.check(R.id.radio_female);
                break;
            case 3:
                mGenderRadioGroup.check(R.id.radio_others);
        }
    }

    private void setBirthdate(String birthdate){
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        try {
            myCalendar.setTime(sdf.parse(birthdate));
            updateBirthdayTextView();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateBirthdayTextView(){
        String myFormat = "MMMM dd, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        birthdayEditTextView.setText(sdf.format(myCalendar.getTime()));
    }

    private void setUpBloodGroupSpinner(){
        //BloodGroup Spinner Spinner
        String[] bloodGroupData = {
                "A+",
                "A-",
                "AB+",
                "AB-",
                "B+",
                "B-",
                "O+",
                "O-",

        };

        final SpinnerAdapter bloodGroupArrayAdapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item, bloodGroupData, getContext().getString(R.string.prompt_blood_group));

        mBloodGroupSpinner = (Spinner) mRootView.findViewById(R.id.bloodGroupSpinner);
        mBloodGroupSpinner.setAdapter(bloodGroupArrayAdapter);

        mBloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    bloodGroupArrayAdapter.setSelectedItem(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

    }

    /*private void setUpCitySpinner(){
        //BloodGroup Spinner Spinner
        String[] bloodGroupData = {
                "Ahmedabad",
                "Surat",
                "Jamnagar",
                "Rajkot",
                "Mehsana",
                "Surat",
                "Vapi",
                "Vadodara",
        };

        final SpinnerAdapter cityArrayAdapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item, bloodGroupData, getContext().getString(R.string.prompt_city));

        final Spinner bloodGroupSpinner = (Spinner) mRootView.findViewById(R.id.citySpinner);
        bloodGroupSpinner.setAdapter(cityArrayAdapter);

        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    cityArrayAdapter.setSelectedItem(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }*/

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    public void saveButtonClick(){
        if (!validate()) {
            return;
        }

        int selectedGenderId = mGenderRadioGroup.getCheckedRadioButtonId();

        switch (selectedGenderId){
            case R.id.radio_male:
                gender = 1;
                break;
            case R.id.radio_female:
                gender = 2;
                break;
            case R.id.radio_others:
                gender = 3;
                break;
        }

        bloodGroup = mBloodGroupSpinner.getSelectedItemPosition();

        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        birthdate = sdf.format(myCalendar.getTime());

        Log.d(LOG_TAG," Profile Details: "+fullName+gender+"Blood Group: "+bloodGroup+" Birthdate: " +birthdate+" Emergency: "+emergencyMobileNumber+
                String.valueOf(mLatitude)+String.valueOf(mLongitude)+fullAddress+mPinCode);

        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving Profile Info...");
        progressDialog.show();

        SavePatientProfileTask savePatientProfileTask = new SavePatientProfileTask(getContext(), getActivity(),
                fullName,gender,bloodGroup,birthdate,emergencyMobileNumber,
                String.valueOf(mLatitude),String.valueOf(mLongitude),fullAddress,mPinCode,progressDialog);
        savePatientProfileTask.execute((Void) null);
    }

    public boolean validate() {
        boolean valid = true;

        mFullNameEditText.setError(null);
        mEmergencyMobileEditText.setError(null);


        fullName = mFullNameEditText.getText().toString();
        emergencyMobileNumber = mEmergencyMobileEditText.getText().toString();

        View focusView = null;
        if(fullName.isEmpty()){
            mFullNameEditText.setError(getString(R.string.error_field_required));
            focusView = mFullNameEditText;
            valid = false;
        } else if (!Utility.isMobileValid(emergencyMobileNumber)) {
            mEmergencyMobileEditText.setError(getString(R.string.error_invalid_mobileNo));
            focusView = mEmergencyMobileEditText;
            valid = false;
        }

        if (!valid) {
            //not valid data
            focusView.requestFocus();
        }

        return valid;
    }

    public void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        /*LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        Criteria locationCritera = new Criteria();
        locationCritera.setAccuracy(Criteria.ACCURACY_FINE);
        locationCritera.setAltitudeRequired(false);
        locationCritera.setBearingRequired(false);
        locationCritera.setCostAllowed(true);
        locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);

        String providerName = locationManager.getBestProvider(locationCritera, true);

        if (providerName != null && locationManager.isProviderEnabled(providerName)) {
            // Provider is enabled
            //locationManager.requestLocationUpdates(providerName, 20000, 100, this);
        } else {
            // Provider not enabled, prompt user to enable it
            Toast.makeText(getContext(), "Please turn on GPS!", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            getActivity().startActivity(myIntent);
        }*/

        //then connect to google api client
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkLocationPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "Connection Suspended.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG,"Connection Failed: "+connectionResult.getErrorMessage());
    }

    public void checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    // Display a SnackBar with an explanation and a button to trigger the request.
                    Snackbar.make(mRootView, "Permission needed for location services.",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_LOCATION);
                                    }
                                    Log.d("Permission", "After denying, Permission is Requested for location!");
                                }

                            })
                            .show();

                } else {
                    // No explanation needed, we can request the permission.

                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION);


                }

            } else {

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                updateAddressDetails();
            }
        }
    }

    private void updateAddressDetails(){

        if(mLastLocation!= null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();


            Geocoder gcd = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude(), 1);
                if (addresses.size() > 0) {
                    for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
                        fullAddress += addresses.get(0).getAddressLine(i) + "\n";

                    mPinCode = addresses.get(0).getPostalCode();
                    areaName = addresses.get(0).getSubLocality();
                    cityName = addresses.get(0).getLocality();
                    stateName = addresses.get(0).getAdminArea();

                    mAddressText.setText(fullAddress);
                    mPostalCode.setText(mPinCode);
                    mArea.setText(areaName);
                    mCity.setText(cityName);
                    mState.setText(stateName);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.d(LOG_TAG,"mLastLocation object is null");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to



                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                updateAddressDetails();

            } else {
                // Permission was denied or request was cancelled
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    // Display a SnackBar with an explanation and a button to trigger the request.
                    Log.d("Permission", "In the result if");
                    Snackbar.make(mRootView, "Give me Permissions!",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//                                    ActivityCompat.requestPermissions(,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                            REQUEST_LOCATION);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_LOCATION);
                                    }
                                }
                            })
                            .show();

                } else {
                    //User has checked never show me again
                    Log.d("Permission", "User has checked never show me again.");
                    Snackbar.make(mRootView, "Go to Settings",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Settrings", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", getActivity().getPackageName(), null));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }

            }
        }
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
