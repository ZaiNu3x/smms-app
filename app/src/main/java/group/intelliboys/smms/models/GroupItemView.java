package group.intelliboys.smms.models;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import group.intelliboys.smms.R;

public class GroupItemView extends LinearLayout {

    public GroupItemView(Context context, String groupName, Uri logoUri) {
        super(context);
        inflate(context, R.layout.group_item, this);

        TextView groupNameTextView = findViewById(R.id.tvGroupName);
        ImageView groupLogoImageView = findViewById(R.id.ivGroupLogo);

        groupNameTextView.setText(groupName);
        if (logoUri != null) {
            groupLogoImageView.setImageURI(logoUri);
        }
    }
}
