package com.codedead.advancedpassgen.ui.support;

import static com.codedead.advancedpassgen.utils.UtilController.openWebsite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;

import com.codedead.advancedpassgen.R;
import com.codedead.advancedpassgen.databinding.FragmentSupportBinding;
import com.codedead.advancedpassgen.utils.UtilController;

public class SupportFragment extends Fragment {

    private FragmentSupportBinding binding;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {

        binding = FragmentSupportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ImageButton btnGitHub = binding.BtnGitHub;
        final ImageButton btnEmail = binding.BtnEmail;

        btnGitHub.setOnClickListener(v -> {
            if (getContext() == null)
                return;
            openWebsite(getContext(), "https://github.com/CodeDead/advanced-passgen-android/issues");
        });

        btnEmail.setOnClickListener(v -> new ShareCompat.IntentBuilder(requireActivity())
                .setType("message/rfc822")
                .addEmailTo("support@codedead.com")
                .setSubject("Advanced PassGen - Android")
                .setText(getString(R.string.text_send_mail))
                .setChooserTitle(getString(R.string.text_send_mail))
                .startChooser());

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
