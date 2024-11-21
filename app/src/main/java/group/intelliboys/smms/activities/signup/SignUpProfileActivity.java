package group.intelliboys.smms.activities.signup;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import group.intelliboys.smms.R;
import group.intelliboys.smms.utils.Commons;
import group.intelliboys.smms.utils.Converter;
import group.intelliboys.smms.utils.ServerAPIs;
import lombok.Getter;

public class SignUpProfileActivity extends AppCompatActivity {
    private EditText signupLastNameField;
    private EditText signupFirstNameField;
    private EditText signupMiddleNameField;
    private RadioButton signupMaleRadioBtn;
    private RadioButton signupFemaleRadioBtn;
    private EditText signupBirthDateField;
    private EditText signupAddressField;
    private CircleImageView signupProfilePic;
    private Button signupSelectProfilePicBtn;
    @Getter
    private Button signupSubmitBtn;

    private String sex;

    private boolean isLastNameValid;
    private boolean isFirstNameValid;
    private boolean isMiddleNameValid;
    private boolean isMaleSelected;
    private boolean isFemaleSelected;
    private boolean isBirthDateValid;
    private boolean isAddressValid;
    private boolean isProfilePicValid = true;

    private ServerAPIs serverAPIs;
    private String formId;
    private static final int PICK_IMAGE_REQUEST = 1;
    private byte[] profilePicData;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user_profile);

        signupLastNameField = findViewById(R.id.signupLastNameField);
        signupFirstNameField = findViewById(R.id.signupFirstNameField);
        signupMiddleNameField = findViewById(R.id.signupMiddleNameField);
        signupMaleRadioBtn = findViewById(R.id.signupMaleRadioBtn);
        signupFemaleRadioBtn = findViewById(R.id.signupFemaleRadioBtn);
        signupBirthDateField = findViewById(R.id.signupBirthDateField);
        signupAddressField = findViewById(R.id.signupAddressField);
        signupProfilePic = findViewById(R.id.signupProfilePic);
        signupSelectProfilePicBtn = findViewById(R.id.signupSelectProfilePicBtn);
        signupSubmitBtn = findViewById(R.id.signupSubmitBtn);
        formId = UUID.randomUUID().toString();

        signupLastNameField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final long DELAY = 500;
            private Runnable runnable;
            private final String REGEX = "^[a-zA-ZñÑ]{2,}$";
            private final Pattern pattern = Pattern.compile(REGEX);

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(runnable);

                runnable = () -> {
                    String lastName = s.toString();
                    Matcher matcher = pattern.matcher(lastName);

                    if (matcher.matches()) {
                        isLastNameValid = true;
                    } else {
                        isLastNameValid = false;
                        signupLastNameField.setError("Invalid lastname!");
                    }

                    areAllFieldsValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signupFirstNameField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final long DELAY = 500;
            private Runnable runnable;
            private final String REGEX = "^[a-zA-ZñÑ]{2,}$";
            private final Pattern pattern = Pattern.compile(REGEX);

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(runnable);

                runnable = () -> {
                    String firstName = s.toString();
                    Matcher matcher = pattern.matcher(firstName);

                    if (matcher.matches()) {
                        isFirstNameValid = true;
                    } else {
                        isFirstNameValid = false;
                        signupFirstNameField.setError("Invalid firstname!");
                    }

                    areAllFieldsValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signupMiddleNameField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final long DELAY = 500;
            private Runnable runnable;
            private final String REGEX = "^[a-zA-ZñÑ]{2,}( [a-zA-ZñÑ]{2,})*$";
            private final Pattern pattern = Pattern.compile(REGEX);

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(runnable);

                runnable = () -> {
                    String middleName = s.toString();
                    Matcher matcher = pattern.matcher(middleName);

                    if (matcher.matches()) {
                        isMiddleNameValid = true;
                    } else {
                        isMiddleNameValid = false;
                        signupMiddleNameField.setError("Invalid middle name!");
                    }

                    areAllFieldsValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signupMaleRadioBtn.setOnClickListener(v -> {
            sex = "m";
            isMaleSelected = signupMaleRadioBtn.isChecked();
            areAllFieldsValid();
        });

        signupFemaleRadioBtn.setOnClickListener(v -> {
            sex = "f";
            isFemaleSelected = signupFemaleRadioBtn.isChecked();
            areAllFieldsValid();
        });

        signupBirthDateField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final long DELAY = 500;
            private Runnable runnable;
            private final String REGEX = "^(19|20)\\d\\d-(0[1-9]|1[0-2]|[1-9])-(0[1-9]|[12][0-9]|3[01]|[1-9])$";
            private final Pattern pattern = Pattern.compile(REGEX);

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(runnable);

                runnable = () -> {
                    String birthDate = s.toString();
                    Matcher matcher = pattern.matcher(birthDate);

                    if (matcher.matches()) {
                        isBirthDateValid = true;
                    } else {
                        isBirthDateValid = false;
                        signupBirthDateField.setError("Invalid birth date!");
                    }

                    areAllFieldsValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signupBirthDateField.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        signupAddressField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private final long DELAY = 500;
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(runnable);

                runnable = () -> {
                    String address = s.toString();

                    if (address.length() >= 8) {
                        isAddressValid = true;
                    } else {
                        isAddressValid = false;
                        signupAddressField.setError("Invalid address!");
                    }

                    areAllFieldsValid();
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        signupSelectProfilePicBtn.setOnClickListener(v -> {
            openImagePicker();
        });

        signupSubmitBtn.setOnClickListener(v -> {
            signupSubmitBtn.setEnabled(false);

            try {
                if (serverAPIs == null) {
                    serverAPIs = new ServerAPIs(this);
                }
                String email = getIntent().getStringExtra("email");
                String phoneNumber = getIntent().getStringExtra("phoneNumber");
                String password = getIntent().getStringExtra("password");
                String confirmPassword = getIntent().getStringExtra("confirmPassword");
                String lastName = signupLastNameField.getText().toString();
                String firstName = signupFirstNameField.getText().toString();
                String middleName = signupMiddleNameField.getText().toString();
                String birthDate = signupBirthDateField.getText().toString();
                String address = signupAddressField.getText().toString();

                JSONObject form = new JSONObject();
                form.put("formId", formId);
                form.put("email", email);
                form.put("phoneNumber", phoneNumber);
                form.put("password", password);
                form.put("confirmPassword", confirmPassword);
                form.put("lastName", lastName);
                form.put("firstName", firstName);
                form.put("middleName", middleName);
                form.put("sex", sex);
                form.put("birthDate", birthDate);
                form.put("address", address);
                form.put("profilePic", Base64.getEncoder().encodeToString(profilePicData));

                serverAPIs.submitUserRegistrationForm(form);
            } catch (Exception e) {
                Commons.toastMessage(this, "Something went wrong!");
                signupSubmitBtn.setEnabled(true);
            }
        });
    }

    private void showDatePickerDialog() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        signupBirthDateField.setText(String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private boolean areAllFieldsValid() {
        boolean result = isLastNameValid &&
                isFirstNameValid &&
                isMiddleNameValid &&
                (isMaleSelected || isFemaleSelected) && // At least one gender must be selected
                isBirthDateValid &&
                isAddressValid &&
                isProfilePicValid;
        signupSubmitBtn.setEnabled(result);
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                profilePicData = Converter.bitmapToByteArray(bitmap);
                signupProfilePic.setImageBitmap(bitmap);
                isProfilePicValid = true;
            } catch (IOException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }
        }
    }
}
