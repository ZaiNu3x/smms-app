package group.intelliboys.smms.fragments.forgot_password;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.intelliboys.smms.R;
import group.intelliboys.smms.utils.ServerAPIs;
import lombok.Getter;

public class SearchAccountFragment extends Fragment {
    @Getter
    private EditText searchAccountField;
    private Button searchAccountBtn;
    private ServerAPIs serverAPIs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_account, container, false);
        searchAccountField = view.findViewById(R.id.searchAccountEditTxt);
        searchAccountBtn = view.findViewById(R.id.searchAccountBtn);
        serverAPIs = new ServerAPIs(requireActivity());

        searchAccountField.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private final long DELAY = 500;
            private final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            private final Pattern pattern = Pattern.compile(EMAIL_REGEX);

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
                    String email = s.toString();
                    Matcher matcher = pattern.matcher(email);

                    if (matcher.matches()) {
                        searchAccountBtn.setEnabled(true);
                        searchAccountField.setError(null);
                    } else {
                        searchAccountField.setError("Invalid Email");
                        searchAccountBtn.setEnabled(false);
                    }
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        searchAccountBtn.setOnClickListener(v -> {
            String email = searchAccountField.getText().toString();
            serverAPIs.forgotPasswordSearchAccount(email);
        });

        return view;
    }
}
