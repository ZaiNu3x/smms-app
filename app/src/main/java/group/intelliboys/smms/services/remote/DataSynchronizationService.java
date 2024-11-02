package group.intelliboys.smms.services.remote;

import androidx.fragment.app.Fragment;

import okhttp3.OkHttpClient;

public class DataSynchronizationService {
    private final OkHttpClient httpClient;
    private final Fragment fragment;

    public DataSynchronizationService(Fragment fragment) {
        httpClient = new OkHttpClient();
        this.fragment = fragment;
    }

    public void synchronizeUserData() {

    }
}
