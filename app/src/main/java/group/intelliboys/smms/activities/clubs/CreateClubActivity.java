package group.intelliboys.smms.activities.clubs;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import group.intelliboys.smms.R;
import group.intelliboys.smms.fragments.dashboard_menu.ClubsFragment;

public class CreateClubActivity extends AppCompatActivity {

    private EditText etClubName;
    private ImageButton btnAddLogo;
    private ImageButton btnAddPeople;
    private Button btnCreateClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);

        etClubName = findViewById(R.id.etClubName);
        btnAddLogo = findViewById(R.id.btnAddLogo);
        btnAddPeople = findViewById(R.id.btnAddPeople);
        btnCreateClub = findViewById(R.id.btnCreateClub);


        btnAddLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(CreateClubActivity.this, "Add logo clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(CreateClubActivity.this, "Add people clicked", Toast.LENGTH_SHORT).show();

            }
        });

        btnCreateClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String clubName = etClubName.getText().toString().trim();

                if (clubName.isEmpty()) {

                    Toast.makeText(CreateClubActivity.this, "Please enter a club name", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(CreateClubActivity.this, "Club '" + clubName + "' created!", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(CreateClubActivity.this, ClubsFragment.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}