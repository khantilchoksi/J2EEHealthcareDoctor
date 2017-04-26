package com.khantilchoksi.j2eehealthcaredoctor;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.khantilchoksi.j2eehealthcaredoctor.ArztAsyncCalls.GetClinicsTask;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShowClinicsActivityFragment extends Fragment implements GetClinicsTask.AsyncResponse{

    private View mRootView;
    private ProgressDialog progressDialog;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayout mNoClinicsAvailableLayout;

    private int mPincode;

    public ShowClinicsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.fragment_show_clinics, container, false);

        mPincode = getActivity().getIntent().getIntExtra(getContext().getResources().getString(R.string.intent_extra_pincode), 0);

        mNoClinicsAvailableLayout = (LinearLayout) mRootView.findViewById(R.id.no_clinics_available_layout);

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.clinics_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        getClinics();

        return mRootView;
    }

    public void getClinics(){


        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving Profile Info...");
        progressDialog.show();

        GetClinicsTask getClinicsTask = new GetClinicsTask(getContext(),getActivity(),mPincode,this,progressDialog);

        getClinicsTask.execute((Void) null);

    }

    @Override
    public void processFinish(ArrayList<Clinic> clinicsList, ProgressDialog progressDialog) {
        if(clinicsList.isEmpty()){
            Button requestToAddClinicButton = (Button) mRootView.findViewById(R.id.btn_request_to_clinic);
            requestToAddClinicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //go to intent

                    //call finish or store pincode
                }
            });
            mNoClinicsAvailableLayout.setVisibility(View.VISIBLE);
        }else{
            PincodeClinicsRecyclerAdapter clinicsRecyclerAdapter = new PincodeClinicsRecyclerAdapter(clinicsList,getContext(),getActivity());
            mRecyclerView.setAdapter(clinicsRecyclerAdapter);
        }
        progressDialog.dismiss();

    }
}
