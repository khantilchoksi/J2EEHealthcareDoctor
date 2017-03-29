package com.khantilchoksi.arztdoctor;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.khantilchoksi.arztdoctor.ArztAsyncCalls.InsertClinicTimeSlotTask;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddClinicTimeSlotsActivityFragment extends Fragment {

    private View mRootView;
    private String mClinicId;
    private String mClinicName;
    private final String LOG_TAG = getClass().getSimpleName();
    private RecyclerView mRecyclerView;
    private EditText mConsultancyCostEditText;
    private EditText mMaxPatientsEditText;
    int mConsultationFees;
    int mMaxPatients;
    private ProgressDialog progressDialog;
    private ClinicSlotsRecyclerAdapter mClinicsSlotsRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Slot> mSlotsList;

    public AddClinicTimeSlotsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_add_clinic_time_slots, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        mClinicId = bundle.getString(getContext().getResources().getString(R.string.bundle_clinic_id));
        mClinicName = bundle.getString(getContext().getResources().getString(R.string.bundle_clinic_name));

        //set clinic name
        TextView clinicNameTextView = (TextView) mRootView.findViewById(R.id.clinic_name_text_view);
        clinicNameTextView.setText(mClinicName);


        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.slots_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mSlotsList = new ArrayList<Slot>();
        mClinicsSlotsRecyclerAdapter = new ClinicSlotsRecyclerAdapter(mSlotsList,getContext(),getActivity());
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mClinicsSlotsRecyclerAdapter);

        Button addSlotsButton = (Button) mRootView.findViewById(R.id.add_new_slot);
        addSlotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mSlotsList.add(new Slot());
                //mClinicsSlotsRecyclerAdapter.notifyDataSetChanged();
                mClinicsSlotsRecyclerAdapter.addItem();
                mClinicsSlotsRecyclerAdapter.notifyItemInserted(mSlotsList.size()-1);
            }
        });

        mConsultancyCostEditText = (EditText) mRootView.findViewById(R.id.consultation_charges_edit_text);

        mMaxPatientsEditText = (EditText)mRootView.findViewById(R.id.max_patients_edit_text);

        final Button saveButton = (Button) mRootView.findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButtonClick();
            }
        });

        return mRootView;
    }

    public void saveButtonClick(){
        boolean valid = true;
        View focusView = null;

        if(mClinicsSlotsRecyclerAdapter.getItemCount()==0){
            valid = false;
            Snackbar.make(mRootView, R.string.error_add_atleast_one_slot,
                    Snackbar.LENGTH_SHORT)
                    .show();
        }

        String consulationFeesString = mConsultancyCostEditText.getText().toString();
        if(consulationFeesString.isEmpty()){
            valid = false;
            focusView = mConsultancyCostEditText;
            mConsultancyCostEditText.setError(getContext().getResources().getString(R.string.error_field_required));
        }
        try{
            mConsultationFees = Integer.parseInt(consulationFeesString);
        }catch (NumberFormatException e){
            valid = false;
            focusView = mConsultancyCostEditText;
            mConsultancyCostEditText.setError(getContext().getResources().getString(R.string.error_invlaid_fees));
        }


        String maxPatientsString = mMaxPatientsEditText.getText().toString();
        if(maxPatientsString.isEmpty()){
            valid = false;
            focusView = mMaxPatientsEditText;
            mMaxPatientsEditText.setError(getContext().getResources().getString(R.string.error_field_required));
        }
        try{
            mMaxPatients = Integer.parseInt(maxPatientsString);
        }catch (NumberFormatException e){
            valid = false;
            focusView = mMaxPatientsEditText;
            mMaxPatientsEditText.setError(getContext().getResources().getString(R.string.error_invalid_max_patients));
        }




        if(!valid && focusView!= null){
            focusView.requestFocus();
        }

        if(valid){
            progressDialog = new ProgressDialog(getActivity(),
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Saving Slot Details...");
            progressDialog.show();
            Log.d(LOG_TAG,"Set Task Count form Activity: "+mClinicsSlotsRecyclerAdapter.getItemCount());
            InsertClinicTimeSlotTask.setSetTaskCounts(mClinicsSlotsRecyclerAdapter.getItemCount());
            Slot tempSlot;
            for(int i = 0;i<mClinicsSlotsRecyclerAdapter.getItemCount();i++){
                tempSlot = mClinicsSlotsRecyclerAdapter.getItemAtPosition(i);
                new InsertClinicTimeSlotTask(getContext(),getActivity(),mClinicId,tempSlot,mConsultationFees,mMaxPatients,progressDialog).execute((Void) null);

            }
        }

    }
}
