package com.codedead.advancedpassgen.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codedead.advancedpassgen.databinding.FragmentHomeBinding;
import com.codedead.advancedpassgen.domain.PasswordAdapter;
import com.codedead.advancedpassgen.domain.PasswordItem;
import com.codedead.advancedpassgen.utils.UtilController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView recyclerView = binding.recyclerView;

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final List<PasswordItem> items = new ArrayList<>();
        recyclerView.setAdapter(new PasswordAdapter(requireContext(), items));

        binding.fabAdd.setOnClickListener(view -> {
            binding.textHome.setVisibility(View.GONE);
            // Generate a random number
            int random = new Random().nextInt();
            items.add(new PasswordItem("password" + random, random));
            ((PasswordAdapter) Objects.requireNonNull(recyclerView.getAdapter())).add();
        });

        binding.fabClear.setOnClickListener(view -> {
            items.clear();
            binding.textHome.setVisibility(View.VISIBLE);
            ((PasswordAdapter) Objects.requireNonNull(recyclerView.getAdapter())).clear();
        });

        if (getActivity() != null)
            UtilController.applyFlagSecure(getActivity(), true);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
