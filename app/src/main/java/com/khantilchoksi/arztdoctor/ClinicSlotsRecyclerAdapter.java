package com.khantilchoksi.arztdoctor;

/**
 * Created by khantilchoksi on 28/03/17.
 */

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

public class ClinicSlotsRecyclerAdapter extends RecyclerView.Adapter<ClinicSlotsRecyclerAdapter.ViewHolder> {


    private final String LOG_TAG = getClass().getSimpleName();

    private ArrayList<Slot> slotsList;
    private Context mContext;
    private Activity mActivity;



    public class ViewHolder extends RecyclerView.ViewHolder{

        public EditText getStartTimeEditText() {
            return startTimeEditText;
        }

        private EditText startTimeEditText;
        private EditText endTimeEditText;

        public EditText getEndTimeEditText() {
            return endTimeEditText;
        }

        private Spinner mDaySpinner;
        private ImageView mClearButton;

        public Spinner getmDaySpinner() {
            return mDaySpinner;
        }


        public ViewHolder(View itemView) {
            super(itemView);

            startTimeEditText = (EditText) itemView.findViewById(R.id.start_time_edit_text);
            startTimeEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Calendar mcurrentTime = Calendar.getInstance();

                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);

                    /*TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            startTimeEditText.setText( selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();*/
                    CustomTimePickerDialog customTimePickerDialog = new CustomTimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String time = String.format("%02d", selectedHour)+":"+String.format("%02d", selectedMinute);
                            startTimeEditText.setText(time);
                            slotsList.get(getAdapterPosition()).setStartTime(time);
                        }
                    }, hour,minute,true);
                    customTimePickerDialog.setTitle("Select Time");
                    customTimePickerDialog.show();

                }

            });

            endTimeEditText = (EditText) itemView.findViewById(R.id.end_time_edit_text);
            endTimeEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Calendar mcurrentTime = Calendar.getInstance();

                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);

                    /*TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            startTimeEditText.setText( selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();*/
                    CustomTimePickerDialog customTimePickerDialog = new CustomTimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String time = String.format("%02d", selectedHour)+":"+String.format("%02d", selectedMinute);
                            endTimeEditText.setText(time);
                            slotsList.get(getAdapterPosition()).setEndTime(time);

                        }
                    }, hour,minute,true);
                    customTimePickerDialog.setTitle("Select Time");
                    customTimePickerDialog.show();

                }

            });


            mClearButton = (ImageView) itemView.findViewById(R.id.clear_slot);
            mClearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOG_TAG,"Removing: " + slotsList.get(getAdapterPosition()).getDay());
                    mDaySpinner.setSelection(0);
                    slotsList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(),slotsList.size()-getAdapterPosition()-1);
                }
            });


            mDaySpinner = (Spinner) itemView.findViewById(R.id.day_spinner);
            String[] daysData = {
                    "Monday",
                    "Tuesday",
                    "Wednesday",
                    "Thursday",
                    "Friday",
                    "Saturday",
                    "Sunday",

            };

            final SpinnerAdapter daysArrayAdapter = new SpinnerAdapter(mActivity, R.layout.spinner_item,
                    daysData, mContext.getString(R.string.prompt_select_slot_day));


            mDaySpinner.setAdapter(daysArrayAdapter);

            mDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position>0){
                        daysArrayAdapter.setSelectedItem(position);
                        slotsList.get(getAdapterPosition()).setDay(daysArrayAdapter.getItem(position));
                        slotsList.get(getAdapterPosition()).setDayIndex(position);
                        Log.d(LOG_TAG,"Selected day: "+daysArrayAdapter.getItem(position)+" and " +
                                "For Slot position: "+getAdapterPosition());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                }
            });*/

        }


    }

    public ClinicSlotsRecyclerAdapter(ArrayList<Slot> slotsList,Context context, Activity activity) {
        this.slotsList = slotsList;
        this.mContext = context;
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.clinic_timeslot_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Added Element at " + position + " with: "+slotsList.get(position).getDay());

        if(slotsList.get(position).getDay()!=null){
            holder.getmDaySpinner().setSelection(slotsList.get(position).getDayIndex());
            holder.getStartTimeEditText().setText(slotsList.get(position).getStartTime());
            holder.getEndTimeEditText().setText(slotsList.get(position).getEndTime());
        }else{
            Log.d(LOG_TAG,"Set Selection 0.");
            holder.getmDaySpinner().setSelection(0);
            holder.getStartTimeEditText().setText(null);
            holder.getEndTimeEditText().setText(null);
        }

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        //holder.getClinicNameTextView().setText(clinicsList.get(position).getClinicName());
        //holder.getClinicNameTextView().setText(clinicsList.get(position).getClinicAddress());
    }

    @Override
    public int getItemCount() {
        return slotsList.size();
    }

    public void addItem(){
        slotsList.add(new Slot());
    }

    public Slot getItemAtPosition(int position){
        return slotsList.get(position);
    }


}

