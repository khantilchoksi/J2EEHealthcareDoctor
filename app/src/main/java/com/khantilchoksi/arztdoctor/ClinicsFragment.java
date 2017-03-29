package com.khantilchoksi.arztdoctor;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.khantilchoksi.arztdoctor.ArztAsyncCalls.GetDoctorClinicSlotsTask;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClinicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClinicsFragment extends Fragment implements GetDoctorClinicSlotsTask.AsyncResponse{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;
    private ClinicRecyclerAdapter mClinicsRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Clinic> mClinicsList;
    private ArrayList<Slot> mSlotsList;
    private LinearLayout mNoClinicsLinearLayout;

    private ProgressDialog progressDialog;

    public ClinicsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onStart() {
        super.onStart();
        initDataset();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClinicsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClinicsFragment newInstance(String param1, String param2) {
        ClinicsFragment fragment = new ClinicsFragment();
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

        //initDataset();
    }

    private void initDataset() {
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Clinics Details...");
        progressDialog.show();

        GetDoctorClinicSlotsTask getDoctorClinicSlotsTask = new GetDoctorClinicSlotsTask(getContext(),
                getActivity(),this,progressDialog);
        getDoctorClinicSlotsTask.execute((Void) null);


        /*specialityNamesList = new ArrayList<String>();
        specialityDescriptionList = new ArrayList<String>();

        specialityNamesList.add("Ontology");
        specialityDescriptionList.add("Treat diseases and disorders of ear and hearing.");

        specialityNamesList.add("Physician");
        specialityDescriptionList.add("Treat diseases and disorders of internal structures of the body.");

        specialityNamesList.add("Ontology");
        specialityDescriptionList.add("Treat diseases and disorders of ear and hearing.");

        specialityNamesList.add("Physician");
        specialityDescriptionList.add("Treat diseases and disorders of internal structures of the body.");

        specialityNamesList.add("Ontology");
        specialityDescriptionList.add("Treat diseases and disorders of ear and hearing.");

        specialityNamesList.add("Physician");
        specialityDescriptionList.add("Treat diseases and disorders of internal structures of the body.");*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_clinics, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.clinics_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.scrollToPosition(scrollPosition);

        mNoClinicsLinearLayout = (LinearLayout) rootView.findViewById(R.id.no_clinics_available_layout);
        /*FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.add_clinic_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add your clinic! Thanks.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        return rootView;
    }


    @Override
    public void processFinish(ArrayList<Clinic> clinicsList, ArrayList<Slot> slotsList, ProgressDialog progressDialog) {
        this.mClinicsList = clinicsList;
        this.mSlotsList = slotsList;

        if(mClinicsList.isEmpty()){
            mNoClinicsLinearLayout.setVisibility(View.VISIBLE);
        }else{
            mClinicsRecyclerAdapter = new ClinicRecyclerAdapter(this.mClinicsList,this.mSlotsList, getContext(), getActivity());
            mRecyclerView.setAdapter(mClinicsRecyclerAdapter);
        }
        progressDialog.dismiss();


    }





}
