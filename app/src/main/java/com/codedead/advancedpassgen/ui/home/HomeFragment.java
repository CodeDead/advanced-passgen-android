package com.codedead.advancedpassgen.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codedead.advancedpassgen.databinding.FragmentHomeBinding;
import com.codedead.advancedpassgen.domain.PasswordAdapter;
import com.codedead.advancedpassgen.domain.PasswordGenerator;
import com.codedead.advancedpassgen.domain.PasswordItem;
import com.codedead.advancedpassgen.utils.UtilController;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentHomeBinding binding;
    private SharedPreferences sharedPreferences;
    private PasswordAdapter adapter;
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
    private int poolSize;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        final View root = binding.getRoot();

        binding.swipeRefresh.setOnRefreshListener(this);
        binding.textHome.setOnRefreshListener(this);

        final RecyclerView recyclerView = binding.recyclerView;
        adapter = new PasswordAdapter(requireContext());
        adapter.setDisplayStrength(sharedPreferences.getBoolean("showPasswordStrength", true));

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        binding.fabAdd.setOnClickListener(view -> {
            binding.textHome.setVisibility(View.GONE);
            binding.swipeRefresh.setVisibility(View.VISIBLE);

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
            binding.swipeRefresh.setVisibility(View.GONE);
        });

        binding.fabRefresh.setOnClickListener(view -> refreshPasswords());
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

        final int numberOfCores = Runtime.getRuntime().availableProcessors();
        poolSize = Math.max(2, Math.min(numberOfCores * 2, 4));

        if (!sharedPreferences.getBoolean("automaticThreading", true)) {
            poolSize = Integer.parseInt(sharedPreferences.getString("poolSize", "1"));
        }

        final boolean preventScreenshot = sharedPreferences.getBoolean("preventScreenshot", true);
        if (getActivity() != null)
            UtilController.applyFlagSecure(getActivity(), preventScreenshot);

        return root;
    }

    /**
     * Refresh the passwords
     */
    private void refreshPasswords() {
        binding.textHome.setVisibility(View.GONE);
        binding.loadingLayout.setVisibility(View.VISIBLE);
        binding.swipeRefresh.setVisibility(View.GONE);

        binding.fabClear.setEnabled(false);
        binding.fabRefresh.setEnabled(false);
        binding.fabAdd.setEnabled(false);

        adapter.clear();

        final int totalNumberOfPasswords = passwordAmount;
        final int numberOfPasswordsPerThread = totalNumberOfPasswords / poolSize;
        final int remainingPasswords = totalNumberOfPasswords % poolSize;

        final ExecutorService executorService = Executors.newFixedThreadPool(Math.min(passwordAmount, poolSize));
        for (int i = 0; i < poolSize; i++) {
            int passwords = numberOfPasswordsPerThread;

            if (i == poolSize - 1) {
                passwords += remainingPasswords;
            }

            int finalPasswords = passwords;
            executorService.execute(() -> {
                final List<PasswordItem> items = PasswordGenerator.generatePasswords(minimumLength, maximumLength, finalPasswords, smallLetters, capitalLetters, numbers, specialCharacters, brackets, spaces, customCharacterSet);

                final FragmentActivity activity = getActivity();
                if (activity == null)
                    return;

                activity.runOnUiThread(() -> {
                    if (binding == null || adapter == null)
                        return;

                    binding.loadingLayout.setVisibility(View.GONE);
                    binding.swipeRefresh.setVisibility(View.VISIBLE);
                    adapter.insert(items);

                    binding.fabClear.setEnabled(true);
                    binding.fabRefresh.setEnabled(true);
                    binding.fabAdd.setEnabled(true);

                    binding.swipeRefresh.setRefreshing(false);
                    binding.textHome.setRefreshing(false);
                });
            });
        }
    }

    @Override
    public void onResume() {
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
        adapter.setDisplayStrength(sharedPreferences.getBoolean("showPasswordStrength", true));
        if (!sharedPreferences.getBoolean("automaticThreading", true)) {
            poolSize = Integer.parseInt(sharedPreferences.getString("poolSize", "1"));
        }

        super.onResume();

        final boolean preventScreenshot = sharedPreferences.getBoolean("preventScreenshot", true);
        if (getActivity() != null)
            UtilController.applyFlagSecure(getActivity(), preventScreenshot);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRefresh() {
        refreshPasswords();
    }
}
