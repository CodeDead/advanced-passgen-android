package com.codedead.advancedpassgen.domain;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public final class PasswordExporter {

    /**
     * The supported export formats
     */
    public enum Format {
        TXT,
        CSV
    }

    /**
     * Initialize a new PasswordExporter
     */
    private PasswordExporter() {
        // Default constructor
    }

    /**
     * Write a {@link List} of {@link PasswordItem} objects to a {@link Writer}
     *
     * @param writer The {@link Writer} to write the passwords to
     * @param items  The {@link List} of {@link PasswordItem} objects to export
     * @param format The {@link Format} to export the passwords in
     * @throws IOException When an error occurs while writing the passwords
     */
    public static void write(final Writer writer, final List<PasswordItem> items, final Format format) throws IOException {
        switch (format) {
            case TXT:
                writeTxt(writer, items);
                break;
            case CSV:
                writeCsv(writer, items);
                break;
        }
    }

    /**
     * Write the passwords as plain text, one password per line
     *
     * @param writer The {@link Writer} to write the passwords to
     * @param items  The {@link List} of {@link PasswordItem} objects to export
     * @throws IOException When an error occurs while writing the passwords
     */
    private static void writeTxt(final Writer writer, final List<PasswordItem> items) throws IOException {
        for (final PasswordItem item : items) {
            writer.write(item.password());
            writer.write('\n');
        }
    }

    /**
     * Write the passwords as CSV, using the Password header and RFC 4180 escaping
     *
     * @param writer The {@link Writer} to write the passwords to
     * @param items  The {@link List} of {@link PasswordItem} objects to export
     * @throws IOException When an error occurs while writing the passwords
     */
    private static void writeCsv(final Writer writer, final List<PasswordItem> items) throws IOException {
        writer.write("Password\r\n");

        for (final PasswordItem item : items) {
            writer.write(escapeCsv(item.password()));
            writer.write("\r\n");
        }
    }

    /**
     * Escape a value so it can be used in a CSV file
     *
     * @param value The value to escape
     * @return The escaped value
     */
    static String escapeCsv(final String value) {
        if (value.indexOf(',') >= 0 || value.indexOf('"') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }

        return value;
    }
}
