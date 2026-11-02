package com.codedead.advancedpassgen.domain;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codedead.advancedpassgen.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordViewHolder> {

    private static final String SORT_NONE = "none";
    private static final String SORT_SHORTEST_FIRST = "shortest_first";
    private static final String SORT_LONGEST_FIRST = "longest_first";

    private final Context context;
    private final List<PasswordItem> items;
    private final List<PasswordItem> generationOrder;
    private boolean displayStrength;
    private String sortOrder;

    /**
     * Initialize a new PasswordAdapter
     *
     * @param context The {@link Context} object
     */
    public PasswordAdapter(final Context context) {
        this.context = context;
        this.items = new ArrayList<>();
        this.generationOrder = new ArrayList<>();
        this.displayStrength = true;
        this.sortOrder = SORT_NONE;
    }

    /**
     * Update the adapter with new items
     *
     * @param newItems The {@link List} of {@link PasswordItem} objects
     */
    @SuppressLint("NotifyDataSetChanged")
    public void insert(final List<PasswordItem> newItems) {
        items.addAll(newItems);
        generationOrder.addAll(newItems);

        if (!SORT_NONE.equals(sortOrder)) {
            sortItemsByLength();
        }

        notifyDataSetChanged();
    }

    /**
     * Add an item to the adapter
     *
     * @param item The {@link PasswordItem} to add
     * @return The position of the added item, or {@link RecyclerView#NO_POSITION}
     */
    @SuppressLint("NotifyDataSetChanged")
    public int add(final PasswordItem item) {
        items.add(item);
        generationOrder.add(item);

        if (SORT_NONE.equals(sortOrder)) {
            final int position = items.size() - 1;
            notifyItemInserted(position);
            return position;
        }

        sortItemsByLength();
        notifyDataSetChanged();
        return indexOfIdentity(item);
    }

    /**
     * Clear the adapter
     */
    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        items.clear();
        generationOrder.clear();
        notifyDataSetChanged();
    }

    /**
     * Set the order in which passwords are displayed
     *
     * @param sortOrder One of "none", "shortest_first", or "longest_first"
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setSortOrder(final String sortOrder) {
        final String normalizedSortOrder = normalizeSortOrder(sortOrder);

        if (normalizedSortOrder.equals(this.sortOrder)) {
            return;
        }

        this.sortOrder = normalizedSortOrder;

        if (items.isEmpty()) {
            return;
        }

        if (SORT_NONE.equals(this.sortOrder)) {
            items.clear();
            items.addAll(generationOrder);
        } else {
            sortItemsByLength();
        }

        notifyDataSetChanged();
    }

    /**
     * Called when RecyclerView needs a new {@link PasswordViewHolder} of the given type to represent
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new {@link PasswordViewHolder} that holds a View of the given view type.
     */
    @NonNull
    @Override
    public PasswordViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new PasswordViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.password_item_view, parent, false));
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
        final int strength = item.strength();
        final int strengthColor = ContextCompat.getColor(context, getStrengthColorRes(strength));
        final int passwordLength = item.password().length();

        holder.getPasswordTextView().setText(item.password());
        holder.getStrengthProgressBar().setProgressCompat(strength, false);
        holder.getStrengthProgressBar().setIndicatorColor(strengthColor);
        holder.getStrengthLabel().setText(getStrengthLabelRes(strength));
        holder.getStrengthLabel().setTextColor(strengthColor);
        holder.getLengthLabel().setText(context.getResources().getQuantityString(
                R.plurals.password_length, passwordLength, passwordLength));
        holder.getStrengthProgressBar().setVisibility(displayStrength ? View.VISIBLE : View.GONE);
        holder.getStrengthLabel().setVisibility(displayStrength ? View.VISIBLE : View.GONE);

        holder.getCopyButton().setOnClickListener(e -> {
            // Copy the password to the clipboard
            final ClipboardManager clipboard = ContextCompat.getSystemService(context, ClipboardManager.class);
            final ClipData clip = ClipData.newPlainText("Advanced PassGen", holder.getPasswordTextView().getText());

            // If Android level is above android 33
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                final PersistableBundle extras = new PersistableBundle();
                extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true);
                clip.getDescription().setExtras(extras);
            } else {
                PersistableBundle extras = new PersistableBundle();
                extras.putBoolean("android.content.extra.IS_SENSITIVE", true);
                clip.getDescription().setExtras(extras);
            }

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

    /**
     * Get a copy of the items in the adapter
     *
     * @return A {@link List} of {@link PasswordItem} objects
     */
    public List<PasswordItem> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * Sort the displayed items by password length
     */
    private void sortItemsByLength() {
        final Comparator<PasswordItem> byLength = Comparator.comparingInt(
                (PasswordItem item) -> item.password().length());

        items.sort(SORT_LONGEST_FIRST.equals(sortOrder) ? byLength.reversed() : byLength);
    }

    /**
     * Normalize a sort order value
     *
     * @param sortOrder The sort order value to normalize
     * @return One of the supported sort order values
     */
    private static String normalizeSortOrder(final String sortOrder) {
        if (SORT_SHORTEST_FIRST.equals(sortOrder) || SORT_LONGEST_FIRST.equals(sortOrder)) {
            return sortOrder;
        }

        return SORT_NONE;
    }

    /**
     * Find the position of an item by object identity
     *
     * @param item The item to find
     * @return The position of the item, or {@link RecyclerView#NO_POSITION}
     */
    private int indexOfIdentity(final PasswordItem item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == item) {
                return i;
            }
        }

        return RecyclerView.NO_POSITION;
    }

    /**
     * Set the display strength setting
     *
     * @param displayStrength The display strength setting
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setDisplayStrength(final boolean displayStrength) {
        if (this.displayStrength == displayStrength) {
            return;
        }

        this.displayStrength = displayStrength;
        notifyDataSetChanged();
    }

    /**
     * Get the color resource for a password strength
     *
     * @param strength The strength of the password
     * @return The color resource identifier
     */
    private static int getStrengthColorRes(final int strength) {
        if (strength >= 100) {
            return R.color.strength_strong;
        }
        if (strength >= 76) {
            return R.color.strength_good;
        }
        if (strength >= 36) {
            return R.color.strength_fair;
        }
        return R.color.strength_weak;
    }

    /**
     * Get the label resource for a password strength
     *
     * @param strength The strength of the password
     * @return The string resource identifier
     */
    @StringRes
    private static int getStrengthLabelRes(final int strength) {
        if (strength >= 100) {
            return R.string.strength_strong;
        }
        if (strength >= 76) {
            return R.string.strength_good;
        }
        if (strength >= 36) {
            return R.string.strength_fair;
        }
        return R.string.strength_weak;
    }
}
