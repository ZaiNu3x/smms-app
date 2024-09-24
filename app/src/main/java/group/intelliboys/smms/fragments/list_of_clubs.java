package group.intelliboys.smms.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import group.intelliboys.smms.R;

public class list_of_clubs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clubs_list);

        TextView maddogTextView = findViewById(R.id.maddog);
        TextView indiosClubTextView = findViewById(R.id.indios_club);
        TextView royaleTextView = findViewById(R.id.royale_club);

        // MAD DOG CLUB
        ClickableSpan maddogClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.maddogmc.com/"));
                startActivity(intent);
            }
        };

        // Create SpannableString for MAD DOG CLUB
        SpannableString maddogSpannedText = new SpannableString("MAD DOG CLUB");
        maddogSpannedText.setSpan(maddogClickableSpan, 0, maddogSpannedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        maddogSpannedText.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, maddogSpannedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        maddogTextView.setText(maddogSpannedText);

        // INDIOS CLUB
        ClickableSpan indiosClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/IndiosFilipinasMC/"));
                startActivity(intent);
            }
        };

        // Create SpannableString for INDIOS CLUB
        SpannableString indiosSpannedText = new SpannableString(getString(R.string.indios_club));
        indiosSpannedText.setSpan(indiosClickableSpan, 0, indiosSpannedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        indiosSpannedText.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, indiosSpannedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        indiosClubTextView.setText(indiosSpannedText);

        // ROYALE CLUB ClickableSpan
        ClickableSpan royaleClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/royalemotoclub/"));
                startActivity(intent);
            }
        };

        // ROYALE CLUB
        SpannableString royaleSpannedText = new SpannableString(getString(R.string.royale_club));
        royaleSpannedText.setSpan(royaleClickableSpan, 0, royaleSpannedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        royaleSpannedText.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, royaleSpannedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        royaleTextView.setText(royaleSpannedText);
    }
}