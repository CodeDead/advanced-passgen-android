package com.codedead.advancedpassgen.domain;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codedead.advancedpassgen.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class PasswordViewHolder extends RecyclerView.ViewHolder {

    private final TextView passwordTextView;
    private final LinearLayout strengthContainer;
    private final LinearProgressIndicator strengthProgressBar;
    private final TextView strengthLabel;
    private final ImageButton copyButton;

    /**
     * Initialize a new PasswordViewHolder
     *
     * @param itemView The {@link View} to be used
     */
    public PasswordViewHolder(@NonNull final View itemView) {
        super(itemView);

        passwordTextView = itemView.findViewById(R.id.password_text);
        strengthContainer = itemView.findViewById(R.id.strength_container);
        strengthProgressBar = itemView.findViewById(R.id.password_strength);
        strengthLabel = itemView.findViewById(R.id.password_strength_label);
        copyButton = itemView.findViewById(R.id.copy_button);
    }

    /**
     * Get the password {@link TextView}
     *
     * @return The password {@link TextView}
     */
    public TextView getPasswordTextView() {
        return passwordTextView;
    }

    /**
     * Get the container holding the strength indicator
     *
     * @return The strength {@link LinearLayout}
     */
    public LinearLayout getStrengthContainer() {
        return strengthContainer;
    }

    /**
     * Get the strength {@link LinearProgressIndicator}
     *
     * @return The strength {@link LinearProgressIndicator}
     */
    public LinearProgressIndicator getStrengthProgressBar() {
        return strengthProgressBar;
    }

    /**
     * Get the strength label {@link TextView}
     *
     * @return The strength label {@link TextView}
     */
    public TextView getStrengthLabel() {
        return strengthLabel;
    }

    /**
     * Get the copy {@link ImageButton}
     *
     * @return The copy {@link ImageButton}
     */
    public ImageButton getCopyButton() {
        return copyButton;
    }
}
