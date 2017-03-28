package com.khantilchoksi.arztdoctor;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddExistingClinicsActivityFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_LOCATION = 1;  // The request code for location
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private final String LOG_TAG = getClass().getSimpleName();
    private View mRootView;

    private Button mSearchButton;
    private EditText mPinCodeEditText;

    private int mPincode;
    private double mLatitude = 0;
    private double mLongitude = 0;

    public AddExistingClinicsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.fragment_add_existing_clinics, container, false);

        buildGoogleApiClient();

        mPinCodeEditText = (EditText) mRootView.findViewById(R.id.pincode);


        Button detectCurrentLocationButton = (Button) mRootView.findViewById(R.id.detect_current_location_button);
        detectCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        });

        mSearchButton = (Button) mRootView.findViewById(R.id.search_clinic);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButtonClick();
            }
        });

        return mRootView;
    }

    public void searchButtonClick(){
        boolean valid = true;
        mPinCodeEditText.setError(null);

        View focusView = null;
        try{
            mPincode = Integer.parseInt(mPinCodeEditText.getText().toString());
        }catch (NumberFormatException e){
            mPinCodeEditText.setError(getContext().getResources().getString(R.string.error_invalid_pincode));
            valid = false;
            focusView = mPinCodeEditText;
            e.printStackTrace();
        }

        if(!valid){
            focusView.requestFocus();

        }else{

            Intent clinicsIntent = new Intent(getActivity(),ShowClinicsActivity.class);
            clinicsIntent.putExtra(getContext().getResources().getString(R.string.intent_extra_pincode),mPincode);
            getActivity().startActivity(clinicsIntent);

        }


    }

    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkLocationPermissions();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG,"Connection Failed: "+connectionResult.getErrorMessage());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "Connection Suspended.");
        mGoogleApiClient.connect();
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

                    mPinCodeEditText.setText(addresses.get(0).getPostalCode());
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
}
