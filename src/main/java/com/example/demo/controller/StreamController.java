package com.example.demo.controller;

import com.example.demo.utils.SshUtil;
import com.example.demo.utils.dataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StreamController {

    // Inject JdbcTemplate to interact with the database
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Retrieves live stream URLs from the database.
     * This method assumes there are 4 URLs in the database with column names `url1`, `url2`, etc.
     * @return An array of 4 live stream URLs
     */
    @GetMapping("/live")
    public String[] getLiveUrls() {
        String[] urls = new String[4]; // Array to store 4 URLs

        // Loop through each URL column (url1, url2, etc.)
        for (int i = 1; i <= 4; i++) {
            int finalI = i; // Capture the loop variable for the SQL query
            String sql = "SELECT url" + finalI + " FROM liveUrl"; // SQL query to select each URL column

            // Execute the query and store the result in the array
            urls[i - 1] = jdbcTemplate.queryForObject(sql, String.class);
            System.out.println(urls[i - 1]); // Print each URL to the console for debugging
        }

        return urls; // Return the array of URLs
    }

    /**
     * Retrieves replay URLs from an external data utility.
     * @return A 2D ArrayList of replay URLs, where each inner list represents a different replay's details
     */
    @GetMapping("/fetchreplays")
    public ArrayList<ArrayList<String>> getFetchReplayUrls() {
        return dataUtil.getReplaysUrl(); // Fetch replay URLs from `dataUtil` utility and return
    }
    @GetMapping("/savereplays")
    public boolean getSavereplayUrls() {
        ArrayList<String> fileNames = dataUtil.getFileName(); // 获取文件名列表
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO replays (original) VALUES ");

        for (int i = 0; i < fileNames.size(); i++) {
            sqlBuilder.append("('").append(fileNames.get(i)).append("')");
            if (i < fileNames.size() - 1) {
                sqlBuilder.append(", ");
            }
        }

        String sql = sqlBuilder.toString();
        System.out.println(sql); // 输出 SQL 语句，用于调试
        try {
            jdbcTemplate.execute(sql);
        }catch (Exception e){
            return false;
        }

        return true;
    }
    /**
     * Renames a file in the database.
     * @param oldName The current name of the file.
     * @param newName The new name to update to.
     * @return A boolean indicating whether the rename operation was successful.
     */
    @GetMapping("/rename")
    public boolean renameFile(@RequestParam String oldName, @RequestParam String newName) {
        String sql = "UPDATE replays SET original = ? WHERE original = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, newName, oldName);
            return rowsAffected > 0; // Return true if at least one row was updated
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return false; // Return false if an exception occurred
        }
    }

}
