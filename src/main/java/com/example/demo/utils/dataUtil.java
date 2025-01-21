package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;


public class dataUtil {

    /**
     * Retrieves replay URLs and formats their data.
     * This method calls `SshUtil.startConnect()` to get raw data from the server,
     * then processes each entry to create a formatted list of replay details.
     * @return A 2D ArrayList where each inner list represents a replay's details
     *         (e.g., filename, type, datetime, and file size in MB)
     */
    public static ArrayList<ArrayList<String>> getReplaysUrl() {
        // Get raw replay data from the server via SSH
        ArrayList<ArrayList<String>> raw = SshUtil.startConnect();

        // Initialize an ArrayList to store formatted replay data
        ArrayList<ArrayList<String>> replays = new ArrayList<>();

        // Process each raw entry
        for (int i = 0; i < raw.size(); i++) {
            String line = raw.get(i).get(0); // The filename from the raw data

            // Initialize a list to store individual replay details
            ArrayList<String> replay = new ArrayList<>();
            replay.add(line); // Add the filename as-is to the replay data

            // Add the first character of the filename (e.g., file type or identifier)
            replay.add(line.substring(0, 1));

            // Split the filename by underscores (assumes a certain naming pattern)
            String[] temp = line.split("_");

            // Get datetime information from the raw data (third element in each raw entry)
            String datetime = raw.get(i).get(2);

            // Get the file size information (second element in each raw entry)
            String line2 = raw.get(i).get(1);

            // Add datetime to replay details
            replay.add(datetime);

            // Convert file size to MB, rounding up
            double tempFileValue = Double.parseDouble(line2);
            line2 = String.valueOf(Math.ceil(tempFileValue / 1024 / 1024));
            replay.add(line2); // Add the converted file size to replay details

            // Add the formatted replay entry to the list of replays
            replays.add(replay);
        }

        // Optional: Reverse the list to show replays in a different order, if needed
        // Collections.reverse(replays);

        return replays; // Return the list of formatted replay details
    }
    public static ArrayList<String> getFileName() {
        ArrayList<ArrayList<String>> raw = SshUtil.startConnect();
        ArrayList<String> files = new ArrayList<>();
        for (int i = 0; i < raw.size(); i++) {
            String line = raw.get(i).get(0);
            files.add(line);

        }
        return files;

    }

}
