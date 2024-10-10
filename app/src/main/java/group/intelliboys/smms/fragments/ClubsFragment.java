package group.intelliboys.smms.fragments;

import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.graphics.Color;

import group.intelliboys.smms.R;

public class ClubsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);


        ImageView clubsLogoImageView = view.findViewById(R.id.clubsLogoImageView);
        TextView memberNameTextView = view.findViewById(R.id.member_name_0);
        LinearLayout memberListLinearLayout = view.findViewById(R.id.member_list);
        Button createButton = view.findViewById(R.id.create_button);


        clubsLogoImageView.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ClubCreationActivity.class);
            startActivity(intent);
        });


        memberNameTextView.setOnClickListener(view1 -> {
            Animation alertAnimation = new AlphaAnimation(1.0f, 0.2f);
            alertAnimation.setDuration(150);
            alertAnimation.setInterpolator(new DecelerateInterpolator());
            alertAnimation.setRepeatCount(1);
            alertAnimation.setRepeatMode(Animation.REVERSE);
            memberNameTextView.startAnimation(alertAnimation);


            memberNameTextView.setTextColor(Color.BLACK);

            alertAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    Intent intent = new Intent(getActivity(), ClubCreationActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        });


        memberListLinearLayout.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ClubCreationActivity.class);
            startActivity(intent);
        });


        createButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ClubCreationActivity.class);
            startActivity(intent);
        });

        return view;

    }
}
