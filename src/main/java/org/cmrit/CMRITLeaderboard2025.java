package org.cmrit;

import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CMRITLeaderboard2025 {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " +
            "users_data (" +
            "handle TEXT PRIMARY KEY," +
            "geeksforgeeks_handle TEXT," +
            "codeforces_handle TEXT," +
            "leetcode_handle TEXT," +
            "codechef_handle TEXT," +
            "hackerrank_handle TEXT," +
            "codeforces_url_exists INTEGER," +
            "geeksforgeeks_url_exists INTEGER," +
            "leetcode_url_exists INTEGER," +
            "codechef_url_exists INTEGER," +
            "hackerrank_url_exists INTEGER)";

    private static final String INSERT_DATA_SQL = "REPLACE INTO users_data " +
            "(handle, geeksforgeeks_handle, codeforces_handle, leetcode_handle, codechef_handle, hackerrank_handle, " +
            "geeksforgeeks_url_exists, codeforces_url_exists, leetcode_url_exists, codechef_url_exists, hackerrank_url_exists) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String CODECHEF_URL = "https://codechef-api.vercel.app/";
    private static final String CODEFORCES_URL = "https://codeforces.com/api/user.info?handles=";
    private static final String LEETCODE_URL = "https://leetcode.com/graphql?query=";
    private static final String GFG_URL = "https://coding-platform-profile-api.onrender.com/geeksforgeeks/";
    private static final String GFG_PRACTICE_URL = "https://practiceapi.geeksforgeeks.org/api/latest/events/recurring/gfg-weekly-coding-contest/leaderboard/?leaderboard_type=0&page=";
    public static final String[] SEARCH_TOKENS = {
            "cmrit25-1-basics", "cmrit25-4-rbd", "cmrit25-3-iterables", "cmrit25-2-lpb", "cmrit25-5-ds",
            "1-basics-2025", "2-loops-2025", "3-bitpat-2025", "4-iterables-2025", "5-recursion-2025",
            "ds-2025", "codevita-2025"
    };

    static Map<String, User> userMap = new HashMap<>();


    public static void main(String[] args) {

        // Load data from csv

        loadCSVtoSQL("src//main//resources//participant_details.csv");

        String methodName = args[0];

        String dbName = "cmrit";
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList <User> trueCodechef = new ArrayList<>();
        ArrayList <User> trueCodeforces = new ArrayList<>();
        ArrayList <User> trueLeetcode = new ArrayList<>();
        ArrayList <User> trueGeeksforgeeks = new ArrayList<>();
        ArrayList <User> trueHackerrank = new ArrayList<>();

        Map<String, User> gfgHandleToUserMap;
        Map<String, User> hackerrankHandleToUserMap;

        switch (methodName) {
            case "codechef":
                // Fetch all true codechef handles from the database
                try {
                    conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
                    statement = conn.createStatement();

                    String sql = "SELECT handle, codechef_handle FROM users_data WHERE codechef_url_exists = 1";
                    resultSet = statement.executeQuery(sql);

                    assert resultSet != null;

                    while (resultSet.next()) {
                        String handle = resultSet.getString("handle");
                        String codechefHandle = resultSet.getString("codechef_handle");
                        if (codechefHandle != null) {
                            trueCodechef.add(new User(handle, "codechef", codechefHandle));
                        }
                    }

                } catch (SQLException e) {
                    System.err.println("Error fetching true Codechef handles: " + e.getMessage());
                } finally {
                    try {
                        if (resultSet != null) resultSet.close();
                        if (statement != null) statement.close();
                        if (conn != null) conn.close();
                    } catch (SQLException e) {
                        System.err.println("Error closing resultSet, statement, or connection: " + e.getMessage());
                    }
                }
                scrapeCodechef(trueCodechef);
                break;
            case "codeforces":
                // Fetch all true codeforces handles from the database
                try {
                    conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
                    statement = conn.createStatement();

                    String sql = "SELECT handle, codeforces_handle FROM users_data WHERE codeforces_url_exists = 1";
                    resultSet = statement.executeQuery(sql);

                    assert resultSet != null;

                    while (resultSet.next()) {
                        String handle = resultSet.getString("handle");
                        String codeforcesHandle = resultSet.getString("codeforces_handle");
                        if (codeforcesHandle != null) {
                            trueCodeforces.add(new User(handle, "codeforces", codeforcesHandle));
                        }
                    }

                } catch (SQLException e) {
                    System.err.println("Error fetching true Codeforces handles: " + e.getMessage());
                } finally {
                    try {
                        if (resultSet != null) resultSet.close();
                        if (statement != null) statement.close();
                        if (conn != null) conn.close();
                    } catch (SQLException e) {
                        System.err.println("Error closing resultSet, statement, or connection: " + e.getMessage());
                    }
                }
                scrapeCodeforces(trueCodeforces);
                break;
            case "leetcode":
                // Fetch all true leetcode handles from the database
                try{
                    conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
                    statement = conn.createStatement();

                    String sql = "SELECT handle, leetcode_handle FROM users_data WHERE leetcode_url_exists = 1";
                    resultSet = statement.executeQuery(sql);

                    assert resultSet != null;

                    while (resultSet.next()) {
                        String handle = resultSet.getString("handle");
                        String leetcodeHandle = resultSet.getString("leetcode_handle");
                        if (leetcodeHandle != null) {
                            trueLeetcode.add(new User(handle, "leetcode", leetcodeHandle));
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error fetching true Leetcode handles: " + e.getMessage());
                } finally {
                    try {
                        if (resultSet != null) resultSet.close();
                        if (statement != null) statement.close();
                        if (conn != null) conn.close();
                    } catch (SQLException e) {
                        System.err.println("Error closing resultSet, statement, or connection: " + e.getMessage());
                    }
                }

                scrapeLeetcode(trueLeetcode);
                break;
            case "gfg":
                // Fetch all true gfg handles from the database
                try{
                    conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
                    statement = conn.createStatement();

                    String sql = "SELECT handle, geeksforgeeks_handle FROM users_data WHERE geeksforgeeks_url_exists = 1";
                    resultSet = statement.executeQuery(sql);

                    assert resultSet != null;

                    while (resultSet.next()) {
                        String handle = resultSet.getString("handle");
                        String geeksforgeeksHandle = resultSet.getString("geeksforgeeks_handle");
                        if (geeksforgeeksHandle != null) {
                            trueGeeksforgeeks.add(new User(handle, "geeksforgeeks", geeksforgeeksHandle));
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error fetching true GeeksforGeeks handles: " + e.getMessage());
                } finally {
                    try {
                        if (resultSet != null) resultSet.close();
                        if (statement != null) statement.close();
                        if (conn != null) conn.close();
                    } catch (SQLException e) {
                        System.err.println("Error closing resultSet, statement, or connection: " + e.getMessage());
                    }
                }

                // create a gfgHandle to user map
                gfgHandleToUserMap = new HashMap<>();
                for (User user : trueGeeksforgeeks) {
                    gfgHandleToUserMap.put(user.getGeeksforgeeksHandle().toLowerCase(), user);
                }

                scrapeGfg(trueGeeksforgeeks, gfgHandleToUserMap);
                break;
            case "hackerrank":
                // Fetch all true hackerrank handles from the database
                try{
                    conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
                    statement = conn.createStatement();

                    String sql = "SELECT handle, hackerrank_handle FROM users_data WHERE hackerrank_url_exists = 1";
                    resultSet = statement.executeQuery(sql);

                    assert resultSet != null;

                    while (resultSet.next()) {
                        String handle = resultSet.getString("handle");
                        String hackerrankHandle = resultSet.getString("hackerrank_handle");
                        if (hackerrankHandle != null) {
                            trueHackerrank.add(new User(handle, "hackerrank", hackerrankHandle));
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error fetching true Hackerrank handles: " + e.getMessage());
                } finally {
                    try {
                        if (resultSet != null) resultSet.close();
                        if (statement != null) statement.close();
                        if (conn != null) conn.close();
                    } catch (SQLException e) {
                        System.err.println("Error closing resultSet, statement, or connection: " + e.getMessage());
                    }
                }

                // create a hackerrankHandle to user map
                hackerrankHandleToUserMap = new HashMap<>();
                for (User user : trueHackerrank) {
                    hackerrankHandleToUserMap.put(user.getHackerrankHandle().toLowerCase(), user);
                }

                scrapeHackerrank(trueHackerrank, hackerrankHandleToUserMap);
                break;
            case "all":
                // Fetch all true handles from the database
                try {
                    conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
                    statement = conn.createStatement();

                    String sql = "SELECT handle, " +
                            "CASE WHEN codeforces_url_exists = 1 THEN codeforces_handle ELSE NULL END AS codeforces_handle, " +
                            "CASE WHEN leetcode_url_exists = 1 THEN leetcode_handle ELSE NULL END AS leetcode_handle, " +
                            "CASE WHEN geeksforgeeks_url_exists = 1 THEN geeksforgeeks_handle ELSE NULL END AS geeksforgeeks_handle, " +
                            "CASE WHEN codechef_url_exists = 1 THEN codechef_handle ELSE NULL END AS codechef_handle, " +
                            "CASE WHEN hackerrank_url_exists = 1 THEN hackerrank_handle ELSE NULL END AS hackerrank_handle " +
                            "FROM users_data";
                    resultSet = statement.executeQuery(sql);

                    assert resultSet != null;

                    while (resultSet.next()) {
                        String handle = resultSet.getString("handle");
                        String codeforcesHandle = resultSet.getString("codeforces_handle");
                        String leetcodeHandle = resultSet.getString("leetcode_handle");
                        String geeksforgeeksHandle = resultSet.getString("geeksforgeeks_handle");
                        String codechefHandle = resultSet.getString("codechef_handle");
                        String hackerrankHandle = resultSet.getString("hackerrank_handle");

                        if (codeforcesHandle != null) {
                            trueCodeforces.add(new User(handle, "codeforces", codeforcesHandle));
                        }
                        if (leetcodeHandle != null) {
                            trueLeetcode.add(new User(handle, "leetcode", leetcodeHandle));
                        }
                        if (geeksforgeeksHandle != null) {
                            trueGeeksforgeeks.add(new User(handle, "geeksforgeeks", geeksforgeeksHandle));
                        }
                        if ((codechefHandle != null)) {
                            trueCodechef.add(new User(handle, "codechef", codechefHandle));
                        }
                        if (hackerrankHandle != null) {
                            trueHackerrank.add(new User(handle, "hackerrank", hackerrankHandle));
                        }
                    }

                } catch (SQLException e) {
                    System.err.println("Error fetching true handles: " + e.getMessage());
                } finally {
                    try {
                        if (resultSet != null) resultSet.close();
                        if (statement != null) statement.close();
                        if (conn != null) conn.close();
                    } catch (SQLException e) {
                        System.err.println("Error closing resultSet, statement, or connection: " + e.getMessage());
                    }
                }

                // create a gfgHandle to user map
                gfgHandleToUserMap = new HashMap<>();
                for (User user : trueGeeksforgeeks) {
                    gfgHandleToUserMap.put(user.getGeeksforgeeksHandle().toLowerCase(), user);
                }

                // create a hackerrankHandle to user map
                hackerrankHandleToUserMap = new HashMap<>();
                for (User user : trueHackerrank) {
                    hackerrankHandleToUserMap.put(user.getHackerrankHandle().toLowerCase(), user);
                }

                scrapeCodechef(trueCodechef);
                scrapeCodeforces(trueCodeforces);
                scrapeLeetcode(trueLeetcode);
                scrapeGfg(trueGeeksforgeeks, gfgHandleToUserMap);
                scrapeHackerrank(trueHackerrank, hackerrankHandleToUserMap);
                break;
            case "build_leaderboard":
                // Add all usernames to the userMap which can be fetched from the database
                try{
                    conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
                    statement = conn.createStatement();

                    String sql = "SELECT * FROM users_data";
                    resultSet = statement.executeQuery(sql);

                    assert resultSet != null;

                    while (resultSet.next()) {
                        String handle = resultSet.getString("handle");
                        String codeforcesHandle = resultSet.getString("codeforces_handle");
                        String geeksforgeeksHandle = resultSet.getString("geeksforgeeks_handle");
                        String leetcodeHandle = resultSet.getString("leetcode_handle");
                        String codechefHandle = resultSet.getString("codechef_handle");
                        String hackerrankHandle = resultSet.getString("hackerrank_handle");
                        User user = new User(handle, codeforcesHandle, geeksforgeeksHandle, leetcodeHandle, codechefHandle, hackerrankHandle);
                        // Add the user to the userMap
                        userMap.put(handle, user);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                buildLeaderboard();
                break;
            default:
                System.err.println("Invalid method specified.");
                System.exit(1);
        }
    }

    private static void buildLeaderboard(){
        // Use all generated rating files to build the leaderboard
        // Read all the rating files and store the ratings in a map

        try {
            BufferedReader reader = new BufferedReader(new FileReader("codeforces_ratings.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String handle = data[0];
                String codeforcesHandle = data[1];
                int rating = Integer.parseInt(data[2]);
                // if the user is not in the map, add the user
                if (!userMap.containsKey(handle)) {
                    userMap.put(handle, new User(handle, "codeforces", codeforcesHandle));
                }
                User user = userMap.get(handle);
                user.setCodeforcesRating(rating);
            }
        } catch (IOException e) {
            System.err.println("Error reading Codeforces ratings file: " + e.getMessage());
        }
        System.out.println("Codeforces ratings read successfully.");
        try {
            BufferedReader reader = new BufferedReader(new FileReader("gfg_ratings.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String handle = data[0];
                String gfgHandle = data[1];
                int rating = Integer.parseInt(data[2]);
                // if the user is not in the map, add the user
                if (!userMap.containsKey(handle)) {
                    userMap.put(handle, new User(handle, "geeksforgeeks", gfgHandle));
                }
                User user = userMap.get(handle);
                user.setGeeksforgeeksRating(rating);
            }
        } catch (IOException e) {
            System.err.println("Error reading GFG ratings file: " + e.getMessage());
        }
        System.out.println("GFG ratings read successfully.");
        try {
            BufferedReader reader = new BufferedReader(new FileReader("gfg_practice_ratings.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String handle = data[0];
                String gfgHandle = data[1];
                int rating = Integer.parseInt(data[2]);
                // if the user is not in the map, add the
                if (!userMap.containsKey(handle)) {
                    userMap.put(handle, new User(handle, "geeksforgeeks", gfgHandle));
                }
                User user = userMap.get(handle);
                user.setGeesforgeeksPracticeRating(rating);
            }
        } catch (IOException e) {
            System.err.println("Error reading GFG Practice ratings file: " + e.getMessage());
        }
        System.out.println("GFG Practice ratings read successfully.");
        try {
            BufferedReader reader = new BufferedReader(new FileReader("leetcode_ratings.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String handle = data[0];
                String leetcodeHandle = data[1];
                int rating = Integer.parseInt(data[2]);
                // if the user is not in the map, add the user
                if (!userMap.containsKey(handle)) {
                    userMap.put(handle, new User(handle, "leetcode", leetcodeHandle));
                }
                User user = userMap.get(handle);
                user.setLeetcodeRating(rating);
            }
        } catch (IOException e) {
            System.err.println("Error reading Leetcode ratings file: " + e.getMessage());
        }
        System.out.println("Leetcode ratings read successfully.");
        try {
            BufferedReader reader = new BufferedReader(new FileReader("codechef_ratings.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String handle = data[0];
                String codechefHandle = data[1];
                int rating = Integer.parseInt(data[2]);
                // if the user is not in the map, add the user
                if (!userMap.containsKey(handle)) {
                    userMap.put(handle, new User(handle, "codechef", codechefHandle));
                }
                User user = userMap.get(handle);
                user.setCodechefRating(rating);
            }
        } catch (IOException e) {
            System.err.println("Error reading Codechef ratings file: " + e.getMessage());
        }
        System.out.println("Codechef ratings read successfully.");
        try {
            BufferedReader reader = new BufferedReader(new FileReader("hackerrank_ratings.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String handle = data[0];
                String hackerrankHandle = data[1];
                int rating = Integer.parseInt(data[2]);
                // if the user is not in the map, add the user
                if (!userMap.containsKey(handle)) {
                    userMap.put(handle, new User(handle, "hackerrank", hackerrankHandle));
                }
                User user = userMap.get(handle);
                user.setHackerrankRating(rating);
            }
        } catch (IOException e) {
            System.err.println("Error reading Hackerrank ratings file: " + e.getMessage());
        }
        System.out.println("Hackerrank ratings read successfully.");

        // Upload userMap as a leaderboard to the database
        uploadLeaderboardToDatabase(userMap);

        // use sql to find max of each rating
        // use sql to find percentile of each user

        String dbName = "leaderboard";
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        int maxCodeforcesRating = 0;
        int maxGeeksforgeeksRating = 0;
        int maxGeeksforgeeksPracticeRating = 0;
        int maxLeetcodeRating = 0;
        int maxCodechefRating = 0;
        int maxHackerrankRating = 0;

        String sql = "SELECT MAX(codeforces_rating) AS max_codeforces_rating, MAX(geeksforgeeks_rating) AS max_geeksforgeeks_rating, MAX(geeksforgeeks_practice_rating) AS max_geeksforgeeks_practice_rating, MAX(leetcode_rating) AS max_leetcode_rating, MAX(codechef_rating) AS max_codechef_rating, MAX(hackerrank_rating) AS max_hackerrank_rating FROM leaderboard";
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
            statement = conn.createStatement();
            resultSet = statement.executeQuery(sql);
            assert resultSet != null;
            maxCodeforcesRating = resultSet.getInt("max_codeforces_rating");
            maxGeeksforgeeksRating = resultSet.getInt("max_geeksforgeeks_rating");
            maxGeeksforgeeksPracticeRating = resultSet.getInt("max_geeksforgeeks_practice_rating");
            maxLeetcodeRating = resultSet.getInt("max_leetcode_rating");
            maxCodechefRating = resultSet.getInt("max_codechef_rating");
            maxHackerrankRating = resultSet.getInt("max_hackerrank_rating");
        } catch (SQLException e) {
            System.err.println("Error fetching max ratings: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resultSet, statement, or connection: " + e.getMessage());
            }
        }

        // update the userMap with percentile
        for (User user : userMap.values()) {
            double cf = (double) user.getCodeforcesRating() / maxCodeforcesRating * 100;
            double gfgs = (double) user.getGeeksforgeeksRating() / maxGeeksforgeeksRating * 100;
            double gfgp = (double) user.getGeesforgeeksPracticeRating() / maxGeeksforgeeksPracticeRating * 100;
            double lc = (double) user.getLeetcodeRating() / maxLeetcodeRating * 100;
            double cc = (double) user.getCodechefRating() / maxCodechefRating * 100;
            double hr = (double) user.getHackerrankRating() / maxHackerrankRating * 100;
            double percentile = ( cf * 0.3 + gfgs*0.3  + gfgp*0.1 + lc*0.1 + cc*0.1 + hr*0.1 );

            user.setPercentile(percentile);
        }

        // Push the updated userMap to the database
        uploadLeaderboardToDatabase(userMap);

        // Fetch the leaderboard from the database with decreasing percentile
        sql = "SELECT * FROM leaderboard ORDER BY percentile DESC";
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
            statement = conn.createStatement();
            resultSet = statement.executeQuery(sql);
            assert resultSet != null;

            // Create a new workbook
            XSSFSheet sheet;
            XSSFWorkbook workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Leaderboard");
            // Create a header row with the column names
            Row headerRow = sheet.createRow(0);

            // Create bold font with size 18 for column headers
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldFont.setFontHeightInPoints((short) 20);

            Font boldFont2 = workbook.createFont();
            boldFont2.setBold(true);
            boldFont2.setFontHeightInPoints((short) 14);

            // Create bold centered cell style with 14 font size for normal cells
            CellStyle boldCenteredCellStyle = workbook.createCellStyle();
            boldCenteredCellStyle.setAlignment(HorizontalAlignment.CENTER);
            boldCenteredCellStyle.setFont(boldFont);
            boldCenteredCellStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE1.getIndex());
            boldCenteredCellStyle.setBorderBottom(BorderStyle.THICK);
            boldCenteredCellStyle.setBorderTop(BorderStyle.THICK);
            boldCenteredCellStyle.setBorderLeft(BorderStyle.THICK);
            boldCenteredCellStyle.setBorderRight(BorderStyle.THICK);
            boldCenteredCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Create bold cell style with 14 font size for normal cells
            CellStyle boldCellStyle = workbook.createCellStyle();
            boldCellStyle.setAlignment(HorizontalAlignment.CENTER);
            boldCellStyle.setFont(boldFont2);
            boldCellStyle.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
            boldCellStyle.setBorderBottom(BorderStyle.THICK);
            boldCellStyle.setBorderTop(BorderStyle.THICK);
            boldCellStyle.setBorderLeft(BorderStyle.THICK);
            boldCellStyle.setBorderRight(BorderStyle.THICK);
            boldCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] columns = {"Rank", "Handle", "Codeforces_Handle", "Codeforces_Rating", "Geeksforgeeks_Handle", "Geeksforgeeks_Rating", "Geeksforgeeks_Practice_Rating", "Leetcode_Handle", "Leetcode_Rating", "Codechef_Handle", "Codechef_Rating", "Hackerrank_Handle", "Hackerrank_Rating", "Percentile"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellStyle(boldCenteredCellStyle);
                cell.setCellValue(columns[i]);
            }

            int rank = 1;
            while (resultSet.next()) {
                String handle = resultSet.getString("handle");
                int codeforcesRating = resultSet.getInt("codeforces_rating");
                int codechefRating = resultSet.getInt("codechef_rating");
                int leetcodeRating = resultSet.getInt("leetcode_rating");
                int geeksforgeeksRating = resultSet.getInt("geeksforgeeks_rating");
                int geeksforgeeksPracticeRating = resultSet.getInt("geeksforgeeks_practice_rating");
                int hackerrankRating = resultSet.getInt("hackerrank_rating");
                double percentile = resultSet.getDouble("percentile");
                String codeforcesHandle = userMap.get(handle).getCodeforcesHandle();
                String geeksforgeeksHandle = userMap.get(handle).getGeeksforgeeksHandle();
                String leetcodeHandle = userMap.get(handle).getLeetcodeHandle();
                String codechefHandle = userMap.get(handle).getCodechefHandle();
                String hackerrankHandle = userMap.get(handle).getHackerrankHandle();
                System.out.println(rank + ". " + handle + " - " + codeforcesRating + " - " + geeksforgeeksRating + " - " + geeksforgeeksPracticeRating + " - " + leetcodeRating + " - " + codechefRating + " - " + hackerrankRating + " - " + percentile);

                // Append the data to the sheet
                Row row = sheet.createRow(rank);
                // add all the data to the row at once
                Object[] data = {rank, handle, codeforcesHandle, codeforcesRating, geeksforgeeksHandle, geeksforgeeksRating, geeksforgeeksPracticeRating, leetcodeHandle, leetcodeRating, codechefHandle, codechefRating, hackerrankHandle, hackerrankRating, percentile};
                for (int i = 0; i < data.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellStyle(boldCellStyle);
                    if (data[i] instanceof String) {
                        cell.setCellValue((String) data[i]);
                    } else if (data[i] instanceof Integer) {
                        cell.setCellValue((Integer) data[i]);
                    } else if (data[i] instanceof Double) {
                        cell.setCellValue((Double) data[i]);
                    }
                }
                rank++;
            }

            // Set all cells to auto-size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }


            // Check if the directory exists, if not create it
            File directory = new File("Leaderboards");
            if (!directory.exists()) {
                boolean dirCreated = directory.mkdir();
                if (!dirCreated) {
                    System.err.println("Error creating directory: Leaderboards");
                }
            }

            // Write the workbook to a file
            try (FileOutputStream fileOut = new FileOutputStream("Leaderboards//CurrentCMRITLeaderboard2025.xlsx")) {
                workbook.write(fileOut);
            } catch (IOException e) {
                System.err.println("Error writing leaderboard to file: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leaderboard: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resultSet, statement, or connection: " + e.getMessage());
            }
        }
    }

    private static void uploadLeaderboardToDatabase(Map<String, User> userMap) {
        String dbName = "leaderboard";
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);

            // if the table exists, drop it
            String sql = "DROP TABLE IF EXISTS leaderboard";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();

            sql = "CREATE TABLE IF NOT EXISTS leaderboard (" +
                    "handle TEXT PRIMARY KEY," +
                    "codeforces_rating INTEGER," +
                    "codechef_rating INTEGER," +
                    "leetcode_rating INTEGER," +
                    "geeksforgeeks_rating INTEGER," +
                    "geeksforgeeks_practice_rating INTEGER," +
                    "hackerrank_rating INTEGER, " +
                    "percentile REAL)";

            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();

            sql = "REPLACE INTO leaderboard " +
                    "(handle, codeforces_rating, codechef_rating, leetcode_rating, geeksforgeeks_rating, geeksforgeeks_practice_rating, hackerrank_rating, percentile) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            preparedStatement = conn.prepareStatement(sql);

            for (User user : userMap.values()) {
                preparedStatement.setString(1, user.getHandle());
                preparedStatement.setInt(2, user.getCodeforcesRating());
                preparedStatement.setInt(3, user.getCodechefRating());
                preparedStatement.setInt(4, user.getLeetcodeRating());
                preparedStatement.setInt(5, user.getGeeksforgeeksRating());
                preparedStatement.setInt(6, user.getGeesforgeeksPracticeRating());
                preparedStatement.setInt(7, user.getHackerrankRating());
                if (user.getPercentile() == null) {
                    preparedStatement.setNull(8, Types.REAL);
                } else {
                    preparedStatement.setDouble(8, user.getPercentile());
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error uploading leaderboard to database: " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing preparedStatement or connection: " + e.getMessage());
            }
        }
    }

    private static void scrapeCodechef(ArrayList <User> resultSet) {
        // Scraper logic for Codechef

        System.out.println("Codechef scraping in progress...");

        String url;
        URI websiteUrl;
        URLConnection connection;
        HttpURLConnection o;
        InputStream inputStream;

        // create or clear the file for writing
        File file = new File("codechef_ratings.txt");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(""); // Clearing the file
            writer.close();
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
        }

        int size = resultSet.size();
        int i = 1;

        for (User user : resultSet) {
            String handle = user.getHandle();
            String codechefHandle = user.getCodechefHandle();

            // remove any spaces from the handle
            codechefHandle = codechefHandle.replace(" ", "");

            System.out.println("(" + i + "/" + size + ") Scraping Codechef for " + handle + " (Codechef Handle: " + codechefHandle + ")");
            i++;

            try {
                url = CODECHEF_URL + codechefHandle;
                websiteUrl = new URI(url);
                connection = websiteUrl.toURL().openConnection();
                o = (HttpURLConnection) connection;
                o.setRequestMethod("GET");
                if (o.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || o.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    throw new RuntimeException();
                }
                inputStream = o.getInputStream();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    StringBuilder jsonContent = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonContent.append(line);
                    }
                    JSONObject jsonObject = new JSONObject(jsonContent.toString());
                    int codechefRating;
                    try {
                        codechefRating = jsonObject.getInt("currentRating");

                        // update the user object with the codechef rating
                        user.setCodechefRating(codechefRating);

                        System.out.println("Codechef rating for " + codechefHandle + " is: " + codechefRating);
                        // Write to a text file
                        FileWriter writer = new FileWriter("codechef_ratings.txt", true);
                        writer.write(handle + "," + codechefHandle + "," + codechefRating + "\n");
                        writer.close();
                    } catch (JSONException e) {
                        System.err.println("Error fetching codechef rating for " + codechefHandle + ": " + e.getMessage());
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }

        }

        System.out.println("Codechef scraping completed.");
        System.out.println("========================================");

    }

    private static final int MAX_HANDLES_PER_REQUEST = 380;

    private static void scrapeCodeforces(ArrayList<User> resultSet) {
        // Scraper logic for Codeforces
        System.out.println("Codeforces scraping in progress...");

        // Create or clear the file for writing
        File file = new File("codeforces_ratings.txt");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(""); // Clearing the file
            writer.close();
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
        }

        List<List<User>> userChunks = splitUsersIntoChunks(resultSet);

        int counter = 1;
        int totalUsers = resultSet.size();

        for (List<User> users : userChunks) {
            try {
                // Create a list of all the Codeforces handles separated by ";"
                String codeforcesHandles = users.stream()
                        .map(User::getCodeforcesHandle)
                        .map(handle -> handle.replaceAll(" ", ""))
                        .collect(Collectors.joining(";"));

                // Construct the URL with handles
                String url = CODEFORCES_URL + codeforcesHandles;
                System.out.println("Codeforces URL: " + url);

                // Make HTTP request
                URI websiteUrl = new URI(url);
                URLConnection connection = websiteUrl.toURL().openConnection();
                HttpURLConnection o = (HttpURLConnection) connection;

                o.setRequestMethod("GET");
                if (o.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || o.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    throw new RuntimeException();
                }

                // Read response
                try (InputStream inputStream = connection.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    StringBuilder jsonContent = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonContent.append(line);
                    }

                    // Parse JSON response
                    JSONObject jsonObject = new JSONObject(jsonContent.toString());
                    JSONArray array = jsonObject.getJSONArray("result");

                    // Process JSON data
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String handle = obj.getString("handle");
                        int rating = obj.optInt("rating", 0);
                        System.out.println("(" + counter + "/" + totalUsers + ") " + "Codeforces rating for " + handle + " is: " + rating);

                        // find user handle with codeforces handle
                        User user = users.stream()
                                .filter(u -> u.getCodeforcesHandle().replace(" ", "").equalsIgnoreCase(handle.replace(" ", "")))
                                .findFirst()
                                .orElse(null);
                        if (user != null) {
                            // update the user object with the codeforces rating
                            user.setCodeforcesRating(rating);
                            // Write to a text file
                            FileWriter writer = new FileWriter("codeforces_ratings.txt", true);
                            writer.write(user.getHandle() + "," + handle + "," + rating + "\n");
                            writer.close();
                        }

                        counter++;
                    }
                } catch (JSONException e) {
                    System.err.println("Error fetching codeforces rating: " + e.getMessage());
                }
            } catch (IOException | URISyntaxException e) {
                System.err.println("Error fetching codeforces rating: " + e.getMessage());
            }
        }
        System.out.println("Codeforces scraping completed.");
        System.out.println("========================================");
    }

    private static List<List<User>> splitUsersIntoChunks(ArrayList<User> resultSet) {
        List<List<User>> chunks = new ArrayList<>();
        int size = resultSet.size();
        for (int i = 0; i < size; i += MAX_HANDLES_PER_REQUEST) {
            int end = Math.min(size, i + MAX_HANDLES_PER_REQUEST);
            chunks.add(new ArrayList<>(resultSet.subList(i, end)));
        }
        return chunks;
    }


    private static void scrapeLeetcode(ArrayList<User> resultSet) {
        // Scraper logic for Leetcode
        System.out.println("Leetcode scraping in progress...");

        // Create or clear the file for writing
        File file = new File("leetcode_ratings.txt");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(""); // Clearing the file
            writer.close();
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
        }

        int counter = 1;
        int size = resultSet.size();

        for (User user : resultSet) {
            String handle = user.getHandle();
            String leetcodeHandle = user.getLeetcodeHandle();
            String encodedLeetcodeHandle = URLEncoder.encode(leetcodeHandle, StandardCharsets.UTF_8);

            String url = LEETCODE_URL + URLEncoder.encode("query{userContestRanking(username:\"" + encodedLeetcodeHandle + "\"){rating}}", StandardCharsets.UTF_8);

            try {
                URI websiteUrl = new URI(url);
                URLConnection connection = websiteUrl.toURL().openConnection();
                HttpURLConnection o = (HttpURLConnection) connection;

                o.setRequestMethod("GET");
                if (o.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || o.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    throw new RuntimeException();
                }

                // Read response
                try (InputStream inputStream = connection.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    StringBuilder jsonContent = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonContent.append(line);
                    }

                    // Parse JSON response
                    JSONObject jsonObject = new JSONObject(jsonContent.toString());
                    JSONObject data = jsonObject.optJSONObject("data");
                    JSONObject userContestRanking = data.optJSONObject("userContestRanking");

                    int rating = 0; // Default rating is 0

                    if (userContestRanking != null) {
                        double rawRating = userContestRanking.optDouble("rating", Double.NaN);
                        if (!Double.isNaN(rawRating)) {
                            // Convert rating to int if it's not NaN
                            rating = (int) rawRating;
                        }
                    }

                    // update the user object with the leetcode rating
                    user.setLeetcodeRating(rating);

                    System.out.println("(" + counter + "/" + size + ") " + "Leetcode rating for " + handle + " with leetcode handle " + leetcodeHandle + " is: " + rating);

                    // Write to a text file
                    FileWriter writer = new FileWriter("leetcode_ratings.txt", true);
                    writer.write(user.getHandle() + "," + leetcodeHandle + "," + rating + "\n");
                    writer.close();

                    counter++;
                } catch (JSONException e) {
                    System.err.println("Error fetching leetcode rating for " + handle + " with leetcode handle " + leetcodeHandle + ": " + e.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Leetcode scraping completed.");
        System.out.println("========================================");
    }

    private static void scrapeGfg(ArrayList<User> trueGfg, Map<String, User> gfgHandleToUserMap) {
        // Scraper logic for GeeksforGeeks

        System.out.println("GeeksforGeeks scraping in progress...");

        // Essential variables
        String url;
        URI websiteUrl;
        URLConnection connection;
        HttpURLConnection o;
        InputStream inputStream;

        // Create or clear the file for writing
        File file = new File("gfg_ratings.txt");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(""); // Clearing the file
            writer.close();
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
        }

        int counter = 1;

        // Overall weekly leaderboard scraping

        for(int j=1;j<=10000;j++) {
            try {
                url = GFG_PRACTICE_URL + j;

                System.out.println("Page: " + j);

                // Fetch JSON data from a URL (or you can read from a file)
                Document doc = Jsoup.connect(url).ignoreContentType(true).get();
                String json = doc.body().text();

                // Parse JSON using Gson
                Gson gson = new Gson();
                DataModel gfguserData = gson.fromJson(json, DataModel.class);

                // Access parsed data

                boolean foundZero = false;

                for (Result gfgUser : gfguserData.results) {
                    String gfgHandle = gfgUser.user_handle;
                    User user = gfgHandleToUserMap.get(gfgHandle.toLowerCase());
                    if (gfgUser.user_score == 0) {
                        foundZero = true;
                        break;
                    }
                    if (user != null) {
                        user.setGeeksforgeeksRating((int)gfgUser.user_score);
                        System.out.println("(" + counter + "/" + trueGfg.size() + ") " + "GFG weekly contest rating for " + user.getHandle() + " with GFG handle " + gfgHandle + " is: " + (int)gfgUser.user_score);
                        // Write to a text file
                        FileWriter writer = new FileWriter("gfg_ratings.txt", true);
                        writer.write(user.getHandle() + "," + gfgHandle + "," + (int)gfgUser.user_score + "\n");
                        writer.close();
                        counter++;
                    }
                }

                if (foundZero) {
                    break;
                }
            } catch (IOException e) {
                System.err.println("Error fetching GFG Practice rating: " + e.getMessage());
            }
        }

        // set all unset ratings to 0
        for (User user : trueGfg) {
            if (user.getGeeksforgeeksRating() == null) {
                user.setGeeksforgeeksRating(0);
                System.out.println("(" + counter + "/" + trueGfg.size() + ") " + "GFG overall rating for " + user.getHandle() + " with GFG handle " + user.getGeeksforgeeksHandle() + " is: " + 0);

                try{
                    FileWriter writer = new FileWriter("gfg_ratings.txt", true);
                    writer.write(user.getHandle() + "," + user.getGeeksforgeeksHandle() + "," + 0 + "\n");
                    writer.close();
                    counter++;
                } catch (IOException e) {
                    System.err.println("Error fetching GFG Practice rating: " + e.getMessage());
                }
            }
        }

        System.out.println("GFG overall scraping completed.");
        System.out.println("========================================");

        // Practice contest scraping

        counter = 1;

        // create or clear the file for writing
        file = new File("gfg_practice_ratings.txt");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(""); // Clearing the file
            writer.close();
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
        }

        for (User user: trueGfg) {
            String gfgHandle = user.getGeeksforgeeksHandle();
            url = GFG_URL + gfgHandle;

            try {
                websiteUrl = new URI(url);
                connection = websiteUrl.toURL().openConnection();
                o = (HttpURLConnection) connection;
                o.setRequestMethod("GET");
                if (o.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || o.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    // set the practice rating to 0
                    user.setGeesforgeeksPracticeRating(0);
                    System.out.println("(" + counter + "/" + trueGfg.size() + ") " + "GFG practice rating for " + user.getHandle() + " with GFG handle " + gfgHandle + " is: " + 0);
                    // Write to a text file
                    FileWriter writer = new FileWriter("gfg_practice_ratings.txt", true);
                    writer.write(user.getHandle() + "," + gfgHandle + "," + 0 + "\n");
                    writer.close();
                    counter++;
                    continue;
                }
                inputStream = o.getInputStream();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    StringBuilder jsonContent = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonContent.append(line);
                    }
                    JSONObject jsonObject = new JSONObject(jsonContent.toString());
                    int gfgPracticeRating = 0;

                    try {
                        gfgPracticeRating = jsonObject.getInt("overall_coding_score");

                    } catch (JSONException e) {
                        System.err.println("Error fetching GFG Practice rating for " + gfgHandle + ": " + e.getMessage());
                    }

                    // update the user object with the gfg practice rating
                    user.setGeesforgeeksPracticeRating(gfgPracticeRating);

                    System.out.println("(" + counter + "/" + trueGfg.size() + ") " + "GFG practice rating for " + user.getHandle() + " with GFG handle " + gfgHandle + " is: " + gfgPracticeRating);
                    // Write to a text file
                    FileWriter writer = new FileWriter("gfg_practice_ratings.txt", true);
                    writer.write(user.getHandle() + "," + gfgHandle + "," + gfgPracticeRating + "\n");
                    writer.close();
                    counter++;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("GeeksforGeeks scraping completed.");
        System.out.println("========================================");
    }

    static class DataModel {
        int count;
        Result[] results;
        boolean consider_for_geek_bits;
    }

    static class Result {
        int user_id;
        String user_handle;
        double user_score;
        int user_rank;
    }

    private static void scrapeHackerrank(ArrayList<User> trueHackerrank, Map<String, User> hackerrankHandleToUserMap) {
        // Scraper logic for Hackerrank
        System.out.println("Hackerrank scraping in progress...");

        // create or clear the file for writing
        File file = new File("hackerrank_ratings.txt");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(""); // Clearing the file
            writer.close();
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
        }

        try {
            for (String trackerName : SEARCH_TOKENS) {
                System.out.println(trackerName);
                for (int j = 0; j < 10000; j += 100) {
                    try {
                        String url = "https://www.hackerrank.com/rest/contests/" + trackerName + "/leaderboard?offset=" + j + "&limit=100";
                        Document doc = Jsoup.connect(url).ignoreContentType(true).get();
                        Element body = doc.body();
                        if (body.text().contains("INVALID URL")) {
                            throw new ArithmeticException("INVALID URL : " + trackerName);
                        }
                        String jsonContent = body.text();
                        Leaderboard leaderboard = new Gson().fromJson(jsonContent, Leaderboard.class);
                        List<LeaderboardModel> models = leaderboard.models;
                        if (models.isEmpty()) break;
                        for (LeaderboardModel model : models) {
                            String userHandle = model.hacker.toLowerCase();

                            // find user handle with hackerrank handle
                            User user = hackerrankHandleToUserMap.get(userHandle);
                            if (user != null) {
                                if (user.getHackerrankRating() == null) {
                                    user.setHackerrankRating((int) model.score);
                                }
                                else{
                                    // add the ratings
                                    user.setHackerrankRating(user.getHackerrankRating() + (int) model.score);
                                }
                                System.out.println("Hackerrank rating for " + userHandle + " is: " + (int) model.score);
                            }
                            else {
                                System.out.println("User not found: " + userHandle);
                            }
                        }
                    } catch (IOException | ArithmeticException e) {
                        System.err.println("Error fetching Hackerrank rating for " + trackerName + ": " + e.getMessage());
                    }
                }
            }
            for (User user : trueHackerrank) {
                if (user.getHackerrankRating() != null) {
                    // Write to a text file
                    FileWriter writer = new FileWriter("hackerrank_ratings.txt", true);
                    writer.write(user.getHandle() + "," + user.getHackerrankHandle() + "," + user.getHackerrankRating() + "\n");
                    writer.close();
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching Hackerrank rating: " + e.getMessage());
        }
    }

    static class Leaderboard {
        List<LeaderboardModel> models;
    }

    static class LeaderboardModel {
        String hacker;
        double score;
    }

    public static void loadCSVtoSQL(String path) {
        String dbName = "cmrit";
        Connection conn = null;
        BufferedReader reader = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
            Statement statement = conn.createStatement();

            // Create table if not exists
            statement.execute(CREATE_TABLE_SQL);

            // Prepare statement for inserting data
            PreparedStatement preparedStatement = conn.prepareStatement(INSERT_DATA_SQL);

            reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                // skip header and last 2 rows
                if (line.equals("Handle,GeeksForGeeks Handle,Codeforces Handle,LeetCode Handle,CodeChef Handle,HackerRank Handle," + "GeeksForGeeks URL Exists,Codeforces URL Exists,LeetCode URL Exists,CodeChef URL Exists,HackerRank URL Exists"))
                    continue;
                if (line.startsWith("None") || line.startsWith("TOTAL")) continue;
                String[] data = line.split(",");
                for (int i = 0; i < data.length; i++) {
                    if (i == 6 || i == 7 || i == 8 || i == 9 || i == 10) {
                        preparedStatement.setBoolean(i + 1, Boolean.parseBoolean(data[i])); // URL Exists (true or false)
                    } else {
                        preparedStatement.setString(i + 1, data[i]);
                    }
                }
                preparedStatement.addBatch();
            }

            // Execute batch insert
            preparedStatement.executeBatch();

        } catch (SQLException | IOException e) {
            System.err.println("Error loading CSV data to SQL database: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) reader.close();
                if (conn != null) conn.close();
            } catch (IOException | SQLException e) {
                System.err.println("Error closing reader or connection: " + e.getMessage());
            }
        }
    }
}

class User {
    private String handle;
    private String codeforcesHandle;
    private String geeksforgeeksHandle;
    private String leetcodeHandle;
    private String codechefHandle;
    private String hackerrankHandle;

    private Integer codeforcesRating;
    private Integer geeksforgeeksRating;
    private Integer geesforgeeksPracticeRating;
    private Integer leetcodeRating;
    private Integer codechefRating;
    private Integer hackerrankRating;
    private Double percentile;

    public User(String handle, String platform, String username) {
        this.handle = handle;
        switch (platform) {
            case "codeforces" -> this.codeforcesHandle = username;
            case "geeksforgeeks" -> this.geeksforgeeksHandle = username;
            case "leetcode" -> this.leetcodeHandle = username;
            case "codechef" -> this.codechefHandle = username;
            case "hackerrank" -> this.hackerrankHandle = username;
        }
        // set all ratings to 0
        this.codeforcesRating = 0;
        this.geeksforgeeksRating = 0;
        this.geesforgeeksPracticeRating = 0;
        this.leetcodeRating = 0;
        this.codechefRating = 0;
        this.hackerrankRating = 0;
    }

    public User(String handle, String codeforcesHandle, String geeksforgeeksHandle, String leetcodeHandle, String codechefHandle, String hackerrankHandle) {
        this.handle = handle;
        this.codeforcesHandle = codeforcesHandle;
        this.geeksforgeeksHandle = geeksforgeeksHandle;
        this.leetcodeHandle = leetcodeHandle;
        this.codechefHandle = codechefHandle;
        this.hackerrankHandle = hackerrankHandle;
        // set all ratings to 0
        this.codeforcesRating = 0;
        this.geeksforgeeksRating = 0;
        this.geesforgeeksPracticeRating = 0;
        this.leetcodeRating = 0;
        this.codechefRating = 0;
        this.hackerrankRating = 0;
    }

    // getters

    public String getHandle() {
        return handle;
    }

    public String getCodeforcesHandle() {
        return codeforcesHandle;
    }

    public String getGeeksforgeeksHandle() {
        return geeksforgeeksHandle;
    }

    public String getLeetcodeHandle() {
        return leetcodeHandle;
    }

    public String getCodechefHandle() {
        return codechefHandle;
    }

    public String getHackerrankHandle() {
        return hackerrankHandle;
    }

    public Integer getCodeforcesRating() {
        return codeforcesRating;
    }

    public Integer getGeeksforgeeksRating() {
        return geeksforgeeksRating;
    }

    public Integer getGeesforgeeksPracticeRating() {
        return geesforgeeksPracticeRating;
    }

    public Integer getLeetcodeRating() {
        return leetcodeRating;
    }

    public Integer getCodechefRating() {
        return codechefRating;
    }

    public Integer getHackerrankRating() {
        return hackerrankRating;
    }

    public Double getPercentile() {
        return percentile;
    }

    // setters

    public void setCodeforcesRating(Integer codeforcesRating) {
        this.codeforcesRating = codeforcesRating;
    }

    public void setGeeksforgeeksRating(Integer geeksforgeeksRating) {
        this.geeksforgeeksRating = geeksforgeeksRating;
    }

    public void setGeesforgeeksPracticeRating(Integer geesforgeeksPracticeRating) {
        this.geesforgeeksPracticeRating = geesforgeeksPracticeRating;
    }

    public void setLeetcodeRating(Integer leetcodeRating) {
        this.leetcodeRating = leetcodeRating;
    }

    public void setCodechefRating(Integer codechefRating) {
        this.codechefRating = codechefRating;
    }

    public void setHackerrankRating(Integer hackerrankRating) {
        this.hackerrankRating = hackerrankRating;
    }

    public void setPercentile(Double percentile) {
        this.percentile = percentile;
    }
}

