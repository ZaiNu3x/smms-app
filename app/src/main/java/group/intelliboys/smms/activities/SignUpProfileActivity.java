package group.intelliboys.smms.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.intelliboys.smms.R;
import group.intelliboys.smms.configs.CustomOkHttpClient;
import group.intelliboys.smms.configs.NetworkConfig;
import group.intelliboys.smms.models.forms.SignUpForm;
import group.intelliboys.smms.models.results.RegistrationResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpProfileActivity extends AppCompatActivity {

    private EditText lastNameEditTxt;
    private EditText firstNameEditTxt;
    private EditText middleNameEditTxt;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private EditText birthDatePicker;
    private EditText addressEditTxt;
    private ImageView profileView;
    private Button selectProfilePicBtn;
    private Button submitButton;
    private byte[] profilePic;
    private OkHttpClient okHttpClient;
    private String ipAddress;

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable drawable;

    @SuppressLint({"MissingInflatedId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user_profile);

        ipAddress = NetworkConfig.getInstance().getServerIpAddress();
        lastNameEditTxt = findViewById(R.id.lastNameEditTxt);
        firstNameEditTxt = findViewById(R.id.firstNameEditTxt);
        middleNameEditTxt = findViewById(R.id.middleNameEditTxt);
        maleRadioButton = findViewById(R.id.maleRadioBtn);
        femaleRadioButton = findViewById(R.id.femaleRadioBtn);
        birthDatePicker = findViewById(R.id.birthDatePicker);
        addressEditTxt = findViewById(R.id.addressEditTxt);
        profileView = findViewById(R.id.profileView);
        selectProfilePicBtn = findViewById(R.id.selectProfileBtn);
        submitButton = findViewById(R.id.submitBtn);

        okHttpClient = CustomOkHttpClient.getOkHttpClient(getApplicationContext());

        drawable = getDrawable(R.drawable.error);
        assert drawable != null;
        drawable.setBounds(0, 0, 45, 45);

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        @SuppressLint("SetTextI18n")
        DatePickerDialog datePicker = new DatePickerDialog(this, (view, y, m, d) -> {
            birthDatePicker.setText(LocalDate.of(y, (m + 1), d).toString());
        }, year, month, day);

        selectProfilePicBtn.setOnClickListener(btn -> {
            // CODE FOR SELECTING IMAGE FROM GALLERY
            openImagePicker();
        });

        lastNameEditTxt.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private final long DELAY = 500;

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
                    String lastName = lastNameEditTxt.getText().toString();

                    boolean containsNumber = false;
                    boolean containsSpecialChar;

                    for (char c : lastName.toCharArray()) {
                        if (Character.isDigit(c)) {
                            containsNumber = true;
                        }
                    }

                    Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\sñ]");
                    Matcher matcher = pattern.matcher(lastName);

                    containsSpecialChar = matcher.find();

                    if (containsNumber || containsSpecialChar) {
                        runOnUiThread(() -> {
                            lastNameEditTxt.setError("Lastname should not contain number \n" +
                                    "or special characters!", drawable);
                        });
                    } else {
                        runOnUiThread(() -> {
                            lastNameEditTxt.setError(null);
                        });
                    }
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        firstNameEditTxt.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private final long DELAY = 500;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler.removeCallbacks(runnable);

                runnable = () -> {
                    String firstName = firstNameEditTxt.getText().toString();

                    boolean containsNumber = false;
                    boolean containsSpecialChar = false;

                    for (char c : firstName.toCharArray()) {
                        if (Character.isDigit(c)) {
                            containsNumber = true;
                        }
                    }

                    Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\sñ]");
                    Matcher matcher = pattern.matcher(firstName);

                    containsSpecialChar = matcher.find();

                    if (containsNumber || containsSpecialChar) {
                        runOnUiThread(() -> {
                            firstNameEditTxt.setError("Firstname should not contain number \n" +
                                    "or special characters!", drawable);
                        });
                    } else {
                        runOnUiThread(() -> {
                            firstNameEditTxt.setError(null);
                        });
                    }
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        middleNameEditTxt.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private final long DELAY = 500;

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
                    String middleName = middleNameEditTxt.getText().toString();

                    boolean containsNumber = false;
                    boolean containsSpecialChar = false;

                    for (char c : middleName.toCharArray()) {
                        if (Character.isDigit(c)) {
                            containsNumber = true;
                        }
                    }

                    Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\sñ]");
                    Matcher matcher = pattern.matcher(middleName);

                    containsSpecialChar = matcher.find();

                    if (containsNumber || containsSpecialChar) {
                        runOnUiThread(() -> {
                            middleNameEditTxt.setError("Middlename should not contain number \n" +
                                    "or special characters!", drawable);
                        });
                    } else {
                        runOnUiThread(() -> {
                            middleNameEditTxt.setError(null);
                        });
                    }
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        birthDatePicker.setOnClickListener((clicked) -> {
            datePicker.show();
        });

        addressEditTxt.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private final long DELAY = 500;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler.removeCallbacks(runnable);

                runnable = () -> {

                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        submitButton.setOnClickListener(btn -> {
            String formId = getIntent().getStringExtra("formId");
            String email = getIntent().getStringExtra("email");
            String phoneNumber = getIntent().getStringExtra("phoneNumber");
            String password = getIntent().getStringExtra("password");
            String confirmPassword = getIntent().getStringExtra("confirmPassword");
            String lastName = lastNameEditTxt.getText().toString();
            String firstName = firstNameEditTxt.getText().toString();
            String middleName = middleNameEditTxt.getText().toString();
            char sex = '0';

            if (maleRadioButton.isChecked()) {
                sex = 'm';
            } else if (femaleRadioButton.isChecked()) {
                sex = 'f';
            }

            LocalDate birthDate = LocalDate.parse(birthDatePicker.getText());
            String address = addressEditTxt.getText().toString();

            SignUpForm form = new SignUpForm();

            form.setFormId(formId);
            form.setEmail(email);
            form.setPhoneNumber(phoneNumber);
            form.setPassword(password);
            form.setConfirmPassword(confirmPassword);
            form.setLastName(lastName);
            form.setFirstName(firstName);
            form.setMiddleName(middleName);
            form.setSex(sex);
            form.setBirthDate(birthDate);
            form.setAddress(address);
            form.setProfilePic(profilePic);

            try {
                doVerify(form);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void doVerify(SignUpForm regForm) throws JsonProcessingException {
        final String LOGIN_URL = ipAddress + "/register/submit";
        final MediaType JSON = MediaType.get("application/json");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String form = mapper.writeValueAsString(regForm);
        RequestBody requestBody = RequestBody.create(form, JSON);

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String responseBody = response.body().string();

                ObjectMapper mapper = new ObjectMapper();

                RegistrationResult result = mapper.readValue(responseBody, RegistrationResult.class);

                if (result != null) {
                    if (result.getStatus().equals("NEED_VERIFICATION")) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Please Verify your Registration!", Toast.LENGTH_LONG).show();
                        });

                        Intent intent = new Intent(getApplicationContext(), SignUpVerificationActivity.class);
                        intent.putExtra("formId", result.getFormId());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private static final int PICK_IMAGE_REQUEST = 1;

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                profilePic = readBytes(imageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            profileView.setImageURI(imageUri);
        }
    }

    public byte[] readBytes(Uri uri) throws IOException {
        // this dynamically extends to take the bytes you read
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        inputStream.close();

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }
}
