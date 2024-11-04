package group.intelliboys.smms.services.remote;

import android.annotation.SuppressLint;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import group.intelliboys.smms.models.data.TravelHistory;
import group.intelliboys.smms.models.data.User;
import group.intelliboys.smms.models.forms.UserCredential;
import group.intelliboys.smms.services.Utils;
import group.intelliboys.smms.services.local.LocalDbStatusUpdateService;
import group.intelliboys.smms.services.local.LocalDbTravelHistoryService;
import group.intelliboys.smms.services.local.LocalDbUserService;
import okhttp3.OkHttpClient;

public class DataSynchronizationService {
    private final OkHttpClient httpClient;
    private final Fragment fragment;

    public DataSynchronizationService(Fragment fragment) {
        httpClient = new OkHttpClient();
        this.fragment = fragment;
    }

    public void synchronizeUserData() throws JsonProcessingException, JSONException {
        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(fragment.requireActivity()
                .getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;
        String token = Objects.requireNonNull(Utils.getInstance().getLoggedInUser().getUserModel()
                .getValue()).getAuthToken();

        RemoteUserService remoteUserService = new RemoteUserService(fragment.requireActivity());
        LocalDbUserService userService = new LocalDbUserService(fragment.requireActivity());
        LocalDbTravelHistoryService travelHistoryService = new LocalDbTravelHistoryService(fragment.requireActivity());

        UserCredential credential = UserCredential.builder()
                .token(token)
                .deviceId(deviceId)
                .deviceName(deviceName)
                .build();

        User user = Utils.getInstance().getLoggedInUser()
                .getUserModel().getValue();
        long userVersion = userService.getUserAccountVersion(user.getEmail());
        user.setVersion(userVersion);

        List<TravelHistory> travelHistories = travelHistoryService.getTravelHistoriesByUser(user.getEmail());
        user.setTravelHistories(travelHistories);

        LocalDbStatusUpdateService statusUpdateService = new LocalDbStatusUpdateService(fragment.requireActivity());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String jsonStr = mapper.writeValueAsString(user);
        JSONObject json = new JSONObject(jsonStr);
        Log.i("", json.toString());

        remoteUserService.synchronizedData(credential, json);
    }
}
