package com.khantilchoksi.arztdoctor;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Khantil on 24-03-2017.
 */

public class ClinicRecyclerAdapter extends RecyclerView.Adapter<ClinicRecyclerAdapter.ViewHolder> {


    private final String LOG_TAG = getClass().getSimpleName();

    private ArrayList<Clinic> mClinicsList;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView getClinicNameTextView() {
            return clinicNameTextView;
        }

        public TextView getClinicAddressTextView() {
            return clinicAddressTextView;
        }


        private final TextView clinicNameTextView;
        private final TextView clinicAddressTextView;

        private final RecyclerView slotsRecyclerView;

        public ViewHolder(View itemView) {
            super(itemView);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });*/

            clinicNameTextView = (TextView)itemView.findViewById(R.id.clinic_name);
            clinicAddressTextView = (TextView)itemView.findViewById(R.id.clinic_address_text_view);
            slotsRecyclerView = (RecyclerView) itemView.findViewById(R.id.slots_recyclerview);
            
        }
    }

    public ClinicRecyclerAdapter(ArrayList<Clinic> clinicsList) {
        this.mClinicsList = clinicsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.show_clinic_slot_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        holder.getClinicNameTextView().setText(mClinicsList.get(position).getClinicName());
        holder.getClinicAddressTextView().setText(mClinicsList.get(position).getClinicAddress());
    }

    @Override
    public int getItemCount() {
        return mClinicsList.size();
    }


}
