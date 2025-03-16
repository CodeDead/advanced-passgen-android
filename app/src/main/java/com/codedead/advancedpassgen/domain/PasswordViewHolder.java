package com.codedead.advancedpassgen.domain;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codedead.advancedpassgen.R;

public class PasswordViewHolder extends RecyclerView.ViewHolder {

    private TextView passwordTextView;
    private ProgressBar strengthProgressBar;
    private ImageButton copyButton;

    /**
     * Initialize a new PasswordViewHolder
     *
     * @param itemView The {@link View} to be used
     */
    public PasswordViewHolder(@NonNull final View itemView) {
        super(itemView);

        passwordTextView = itemView.findViewById(R.id.password_text);
        strengthProgressBar = itemView.findViewById(R.id.password_strength);
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
     * Set the password {@link TextView}
     *
     * @param passwordTextView The password {@link TextView}
     */
    public void setPasswordTextView(final TextView passwordTextView) {
        this.passwordTextView = passwordTextView;
    }

    /**
     * Get the strength {@link ProgressBar}
     *
     * @return The strength {@link ProgressBar}
     */
    public ProgressBar getStrengthProgressBar() {
        return strengthProgressBar;
    }

    /**
     * Set the strength {@link ProgressBar}
     *
     * @param strengthProgressBar The strength {@link ProgressBar}
     */
    public void setStrengthProgressBar(final ProgressBar strengthProgressBar) {
        this.strengthProgressBar = strengthProgressBar;
    }

    /**
     * Get the copy {@link ImageButton}
     *
     * @return The copy {@link ImageButton}
     */
    public ImageButton getCopyButton() {
        return copyButton;
    }

    /**
     * Set the copy {@link ImageButton}
     *
     * @param copyButton The copy {@link ImageButton}
     */
    public void setCopyButton(final ImageButton copyButton) {
        this.copyButton = copyButton;
    }
}
