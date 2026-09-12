package com.airtribe.meditrack.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {

    // ---------------------------------------------------------------------
    // Appointment module additions (Rohan). Appended only — nothing above
    // this line was modified.
    //
    // These helpers are deliberately generic (List<String> in, List<String>
    // out) rather than appointment-specific, so the doctor and patient modules
    // can reuse them for their own CSV files instead of writing competing
    // versions. Row <-> object mapping lives in each module's own *CSVUtil.
    // ---------------------------------------------------------------------

    /**
     * Reads every line of a CSV file.
     * <p>
     * Uses try-with-resources, so the reader is closed even if reading fails.
     *
     * @param path path to the file
     * @return the lines in file order, or an empty list when the file does not exist
     * @throws RuntimeException when the file exists but cannot be read
     */
    public static List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            // A missing file simply means "nothing persisted yet" — not an error.
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            // Chained so the underlying IO failure is preserved.
            throw new RuntimeException("Failed to read CSV file: " + path, e);
        }
        return lines;
    }

    /**
     * Writes lines to a CSV file, replacing any existing content and creating parent
     * directories as needed.
     *
     * @param path  path to the file
     * @param lines the lines to write, header included
     * @throws RuntimeException when the file cannot be written
     */
    public static void writeLines(String path, List<String> lines) {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new RuntimeException("Failed to create directory for CSV file: " + path);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV file: " + path, e);
        }
    }

    /**
     * Splits one CSV row into its fields using {@code String.split(",")}.
     * <p>
     * The limit of {@code -1} keeps trailing empty fields, so a row ending in a blank
     * column still yields the full column count.
     *
     * @param line the row to split
     * @return the trimmed fields
     */
    public static String[] splitRow(String line) {
        String[] parts = line.split(com.airtribe.meditrack.constants.Constants.CSV_SEPARATOR, -1);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }

    /**
     * Joins field values into a single CSV row.
     * <p>
     * Any separator inside a value is replaced with a space, which keeps rows
     * parseable by the simple {@code split(",")} reader this project uses.
     *
     * @param values the field values
     * @return the joined row
     */
    public static String joinRow(Object... values) {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                row.append(com.airtribe.meditrack.constants.Constants.CSV_SEPARATOR);
            }
            String value = (values[i] == null) ? "" : values[i].toString();
            row.append(value.replace(com.airtribe.meditrack.constants.Constants.CSV_SEPARATOR, " "));
        }
        return row.toString();
    }
}
