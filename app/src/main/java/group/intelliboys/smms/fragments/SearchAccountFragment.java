package group.intelliboys.smms.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import group.intelliboys.smms.R;
import group.intelliboys.smms.configs.CustomOkHttpClient;
import group.intelliboys.smms.configs.NetworkConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchAccountFragment extends Fragment {
    private EditText searchAccountEditTxt;
    private Button searchAccountBtn;
    private OkHttpClient okHttpClient;
    private String ipAddress;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_account, container, false);
        searchAccountEditTxt = view.findViewById(R.id.searchAccountEditTxt);
        searchAccountBtn = view.findViewById(R.id.searchAccountBtn);
        okHttpClient = CustomOkHttpClient.getOkHttpClient(requireContext());

        if (NetworkConfig.getInstance().isNetworkActive()) {
            ipAddress = NetworkConfig.getInstance().getServerIpAddress();
        }

        searchAccountEditTxt.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;
            private final long DELAY = 500;
            private boolean isEmailValid;

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
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = requireActivity().getDrawable(R.drawable.error);
                    assert drawable != null;
                    drawable.setBounds(0, 0, 45, 45);

                    isEmailValid = Patterns.EMAIL_ADDRESS.matcher(s).matches();

                    if (!isEmailValid) {
                        searchAccountEditTxt.setError("INVALID EMAIL ADDRESS!", drawable);
                        searchAccountBtn.setEnabled(false);
                    } else {
                        searchAccountEditTxt.setError(null);
                        searchAccountBtn.setEnabled(true);
                    }
                };

                handler.postDelayed(runnable, DELAY);
            }
        });

        searchAccountBtn.setOnClickListener(v -> {
            String email = searchAccountEditTxt.getText().toString();
            searchAccount(email);
        });

        return view;
    }

    public void searchAccount(String email) {
        final String URL = ipAddress + "/forgot-password/search-account/" + email;

        Request request = new Request.Builder()
                .get()
                .url(URL)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                ObjectMapper mapper = new ObjectMapper();
                String body = response.body().string();
                Map<String, Object> map = mapper.readValue(body, Map.class);

                if ((boolean) map.get("isExists")) {
                    String id = (String) map.get("id");
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.forgotPasswordContainer, ForgotPasswordVerificationFragment.class, bundle)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                } else if (!(boolean) map.get("isExists")) {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    Drawable drawable = requireActivity().getDrawable(R.drawable.error);
                    assert drawable != null;
                    drawable.setBounds(0, 0, 45, 45);

                    requireActivity().runOnUiThread(() -> {
                        searchAccountEditTxt.setError("EMAIL NOT FOUND", drawable);
                    });
                }
            }
        });
    }
}
