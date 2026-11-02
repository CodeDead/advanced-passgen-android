package com.codedead.advancedpassgen.domain;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codedead.advancedpassgen.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class PasswordViewHolder extends RecyclerView.ViewHolder {

    private final TextView passwordTextView;
    private final LinearProgressIndicator strengthProgressBar;
    private final TextView strengthLabel;
    private final TextView lengthLabel;
    private final ImageButton copyButton;

    /**
     * Initialize a new PasswordViewHolder
     *
     * @param itemView The {@link View} to be used
     */
    public PasswordViewHolder(@NonNull final View itemView) {
        super(itemView);

        passwordTextView = itemView.findViewById(R.id.password_text);
        strengthProgressBar = itemView.findViewById(R.id.password_strength);
        strengthLabel = itemView.findViewById(R.id.password_strength_label);
        lengthLabel = itemView.findViewById(R.id.password_length);
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
     * Get the password length label {@link TextView}
     *
     * @return The password length label {@link TextView}
     */
    public TextView getLengthLabel() {
        return lengthLabel;
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
