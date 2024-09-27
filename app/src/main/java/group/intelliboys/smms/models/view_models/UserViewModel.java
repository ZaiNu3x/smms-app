package group.intelliboys.smms.models.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import group.intelliboys.smms.models.data.User;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> userModel = new MutableLiveData<>();

    public LiveData<User> getUserModel() {
        return userModel;
    }

    public void updateUserModel(User user) {
        userModel.setValue(user);
    }
}
