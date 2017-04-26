package com.khantilchoksi.j2eehealthcaredoctor;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public PersonalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalFragment newInstance(String param1, String param2) {
        PersonalFragment fragment = new PersonalFragment();
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
        View rootView =  inflater.inflate(R.layout.fragment_personal, container, false);

        //making rounded profile picture
        ImageView userProfilePhoto = (ImageView) rootView.findViewById(R.id.user_profile_photo);
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.default_profile);
        RoundedBitmapDrawable drawable =
                RoundedBitmapDrawableFactory.create(res, bitmap);
        drawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
        drawable.setCircular(true);
        userProfilePhoto.setImageDrawable(drawable);

        //Setting name and mobile number from the shared preferences
        TextView patientNameTextView = (TextView) rootView.findViewById(R.id.user_profile_name);
        patientNameTextView.setText(Utility.getPatientFullName(getContext()));

        TextView patientMobileTextView = (TextView) rootView.findViewById(R.id.user_profile_short_number);
        patientMobileTextView.setText(getContext().getResources().getString(R.string.indian_country_code).
                concat(Utility.getPatientMobileNumber(getContext())));

        TextView editProfileTextView = (TextView) rootView.findViewById(R.id.edit_profile_textview);
        editProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileActivity = new Intent(getActivity() ,MyProfileActivity.class);
                getActivity().startActivity(profileActivity);
            }
        });

        TextView logOutTextView = (TextView) rootView.findViewById(R.id.sign_out_text_view);
        logOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getActivity().getSharedPreferences(getResources().getString(R.string.login_pref), MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();

                //Set "hasLoggedIn" to true
                editor.putBoolean(getString(R.string.pref_logged_in), false);
                editor.commit();

                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        return rootView;
    }

}
