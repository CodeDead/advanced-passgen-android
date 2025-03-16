package com.codedead.advancedpassgen.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codedead.advancedpassgen.domain.PasswordItem;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<PasswordItem>> passwordItems;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        passwordItems = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<PasswordItem>> getPasswordItems() {
        return passwordItems;
    }
}