package com.codedead.advancedpassgen.ui.info;

import static com.codedead.advancedpassgen.utils.UtilController.openWebsite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.codedead.advancedpassgen.databinding.FragmentInfoBinding;
import com.codedead.advancedpassgen.utils.UtilController;

public class InfoFragment extends Fragment {

    private FragmentInfoBinding binding;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {

        binding = FragmentInfoBinding.inflate(inflater, container, false);
        final View root = binding.getRoot();

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

        if (getActivity() != null)
            UtilController.applyFlagSecure(getActivity(), false);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
