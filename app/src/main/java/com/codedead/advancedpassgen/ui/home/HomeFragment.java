package com.codedead.advancedpassgen.ui.home;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codedead.advancedpassgen.databinding.FragmentHomeBinding;
import com.codedead.advancedpassgen.domain.LocaleHelper;
import com.codedead.advancedpassgen.domain.PasswordAdapter;
import com.codedead.advancedpassgen.domain.PasswordGenerator;
import com.codedead.advancedpassgen.domain.PasswordItem;
import com.codedead.advancedpassgen.utils.UtilController;

import java.util.List;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SharedPreferences sharedPreferences;
    private String lastLanguage;
    private String customCharacterSet;
    private int minimumLength;
    private int maximumLength;
    private int passwordAmount;
    private boolean smallLetters;
    private boolean capitalLetters;
    private boolean spaces;
    private boolean specialCharacters;
    private boolean numbers;
    private boolean brackets;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView recyclerView = binding.recyclerView;
        final PasswordAdapter adapter = new PasswordAdapter(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        binding.fabAdd.setOnClickListener(view -> {
            binding.textHome.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);

            binding.fabClear.setEnabled(false);
            binding.fabRefresh.setEnabled(false);
            binding.fabAdd.setEnabled(false);

            final PasswordItem item = PasswordGenerator.generatePasswords(minimumLength, maximumLength, 1, smallLetters, capitalLetters, numbers, specialCharacters, brackets, spaces, customCharacterSet).get(0);

            adapter.add(item);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);

            binding.fabClear.setEnabled(true);
            binding.fabRefresh.setEnabled(true);
            binding.fabAdd.setEnabled(true);
        });

        binding.fabClear.setOnClickListener(view -> {
            adapter.clear();

            binding.textHome.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        });

        binding.fabRefresh.setOnClickListener(view -> {
            binding.textHome.setVisibility(View.GONE);
            binding.loadingLayout.setVisibility(View.VISIBLE);

            binding.fabClear.setEnabled(false);
            binding.fabRefresh.setEnabled(false);
            binding.fabAdd.setEnabled(false);

            Executors.newSingleThreadExecutor().execute(() -> {
                final List<PasswordItem> items = PasswordGenerator.generatePasswords(minimumLength, maximumLength, passwordAmount, smallLetters, capitalLetters, numbers, specialCharacters, brackets, spaces, customCharacterSet);
                requireActivity().runOnUiThread(() -> {
                    if (binding == null)
                        return;

                    binding.loadingLayout.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    adapter.insert(items);

                    binding.fabClear.setEnabled(true);
                    binding.fabRefresh.setEnabled(true);
                    binding.fabAdd.setEnabled(true);
                });
            });
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        lastLanguage = sharedPreferences.getString("appLanguage", "en");
        customCharacterSet = sharedPreferences.getString("customCharacterSet", "");
        minimumLength = Integer.parseInt(sharedPreferences.getString("minimumLength", "8"));
        maximumLength = Integer.parseInt(sharedPreferences.getString("maximumLength", "30"));
        passwordAmount = Integer.parseInt(sharedPreferences.getString("passwordAmount", "100"));
        smallLetters = sharedPreferences.getBoolean("smallLetters", true);
        capitalLetters = sharedPreferences.getBoolean("capitalLetters", true);
        spaces = sharedPreferences.getBoolean("spaces", false);
        specialCharacters = sharedPreferences.getBoolean("specialCharacters", true);
        numbers = sharedPreferences.getBoolean("numbers", true);
        brackets = sharedPreferences.getBoolean("brackets", false);

        final boolean preventScreenshot = sharedPreferences.getBoolean("preventScreenshot", true);
        if (getActivity() != null)
            UtilController.applyFlagSecure(getActivity(), preventScreenshot);

        return root;
    }

    @Override
    public void onResume() {
        final String selectedLanguage = sharedPreferences.getString("appLanguage", "en");

        if (!lastLanguage.equals(selectedLanguage)) {
            LocaleHelper.setLocale(getContext(), selectedLanguage);
            // recreate();
        }

        lastLanguage = sharedPreferences.getString("appLanguage", "en");
        customCharacterSet = sharedPreferences.getString("customCharacterSet", "");
        minimumLength = Integer.parseInt(sharedPreferences.getString("minimumLength", "8"));
        maximumLength = Integer.parseInt(sharedPreferences.getString("maximumLength", "30"));
        passwordAmount = Integer.parseInt(sharedPreferences.getString("passwordAmount", "100"));
        smallLetters = sharedPreferences.getBoolean("smallLetters", true);
        capitalLetters = sharedPreferences.getBoolean("capitalLetters", true);
        spaces = sharedPreferences.getBoolean("spaces", false);
        specialCharacters = sharedPreferences.getBoolean("specialCharacters", true);
        numbers = sharedPreferences.getBoolean("numbers", true);
        brackets = sharedPreferences.getBoolean("brackets", false);

        super.onResume();

        final boolean preventScreenshot = sharedPreferences.getBoolean("preventScreenshot", true);
        if (getActivity() != null)
            UtilController.applyFlagSecure(getActivity(), preventScreenshot);
    }

    @Override
    public void onConfigurationChanged(@NonNull final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.onAttach(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
