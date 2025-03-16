package com.codedead.advancedpassgen.domain;

public class PasswordItem {

    private String password;
    private int strength;

    /**
     * Default constructor
     */
    public PasswordItem() {
        // Default constructor
    }

    /**
     * Initialize a new PasswordItem
     *
     * @param password The password
     * @param strength The strength of the password
     */
    public PasswordItem(final String password, final int strength) {
        this.password = password;
        this.strength = strength;
    }

    /**
     * Get the password
     *
     * @return The password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password
     *
     * @param password The password
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Get the strength of the password
     *
     * @return The strength of the password
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Set the strength of the password
     *
     * @param strength The strength of the password
     */
    public void setStrength(final int strength) {
        this.strength = strength;
    }
}
