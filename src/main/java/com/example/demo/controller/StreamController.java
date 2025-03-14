package com.example.demo.controller;

import com.example.demo.utils.SshUtil;
import com.example.demo.utils.dataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
        // 1️⃣ 从 dataUtil 获取原始回放信息
        ArrayList<ArrayList<String>> rawData = dataUtil.getReplaysUrl();

        // 如果没有原始数据，可以直接返回空
        if (rawData.isEmpty()) {
            System.out.println("⚠️ No replay data found in dataUtil!");
            return new ArrayList<>();
        }

        // 2️⃣ 从数据库查询所有 (original, display)，构建映射 original -> display
        String querySql = "SELECT original, display FROM replays";
        List<Map<String, Object>> dbRows = jdbcTemplate.queryForList(querySql);

        // 用一个 Map 来存储不为空的 display
        Map<String, String> nameMap = new HashMap<>();
        for (Map<String, Object> row : dbRows) {
            String original = (String) row.get("original");
            String display = (String) row.get("display");
            // 如果 display 不为空，就存入映射表
            if (display != null && !display.trim().isEmpty()) {
                nameMap.put(original, display.trim());
            }
        }

        // 3️⃣ 合并逻辑：如果 nameMap 中有新的文件名，就替换
        ArrayList<ArrayList<String>> finalData = new ArrayList<>();
        for (ArrayList<String> row : rawData) {
            // 确保 row 至少有 4 列
            if (row.size() < 4) {
                System.out.println("⚠️ Invalid row format: " + row);
                continue;
            }

            // 解构原始数据
            String oldName  = row.get(0);
            String streamNo = row.get(1);
            String date     = row.get(2);
            String fileSize = row.get(3);

            // 如果数据库里有 display，则使用 display 替换原来的 oldName
            if (nameMap.containsKey(oldName)) {
                oldName = nameMap.get(oldName);
            }

            // 把合并结果再装回新的二维列表
            ArrayList<String> newRow = new ArrayList<>();
            newRow.add(oldName);    // 替换后的文件名
            newRow.add(streamNo);   // 原来的流编号
            newRow.add(date);       // 原来的日期
            newRow.add(fileSize);   // 原来的文件大小
            finalData.add(newRow);
        }

        // 4️⃣ 返回合并后的数据
        System.out.println("✅ Fetched & Merged Replays: " + finalData);
        return finalData;
    }

    @GetMapping("/savereplays")
    public boolean saveReplays() {
        ArrayList<ArrayList<String>> replayData = dataUtil.getReplaysUrl();
        if (replayData.isEmpty()) {
            System.out.println("⚠️ No replay data found!");
            return false;
        }

        // 构造 SQL 插入语句
        StringBuilder sqlBuilder = new StringBuilder("INSERT IGNORE INTO replays (original, streamNo, date) VALUES ");

        for (int i = 0; i < replayData.size(); i++) {
            String original = replayData.get(i).get(0); // 文件名
            String streamNo = replayData.get(i).get(1); // 流编号
            String rawDate = replayData.get(i).get(2);  // 直接使用原始日期字符串

            sqlBuilder.append("('").append(original).append("', '")
                    .append(streamNo).append("', '")
                    .append(rawDate).append("')");

            if (i < replayData.size() - 1) {
                sqlBuilder.append(", ");
            }
        }

        String sql = sqlBuilder.toString();
        try {
            jdbcTemplate.execute(sql);
            System.out.println("✅ Replays saved successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error saving replays: " + e.getMessage());
            return false;
        }
    }

    /**
     * Renames a file in the database.
     * @param oldName The current name of the file.
     * @param newName The new name to update to.
     * @return A boolean indicating whether the rename operation was successful.
     */
    @GetMapping("/rename")
    public boolean renameFile(@RequestParam String oldName, @RequestParam String newName) {
        String orgFind = "SELECT original FROM replays";
        List<String> result = jdbcTemplate.query(
                orgFind,
                (rs, rowNum) -> rs.getString("original")
        );

        System.out.println(result);
        for (int i = 0; i < result.size(); i++) {
            String value = result.get(i);
            if (value.equals(oldName)) {
                String sql = "UPDATE replays SET display = ? WHERE original = ?";
                try {
                    int rowsAffected = jdbcTemplate.update(sql, newName, oldName);
                    return rowsAffected > 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        String sql = "UPDATE replays SET display = ? WHERE display = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, newName, oldName);
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    @GetMapping("/getDisplay")
    public List<Map<String, String>> getDisplay() {
        String sql = "SELECT original, display, StreamNo, date FROM replays";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, String> map = new HashMap<>();
                map.put("old", rs.getString("original"));  // 旧名称
                map.put("new", rs.getString("display"));   // 新名称

                return map;
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Collections.emptyList(); // 发生异常时返回空列表
        }
    }



}
