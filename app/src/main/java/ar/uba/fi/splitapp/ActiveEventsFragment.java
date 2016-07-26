package ar.uba.fi.splitapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.pkmmte.view.CircularImageView;

import java.text.DateFormat;
import java.util.Date;

public class ActiveEventsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FacebookManager.checkInit(this.getActivity());

        View fragment = inflater.inflate(R.layout.fragment_active_events, container, false);

        LinearLayout templates = (LinearLayout) fragment.findViewById(R.id.active_events_list);

        /*Mock*/
        View templateItem = inflater.inflate(R.layout.settlement_debt_layout, null);

        CircularImageView view = (CircularImageView) templateItem.findViewById(R.id.debt_friend_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, getActivity().getApplicationContext());

        view = (CircularImageView) templateItem.findViewById(R.id.debt_user_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, getActivity().getApplicationContext());

        ImageView background = (ImageView) templateItem.findViewById(R.id.debt_background);
        Glide.with(getActivity()).load(R.drawable.debt_on).centerCrop().into(background);

        Glide.with(getActivity().getApplicationContext()).load(R.drawable.debt_on).centerCrop().into(background);
        templates.addView(templateItem);

        templateItem = inflater.inflate(R.layout.settlement_debt_layout, null);

        view = (CircularImageView) templateItem.findViewById(R.id.debt_friend_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, getActivity().getApplicationContext());

        view = (CircularImageView) templateItem.findViewById(R.id.debt_user_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, getActivity().getApplicationContext());

        background = (ImageView) templateItem.findViewById(R.id.debt_background);
        Glide.with(getActivity().getApplicationContext()).load(R.drawable.debt_off).centerCrop().into(background);

        templates.addView(templateItem);

        templateItem = inflater.inflate(R.layout.settlement_debt_layout, null);

        view = (CircularImageView) templateItem.findViewById(R.id.debt_friend_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, getActivity().getApplicationContext());

        view = (CircularImageView) templateItem.findViewById(R.id.debt_user_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, getActivity().getApplicationContext());

        background = (ImageView) templateItem.findViewById(R.id.debt_background);
        Glide.with(getActivity().getApplicationContext()).load(R.drawable.settle_on).centerCrop().into(background);

        templates.addView(templateItem);

        templateItem = inflater.inflate(R.layout.settlement_debt_layout, null);

        view = (CircularImageView) templateItem.findViewById(R.id.debt_friend_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, getActivity().getApplicationContext());

        view = (CircularImageView) templateItem.findViewById(R.id.debt_user_img);
        FacebookManager.fillWithUserPic(Profile.getCurrentProfile().getId(), view, getActivity().getApplicationContext());

        background = (ImageView) templateItem.findViewById(R.id.debt_background);
        Glide.with(getActivity().getApplicationContext()).load(R.drawable.settle_off).centerCrop().into(background);

        templates.addView(templateItem);
            /*Mock*/

        for (int i = 1; i < 21; i++) {
            templateItem = inflater.inflate(R.layout.event_active_item, null);

            TextView text = (TextView) templateItem.findViewById(R.id.event_name);
            text.setText("Evento #" + i);

            TextView date = (TextView) templateItem.findViewById(R.id.event_date);
            date.setText(DateFormat.getDateInstance().format(new Date()));

            templateItem.setOnClickListener(v -> {
                Intent eventDetail = new Intent(getContext(), EventDescriptionActivity.class);
                startActivity(eventDetail);
            });

            templates.addView(templateItem);

        }



        return fragment;
    }

}

