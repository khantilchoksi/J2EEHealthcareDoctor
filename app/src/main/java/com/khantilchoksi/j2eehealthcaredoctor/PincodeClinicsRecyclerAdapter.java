package com.khantilchoksi.j2eehealthcaredoctor;

/**
 * Created by khantilchoksi on 28/03/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PincodeClinicsRecyclerAdapter extends RecyclerView.Adapter<PincodeClinicsRecyclerAdapter.ViewHolder> {


    private final String LOG_TAG = getClass().getSimpleName();

    private ArrayList<Clinic> clinicsList;
    private Context mContext;
    private Activity mActivity;


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView getClinicNameTextView() {
            return clinicNameTextView;
        }

        public TextView getClinicAddressTextView() {
            return clinicAddressTextView;
        }



        private final TextView clinicNameTextView;
        private final TextView clinicAddressTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG," Clinic: "+getAdapterPosition()+ " clicked. Means "
                            +clinicsList.get(getAdapterPosition()).getClinicName()+" got.");

                    Intent manageTimeSlot = new Intent(mActivity,AddClinicTimeSlotsActivity.class);


                    Bundle bundle = new Bundle();
                    bundle.putString(mContext.getResources().getString(R.string.bundle_clinic_id),clinicsList.get(getAdapterPosition()).getClinicId());
                    bundle.putString(mContext.getResources().getString(R.string.bundle_clinic_name),clinicsList.get(getAdapterPosition()).getClinicName());
                    manageTimeSlot.putExtras(bundle);
                    mActivity.startActivity(manageTimeSlot);



                }
            });

            clinicNameTextView = (TextView)itemView.findViewById(R.id.clinic_name_text_view);
            clinicAddressTextView = (TextView)itemView.findViewById(R.id.clinic_address_text_view);

        }
    }

    public PincodeClinicsRecyclerAdapter(ArrayList<Clinic> clinicsList, Context context, Activity activity) {
        this.clinicsList = clinicsList;
        this.mContext = context;
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.clinic_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        holder.getClinicNameTextView().setText(clinicsList.get(position).getClinicName());
        holder.getClinicAddressTextView().setText(clinicsList.get(position).getClinicAddress());
    }

    @Override
    public int getItemCount() {
        return clinicsList.size();
    }


}

