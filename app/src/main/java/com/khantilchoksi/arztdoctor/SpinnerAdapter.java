package com.khantilchoksi.arztdoctor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Khantil on 19-02-2017.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    int selectedItem = 0;

    private String promptTitle;
    private Context mContext;

    public SpinnerAdapter(Context context, int resource, String[] objects, String promptTitle) {
        super(context, resource, objects);
        this.promptTitle = promptTitle;
        this.mContext = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){

        LinearLayout linear = null;

        if(position == 0){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            linear = (LinearLayout) inflater.inflate(R.layout.spinner_title, parent, false);

            TextView promptText = (TextView) linear.findViewById(R.id.promptTextView);
            promptText.setText(promptTitle);
        } else{
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            linear = (LinearLayout) inflater.inflate(R.layout.spinner_item_with_separator, parent, false);

            TextView itemText = (TextView) linear.findViewById(R.id.myspinneritem);
            itemText.setText(getItem(position));

            if(position == (getCount() - 1) ){
               linear.removeView(linear.findViewById(R.id.separatorLayout));
            }

            if(position == selectedItem)
                itemText.setTextColor(mContext.getColor(R.color.colorPrimary));

        }

        return linear;
    }


    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public String getItem(int position) {
        if (position == 0) {
            return promptTitle;
        }
        return super.getItem(position - 1);
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

    public void setSelectedItem(int position){
        selectedItem = position;
    }

}
