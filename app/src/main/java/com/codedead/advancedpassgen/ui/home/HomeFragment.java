package com.codedead.advancedpassgen.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codedead.advancedpassgen.R;
import com.codedead.advancedpassgen.databinding.FragmentHomeBinding;
import com.codedead.advancedpassgen.domain.PasswordAdapter;
import com.codedead.advancedpassgen.domain.PasswordExporter;
import com.codedead.advancedpassgen.domain.PasswordGenerator;
import com.codedead.advancedpassgen.domain.PasswordItem;
import com.codedead.advancedpassgen.utils.UtilController;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private List<PasswordItem> pendingExportItems;

    private final ActivityResultLauncher<String> exportTxtLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("text/plain"),
            uri -> handleExportResult(uri, PasswordExporter.Format.TXT));

    private final ActivityResultLauncher<String> exportCsvLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("text/csv"),
            uri -> handleExportResult(uri, PasswordExporter.Format.CSV));

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

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateExportFabVisibility();
            }

            @Override
            public void onItemRangeInserted(final int positionStart, final int itemCount) {
                updateExportFabVisibility();
            }

            @Override
            public void onItemRangeRemoved(final int positionStart, final int itemCount) {
                updateExportFabVisibility();
            }
        });

        binding.fabExport.setOnClickListener(view -> showExportFormatDialog());
        updateExportFabVisibility();

        binding.fabAdd.setOnClickListener(view -> {
            binding.textHome.setVisibility(View.GONE);
            binding.swipeRefresh.setVisibility(View.VISIBLE);

            binding.fabClear.setEnabled(false);
            binding.fabRefresh.setEnabled(false);
            binding.fabAdd.setEnabled(false);
            binding.fabExport.setEnabled(false);

            final PasswordItem item = PasswordGenerator.generatePasswords(minimumLength, maximumLength, 1, smallLetters, capitalLetters, numbers, specialCharacters, brackets, spaces, customCharacterSet).get(0);

            adapter.add(item);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);

            binding.fabClear.setEnabled(true);
            binding.fabRefresh.setEnabled(true);
            binding.fabAdd.setEnabled(true);
            binding.fabExport.setEnabled(true);
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
        binding.fabExport.setEnabled(false);

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
                    binding.fabExport.setEnabled(true);

                    binding.swipeRefresh.setRefreshing(false);
                    binding.textHome.setRefreshing(false);
                });
            });
        }
    }

    /**
     * Show or hide the export button based on the number of passwords
     */
    private void updateExportFabVisibility() {
        if (binding == null || adapter == null) {
            return;
        }

        binding.fabExport.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * Show a dialog which allows the user to pick an export format
     */
    private void showExportFormatDialog() {
        if (adapter == null || adapter.getItemCount() == 0) {
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.export_passwords)
                .setItems(new CharSequence[]{
                        getString(R.string.export_txt),
                        getString(R.string.export_csv)
                }, (dialog, which) -> {
                    if (which == 0) {
                        startExport(PasswordExporter.Format.TXT);
                    } else {
                        startExport(PasswordExporter.Format.CSV);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /**
     * Start an export by asking the user where to save the file
     *
     * @param format The {@link PasswordExporter.Format} to export the passwords in
     */
    private void startExport(final PasswordExporter.Format format) {
        if (adapter == null) {
            return;
        }

        pendingExportItems = adapter.getItems();

        if (pendingExportItems.isEmpty()) {
            return;
        }

        final String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        final String filename = "passwords_" + timestamp;

        if (format == PasswordExporter.Format.TXT) {
            exportTxtLauncher.launch(filename + ".txt");
        } else {
            exportCsvLauncher.launch(filename + ".csv");
        }
    }

    /**
     * Handle the result of the file picker
     *
     * @param uri    The {@link Uri} of the file to export to, or null if the user cancelled
     * @param format The {@link PasswordExporter.Format} to export the passwords in
     */
    private void handleExportResult(final Uri uri, final PasswordExporter.Format format) {
        if (uri == null) {
            pendingExportItems = null;
            return;
        }

        writeExport(uri, format);
    }

    /**
     * Write the pending passwords to the selected file
     *
     * @param uri    The {@link Uri} of the file to export to
     * @param format The {@link PasswordExporter.Format} to export the passwords in
     */
    private void writeExport(final Uri uri, final PasswordExporter.Format format) {
        final List<PasswordItem> items = pendingExportItems;
        pendingExportItems = null;

        if (items == null || items.isEmpty()) {
            return;
        }

        final Context context = getContext();
        if (context == null) {
            return;
        }

        final Context applicationContext = context.getApplicationContext();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            try {
                final OutputStream outputStream = applicationContext.getContentResolver().openOutputStream(uri, "wt");
                if (outputStream == null) {
                    throw new IOException("Unable to open the export file");
                }

                try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
                    PasswordExporter.write(writer, items, format);
                }

                showExportResult(applicationContext, true);
            } catch (final Exception ex) {
                Log.e(HomeFragment.class.getSimpleName(), "An error occurred while exporting passwords: " + ex.getMessage());
                showExportResult(applicationContext, false);
            } finally {
                executorService.shutdown();
            }
        });
    }

    /**
     * Show the result of an export
     *
     * @param context The {@link Context} that can be used to show the toast
     * @param success Whether the export was successful
     */
    private static void showExportResult(final Context context, final boolean success) {
        final int message = success ? R.string.export_success : R.string.export_failed;

        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
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
