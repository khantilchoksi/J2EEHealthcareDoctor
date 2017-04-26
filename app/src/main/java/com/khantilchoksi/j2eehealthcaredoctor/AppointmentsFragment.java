package com.khantilchoksi.j2eehealthcaredoctor;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.khantilchoksi.j2eehealthcaredoctor.ArztAsyncCalls.GetAppointmentsTask;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppointmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppointmentsFragment extends Fragment implements GetAppointmentsTask.AsyncResponse{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View mRootview;
    private RecyclerView mRecyclerView;
    private AppointmentsAdapter mAppointmentAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Appointment> mAppointmentsList;
    private LinearLayout mNoAppointmentsLinearLayout;

    private ProgressDialog progressDialog;

    @Override
    public void onStart() {
        super.onStart();
        initDataset();
    }

    public AppointmentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppointmentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppointmentsFragment newInstance(String param1, String param2) {
        AppointmentsFragment fragment = new AppointmentsFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootview = inflater.inflate(R.layout.fragment_appointments, container, false);

        mNoAppointmentsLinearLayout = (LinearLayout) mRootview.findViewById(R.id.no_appointments_available_layout);

        mRecyclerView = (RecyclerView) mRootview.findViewById(R.id.appointments_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return mRootview;
    }

    private void initDataset() {
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Appointments...");
        progressDialog.show();

        GetAppointmentsTask getAppointmentsTask = new GetAppointmentsTask(getContext(),
                getActivity(),this,progressDialog);
        getAppointmentsTask.execute((Void) null);
    }

    @Override
    public void processFinish(ArrayList<Appointment> appointmentsList, ProgressDialog progressDialog) {
        this.mAppointmentsList = appointmentsList;


        if(mAppointmentsList.isEmpty()){
            mNoAppointmentsLinearLayout.setVisibility(View.VISIBLE);
        }else{
            mAppointmentAdapter = new AppointmentsAdapter(this.mAppointmentsList, getActivity());
            mRecyclerView.setAdapter(mAppointmentAdapter);
        }
        progressDialog.dismiss();
    }
}
