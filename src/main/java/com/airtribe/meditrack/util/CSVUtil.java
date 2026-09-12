package com.airtribe.meditrack.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {

    /**
     * Reads a CSV file and returns a list of string arrays, where each array represents a row.
     *
     * @param filePath the path to the CSV file
     * @return list of string arrays representing CSV rows
     * @throws IOException if file reading fails
     */
    public static List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                data.add(values);
            }
        }
        return data;
    }

    /**
     * Writes a list of string arrays to a CSV file.
     *
     * @param filePath the path to the CSV file
     * @param data list of string arrays representing CSV rows
     * @throws IOException if file writing fails
     */
    public static void writeCSV(String filePath, List<String[]> data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                String line = String.join(",", row);
                bw.write(line);
                bw.newLine();
            }
        }
    }

    /**
     * Converts a list of string arrays to a single CSV string.
     *
     * @param data list of string arrays representing CSV rows
     * @return CSV formatted string
     */
    public static String toCSVString(List<String[]> data) {
        StringBuilder sb = new StringBuilder();
        for (String[] row : data) {
            sb.append(String.join(",", row));
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Parses a CSV string into a list of string arrays.
     *
     * @param csvString the CSV formatted string
     * @return list of string arrays representing CSV rows
     */
    public static List<String[]> fromCSVString(String csvString) {
        List<String[]> data = new ArrayList<>();
        String[] lines = csvString.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                data.add(line.split(","));
            }
        }
        return data;
    }

    /**
     * Escapes special characters in a CSV field value.
     *
     * @param value the field value to escape
     * @return escaped value
     */
    public static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Unescapes a CSV field value.
     *
     * @param value the escaped field value
     * @return unescaped value
     */
    public static String unescapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            return value.replace("\"\"", "\"");
        }
        return value;
    }

    /**
     * Joins an array of strings into a single CSV row.
     *
     * @param values the array of values to join
     * @return CSV row string
     */
    public static String joinRow(String[] values) {
        if (values == null) {
            return "";
        }
        return String.join(",", values);
    }

    /**
     * Splits a CSV row into an array of strings.
     *
     * @param row the CSV row string
     * @return array of values
     */
    public static String[] splitRow(String row) {
        if (row == null || row.trim().isEmpty()) {
            return new String[0];
        }
        return row.split(",");
    }

    /**
     * Writes lines to a CSV file. Alias for writeCSV.
     *
     * @param filePath the path to the CSV file
     * @param lines list of string arrays representing CSV rows
     * @throws IOException if file writing fails
     */
    public static void writeLines(String filePath, List<String[]> lines) throws IOException {
        writeCSV(filePath, lines);
    }

    /**
     * Reads lines from a CSV file. Alias for readCSV.
     *
     * @param filePath the path to the CSV file
     * @return list of string arrays representing CSV rows
     * @throws IOException if file reading fails
     */
    public static List<String[]> readLines(String filePath) throws IOException {
        return readCSV(filePath);
    }
}
