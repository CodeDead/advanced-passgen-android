package com.codedead.advancedpassgen.ui.info;

import static com.codedead.advancedpassgen.utils.UtilController.openWebsite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.codedead.advancedpassgen.databinding.FragmentInfoBinding;

public class InfoFragment extends Fragment {

    private FragmentInfoBinding binding;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        final InfoViewModel infoViewModel = new ViewModelProvider(this).get(InfoViewModel.class);

        binding = FragmentInfoBinding.inflate(inflater, container, false);
        final View root = binding.getRoot();

        final TextView textView = binding.textGallery;
        infoViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final ImageButton btnFacebook = binding.BtnFacebook;
        final ImageButton btnBluesky = binding.BtnBluesky;
        final ImageButton btnWebsite = binding.BtnWebsiteAbout;

        btnFacebook.setOnClickListener(v -> {
            if (getContext() == null)
                return;
            openWebsite(getContext(), "https://facebook.com/deadlinecodedead/");
        });

        btnBluesky.setOnClickListener(v -> {
            if (getContext() == null)
                return;
            openWebsite(getContext(), "https://bsky.app/profile/codedead.com");
        });

        btnWebsite.setOnClickListener(v -> {
            if (getContext() == null)
                return;
            openWebsite(getContext(), "https://codedead.com/");
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}