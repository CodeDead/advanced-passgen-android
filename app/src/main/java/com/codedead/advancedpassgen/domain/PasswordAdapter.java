package com.codedead.advancedpassgen.domain;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codedead.advancedpassgen.R;

import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordViewHolder> {

    private final Context context;
    private final List<PasswordItem> items;

    /**
     * Initialize a new PasswordAdapter
     *
     * @param context The {@link Context} object
     * @param items   The {@link List} of {@link PasswordItem} objects
     */
    public PasswordAdapter(final Context context, final List<PasswordItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public PasswordViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new PasswordViewHolder(LayoutInflater.from(context).inflate(R.layout.password_item_view, parent, false));
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull final PasswordViewHolder holder, final int position) {
        final PasswordItem item = items.get(position);

        holder.getPasswordTextView().setText(item.getPassword());
        holder.getStrengthProgressBar().setProgress(item.getStrength());

        holder.getCopyButton().setOnClickListener(e -> {
            // Copy the password to the clipboard
            ClipboardManager clipboard = ContextCompat.getSystemService(context, ClipboardManager.class);
            ClipData clip = ClipData.newPlainText("Advanced PassGen", holder.getPasswordTextView().getText());

            assert clipboard != null;
            clipboard.setPrimaryClip(clip);

            // Show a toast message
            Toast.makeText(context, R.string.password_copied, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Get the number of items in the adapter
     *
     * @return The number of items in the adapter
     */
    @Override
    public int getItemCount() {
        return items.size();
    }
}
