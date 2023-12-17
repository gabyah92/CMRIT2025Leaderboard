// Built OVERNIGHT, yes again, by https://www.instagram.com/gabyah92 (Instagram)
// Technical Trainer

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import jdk.jfr.SettingControl;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CMRITLeaderboard2025{
    private String excelSheetField = "lib//CMRIT2025Leaderboard.xlsx";
    HashMap <String, Integer> geeksforgeeksDB = new HashMap<>();
    HashMap <String, Integer> hackerrankDB = new HashMap<>();
    HashMap <String, Integer> codeforcesDB = new HashMap<>();

    static boolean hackerrankchk = false;
    static String searchToken = "";
    static int hackerrankMaxScore = 0;
    static int GFGMaxScore = 0;
    static int GFGpMaxScore = 0;
    static int codechefMaxRating = 0;
    static int leetcodeMaxRating = 0;
    static int codeforcesMaxRating = 0;

    public CMRITLeaderboard2025(boolean is_minimal) {
        hackerrankMaxScore = 0;
        GFGMaxScore = 0;
        GFGpMaxScore = 0;
        codechefMaxRating = 0;
        leetcodeMaxRating = 0;
        codeforcesMaxRating = 0;
        searchToken = "cmrit25-1-basics, cmrit25-4-rbd, cmrit25-3-iterables, cmrit25-2-lpb, cmrit25-5-ds, 1-basics-2025, 2-loops-2025, 3-bitpat-2025, 4-iterables-2025, ds-2025, codevita-2025";
        if (searchToken.equals("")) {
            hackerrankchk = false;
        }
        else hackerrankchk = true;
        List<Participant> curr_leaderboard = null;

        if (is_minimal) {
            System.out.println("Loading Minimal Excel Sheet");
            excelSheetField = "lib//CMRIT2025Leaderboard_Minimal.xlsx";
        }

        String excelSheetPath = excelSheetField;
        System.out.print("Loading User Data from " + excelSheetPath);
        if (!excelSheetPath.equals("")) {
            try{
                curr_leaderboard = loadExcelSheet(excelSheetPath);
                if(curr_leaderboard.isEmpty()) throw new Exception();
                downloadLeaderboard(curr_leaderboard);
                // Sort and assign ranks
                assignRanks(curr_leaderboard);
                // Display the leaderboard in console
                exportParticipantsToExcel((ArrayList<Participant>) curr_leaderboard);
                curr_leaderboard.clear();
            } catch(Exception f) { System.out.println("Invalid Excel Sheet! " + "Error!!!"); }
        } else {
            System.out.println("Select an Excel Sheet! " + "Error!!!");
            // if Djava.awt.headless=true then exit the program
            if (System.getProperty("java.awt.headless") != null && System.getProperty("java.awt.headless").equals("true")) {
                System.exit(0);
            }
        }
        this.geeksforgeeksDB.clear();
        this.hackerrankDB.clear();
        this.codeforcesDB.clear();
    }

    static void exportParticipantsToExcel(ArrayList<Participant> participants) {
        System.out.println("Generating Excel Sheet...");
        try {
            // Create a new Workbook
            XSSFWorkbook workbook = new XSSFWorkbook();

            // Create a new Sheet
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Current CMRITLeaderboard2025");

            // Create bold font with size 18 for column headers
            org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldFont.setFontHeightInPoints((short) 20);

            org.apache.poi.ss.usermodel.Font boldFont2 = workbook.createFont();
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

            // Add column headers =>
            // Rank, Codeforces_Handle, 35% Codeforces_Rating,
            //       GFG_Handle, 30% GFG_Contest_Score, 10% GFG_Practice_Score,
            //       Leetcode_Handle, 15% Leetcode_Rating
            //       CodeChef_Handle, 10% Codechef_Rating
            Row headerRow = sheet.createRow(0);
            Cell rankHeaderCell = headerRow.createCell(0);
            rankHeaderCell.setCellValue("Rank");
            rankHeaderCell.setCellStyle(boldCenteredCellStyle);

            Cell handleHeaderCell = headerRow.createCell(1);
            handleHeaderCell.setCellValue("Handle");
            handleHeaderCell.setCellStyle(boldCenteredCellStyle);

            Cell codeforcesIdHeaderCell = headerRow.createCell(2);
            codeforcesIdHeaderCell.setCellValue("Codeforces_Handle");
            codeforcesIdHeaderCell.setCellStyle(boldCenteredCellStyle);

            Cell codeforcesRatingHeaderCell = headerRow.createCell(3);
            codeforcesRatingHeaderCell.setCellValue("Codeforces_Rating");
            codeforcesRatingHeaderCell.setCellStyle(boldCenteredCellStyle);

            Cell gfgHeaderCell = headerRow.createCell(4);
            gfgHeaderCell.setCellValue("GFG_Handle");
            gfgHeaderCell.setCellStyle(boldCenteredCellStyle);

            Cell gfgCScoreHeaderCell = headerRow.createCell(5);
            gfgCScoreHeaderCell.setCellValue("GFG_Contest_Score");
            gfgCScoreHeaderCell.setCellStyle(boldCenteredCellStyle);

            Cell gfgPScoreHeaderCell = headerRow.createCell(6);
            gfgPScoreHeaderCell.setCellValue("GFG_Practice_Score");
            gfgPScoreHeaderCell.setCellStyle(boldCenteredCellStyle);

            Cell leetcodeHeaderCell = headerRow.createCell(7);
            leetcodeHeaderCell.setCellValue("Leetcode_Handle");
            leetcodeHeaderCell.setCellStyle(boldCenteredCellStyle);

            Cell leetcodeRatingHeaderCell = headerRow.createCell(8);
            leetcodeRatingHeaderCell.setCellValue("Leetcode_Rating");
            leetcodeRatingHeaderCell.setCellStyle(boldCenteredCellStyle);

            Cell codechefHeaderCell = headerRow.createCell(9);
            codechefHeaderCell.setCellValue("Codechef_Handle");
            codechefHeaderCell.setCellStyle(boldCenteredCellStyle);

            Cell codechefRatingHeaderCell = headerRow.createCell(10);
            codechefRatingHeaderCell.setCellValue("Codechef_Rating");
            codechefRatingHeaderCell.setCellStyle(boldCenteredCellStyle);

            int hc = 11;

            if( hackerrankchk ){
                hc = 13;
                Cell hackerRankHeaderCell = headerRow.createCell(11);
                hackerRankHeaderCell.setCellValue("HackerRank_Handle");
                hackerRankHeaderCell.setCellStyle(boldCenteredCellStyle);

                Cell hackerRankRatingHeaderCell = headerRow.createCell(12);
                hackerRankRatingHeaderCell.setCellValue("HackerRank_Practice_Score");
                hackerRankRatingHeaderCell.setCellStyle(boldCenteredCellStyle);
            }

            Cell percentileHeaderCell = headerRow.createCell(hc);
            percentileHeaderCell.setCellValue("Percentile");
            percentileHeaderCell.setCellStyle(boldCenteredCellStyle);

            // Add participants' data : Rank, Codeforces_Handle, 35% Codeforces_Rating,
            //       GFG_Handle, 30% GFG_Contest_Score, 10% GFG_Practice_Score,
            //       Leetcode_Handle, 15% Leetcode_Rating
            //       CodeChef_Handle, 10% Codechef_Rating
            for (int i = 0; i < participants.size(); i++) {
                Participant participant = participants.get(i);
                Row row = sheet.createRow(i + 1);

                Cell rankCell = row.createCell(0);
                rankCell.setCellValue(participant.getRank());
                rankCell.setCellStyle(boldCellStyle);

                Cell handleCell = row.createCell(1);
                handleCell.setCellValue(participant.getHandle());
                handleCell.setCellStyle(boldCellStyle);

                Cell idCell1 = row.createCell(2);
                idCell1.setCellValue(participant.getCodeforcesHandle());
                idCell1.setCellStyle(boldCellStyle);

                Cell scoreCell1 = row.createCell(3);
                scoreCell1.setCellValue(participant.getCodeforcesRating());
                scoreCell1.setCellStyle(boldCellStyle);

                Cell idCell2 = row.createCell(4);
                idCell2.setCellValue(participant.getGeeksForGeeksHandle());
                idCell2.setCellStyle(boldCellStyle);

                Cell scoreCell2 = row.createCell(5);
                scoreCell2.setCellValue(participant.getGeeksForGeeksScore());
                scoreCell2.setCellStyle(boldCellStyle);

                Cell scoreCell2_1 = row.createCell(6);
                scoreCell2_1.setCellValue(participant.getGeeksForGeekspScore());
                scoreCell2_1.setCellStyle(boldCellStyle);

                Cell idCell3 = row.createCell(7);
                idCell3.setCellValue(participant.getLeetcodeHandle());
                idCell3.setCellStyle(boldCellStyle);

                Cell scoreCell3 = row.createCell(8);
                scoreCell3.setCellValue(participant.getLeetcodeRating());
                scoreCell3.setCellStyle(boldCellStyle);

                Cell idCell4 = row.createCell(9);
                idCell4.setCellValue(participant.getCodeChefHandle());
                idCell4.setCellStyle(boldCellStyle);

                Cell scoreCell4 = row.createCell(10);
                scoreCell4.setCellValue(participant.getCodeChefRating());
                scoreCell4.setCellStyle(boldCellStyle);

                if(hackerrankchk){
                    Cell idCell5 = row.createCell(11);
                    idCell5.setCellValue(participant.getHackerrankHandle());
                    idCell5.setCellStyle(boldCellStyle);

                    Cell scoreCell5 = row.createCell(12);
                    scoreCell5.setCellValue(participant.getHackerrankScore());
                    scoreCell5.setCellStyle(boldCellStyle);
                }
                Cell scoreCell6 = row.createCell(hc);
                DecimalFormat df = new DecimalFormat("#.##");
                scoreCell6.setCellValue( df.format(participant.getPercentile())+"%" );
                scoreCell6.setCellStyle(boldCellStyle);
            }

            File folder = new File("Leaderboards");
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Resize columns to fit the content
            for(int i=0;i<12 + (hackerrankchk?2:0);i++) sheet.autoSizeColumn(i);

            String baseFileName = "Leaderboards/CurrentCMRITLeaderboard2025";
            String extension = ".xlsx";


            File file = new File(baseFileName + extension);
            // if file exists, delete it
            if (file.exists()) {
                file.delete();
            }
            try ( FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                System.out.println("Excel file created successfully!");
                workbook.close();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch ( Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private List<Participant> downloadLeaderboard(List <Participant>list) throws Exception {
        try{
            String url;
            URI websiteUrl;
            URLConnection connection;
            HttpURLConnection o;
            InputStream inputStream;
            // geeksforgeeks
            System.out.println("Downloading GFG leaderboard...");
            for(int j=1;j<=10000;j++){
                System.out.println("Page "+j);
                try{
                    url = "https://practiceapi.geeksforgeeks.org/api/latest/events/recurring/gfg-weekly-coding-contest/leaderboard/?leaderboard_type=0&page="+j;
                    websiteUrl = new URI(url);
                    connection = new URI(url).toURL().openConnection();
                    o = (HttpURLConnection) websiteUrl.toURL().openConnection();
                    o.setRequestMethod("GET");
                    if (o.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || o.getResponseCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE){ continue; }
                    inputStream = connection.getInputStream();
                    try ( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                        StringBuilder jsonContent = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            jsonContent.append(line);
                        }
                        JSONObject jsonObject = new JSONObject(jsonContent.toString());
                        JSONArray arr = jsonObject.getJSONArray("results");
                        int n = arr.length();
                        if( n == 0) break;
                        for(int i=0;i<n;i++){
                            JSONObject tmp = arr.getJSONObject(i);
                            String userHandle = tmp.getString("user_handle").toLowerCase();
                            if(geeksforgeeksDB.containsKey(userHandle)) {
                                int score = (int)tmp.getDouble("user_score");
                                System.out.println("GFG: Setting "+userHandle+" to "+score);
                                list.get(geeksforgeeksDB.get(userHandle)).setGeeksForGeeksScore(score);
                                GFGMaxScore = Integer.max(GFGMaxScore, score);
                            }
                        }
                    } catch(Exception t) {}
                }catch(Exception pp) {}
            }
            // hackerrank
            System.out.println("Downloading HR leaderboard...");
            if(hackerrankchk){
                try{
                    String tracker_names[] = searchToken.replace(" ", "").split(",");
                    for(String tracker_name : tracker_names){
                        System.out.print(tracker_name);
                        for(int j=0;j<10000;j+=100){
                            try{
                                url = "https://www.hackerrank.com/rest/contests/" + tracker_name +  "/leaderboard?offset="+j+"&limit=100";
                                websiteUrl = new URI(url);
                                connection = new URI(url).toURL().openConnection();
                                o = (HttpURLConnection) websiteUrl.toURL().openConnection();
                                o.setRequestMethod("GET");
                                connection.setRequestProperty("Accept", "application/json");
                                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                                if (o.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || o.getResponseCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE){ throw new ArithmeticException("INVALID URL : " + tracker_name); }
                                inputStream = connection.getInputStream();
                                try ( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                                    StringBuilder jsonContent = new StringBuilder();
                                    String line;
                                    while ((line = bufferedReader.readLine()) != null) {
                                        jsonContent.append(line);
                                    }
                                    JSONObject jsonObject = new JSONObject(jsonContent.toString());
                                    JSONArray arr = jsonObject.getJSONArray("models");
                                    int n = arr.length();
                                    if( n == 0) break;
                                    for(int i=0;i<n;i++){
                                        JSONObject tmp = arr.getJSONObject(i);
                                        String userHandle = tmp.getString("hacker").toLowerCase();

                                        if( ( !userHandle.isBlank() && !userHandle.equals("[deleted]")) && this.hackerrankDB.containsKey(userHandle)) {
                                            int index = hackerrankDB.get(userHandle);
                                            int score = list.get(index).getHackerrankScore()+(int)tmp.getDouble("score");
                                            System.out.println("HR: Setting "+userHandle+" to "+score);
                                            hackerrankMaxScore = Integer.max(score, hackerrankMaxScore);
                                            list.get(index).setHackerrankScore(score);
                                        }
                                    }
                                } catch(Exception t) {}
                            }
                            catch(ArithmeticException e){

                                break;
                            }
                            catch(Exception pp) {}
                        }
                    }
                }catch(Exception ee){}
            }
            int n = list.size();
            for(int i=0;i<n;i++){
                System.out.println("User "+i+" : "+list.get(i).getHandle());
                System.out.println("---------------------------------");
                // geeksforgeeks overallScore
                try{
                    if(list.get(i).getGeeksForGeeksHandle().isBlank()) throw new Exception("");
                    url = "https://coding-platform-profile-api.onrender.com/geeksforgeeks/"+list.get(i).getGeeksForGeeksHandle();
                    websiteUrl = new URI(url);
                    connection = new URI(url).toURL().openConnection();
                    o = (HttpURLConnection) websiteUrl.toURL().openConnection();
                    o.setRequestMethod("GET");
                    if (o.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || o.getResponseCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE){
                        throw new ArithmeticException();
                    }
                    inputStream = connection.getInputStream();
                    try ( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                        StringBuilder jsonContent = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            jsonContent.append(line);
                        }
                        JSONObject jsonObject = new JSONObject(jsonContent.toString());
                        int score;
                        try{
                            score = jsonObject.getInt("overall_coding_score");
                        }catch(Exception e) { score = 0; }
                        list.get(i).setGeeksForGeekspScore(score);
                        System.out.println("GFG: Setting "+list.get(i).getHandle()+" to "+score);
                        GFGpMaxScore = Integer.max(score, GFGpMaxScore);
                    }catch (Exception e) { }
                }catch(Exception e) {  }

                // Codechef
                try{
                    if(list.get(i).getCodeChefHandle().isBlank()) throw new Exception("");
                    url = "https://codechef-api.vercel.app/"+list.get(i).getCodeChefHandle();
                    websiteUrl = new URI(url);
                    connection = new URI(url).toURL().openConnection();
                    o = (HttpURLConnection) websiteUrl.toURL().openConnection();
                    o.setRequestMethod("GET");
                    if (o.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || o.getResponseCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE){
                        throw new ArithmeticException();
                    }
                    inputStream = connection.getInputStream();
                    try ( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                        StringBuilder jsonContent = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            jsonContent.append(line);
                        }
                        JSONObject jsonObject = new JSONObject(jsonContent.toString());
                        int rating = 0;
                        try{
                            rating = jsonObject.getInt("currentRating");
                        }catch(Exception e) { rating = 0; }
                        list.get(i).setCodeChefRating(rating);
                        System.out.println("CC: Setting "+list.get(i).getHandle()+" to "+rating);
                        codechefMaxRating = Integer.max(codechefMaxRating, rating);
                    }catch (Exception e) { }
                }catch(Exception e){  }

                // leetcode
                try{
                    if(list.get(i).leetcode_handle.isBlank()) throw new Exception("");

                    String username = list.get(i).leetcode_handle;
                    String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);

                    url = "https://leetcode.com/graphql?query=" +
                            URLEncoder.encode("query{userContestRanking(username:\"" + encodedUsername + "\"){rating}}", StandardCharsets.UTF_8);
                    websiteUrl = new URI(url);
                    connection = new URI(url).toURL().openConnection();
                    o = (HttpURLConnection) websiteUrl.toURL().openConnection();
                    o.setRequestMethod("GET");
                    if (o.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || o.getResponseCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE)
                    {  throw new ArithmeticException(); }
                    inputStream = connection.getInputStream();
                    try ( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                        StringBuilder jsonContent = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            jsonContent.append(line);
                        }
                        JSONObject jsonObject = new JSONObject(jsonContent.toString());
                        int rating = 0;
                        try{
                            rating = (int)jsonObject.getJSONObject("data").getJSONObject("userContestRanking").getDouble("rating");
                        }catch(Exception e) { rating = 0; }
                        list.get(i).setLeetcodeRating(rating);
                        leetcodeMaxRating = Integer.max(rating, leetcodeMaxRating);
                    }catch (Exception e) { }
                }catch(Exception e) {  }

                System.out.println("\n");
            }
        } catch(Exception e) {}

        //codeforces
        System.out.println("Getting Codeforces standings...");
        try {
            String url = "https://codeforces.com/api/user.ratedList?activeOnly=false&includeRetired=true";
            JSONArray rows = null;
            try {
                URI websiteUrl = new URI(url);
                URLConnection connection = new URI(url).toURL().openConnection();
                HttpURLConnection o = (HttpURLConnection) websiteUrl.toURL().openConnection();

                o.setRequestMethod("GET");
                if (o.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND || o.getResponseCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {

                }
                System.out.println("Codeforces Response: "+o.getResponseCode());
                InputStream inputStream = connection.getInputStream();
                try ( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    StringBuilder jsonContent = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        jsonContent.append(line);
                    }
                    JSONObject jsonObject = new JSONObject(jsonContent.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("OK")) {
                        rows = jsonObject.getJSONArray("result");
                    }
                }
            } catch ( IOException | NumberFormatException | JSONException e) {

            }
            JSONArray standings = rows;
            try {
                if (standings != null) {
                    for (int j = 0; j < standings.length(); j++) {
                        JSONObject member = standings.getJSONObject(j);
                        String handle = member.getString("handle").toLowerCase();
                        int points = member.getInt("rating");
                        if(codeforcesDB.containsKey(handle)) {
                            System.out.println("CF: Setting "+list.get(codeforcesDB.get(handle)).getHandle()+" to "+points);
                            list.get(codeforcesDB.get(handle)).setCodeforcesRating(points);
                            codeforcesMaxRating = Integer.max(points, codeforcesMaxRating);
                        }
                    }
                } else {
                }
            } catch (Exception e) {

            }
        } catch(Exception e) {  }
        return list;
    }

    private double participantRank(Participant p){ // using normalization and weighted averages
        // metric being 35% codeforces, 30% geeksforgeeks, 10% geeksforgeeks(practice), 15% leetcode, 10% codechef
        // including hackerrank:
        // metric being 30% codeforces, 30% geeksforgeeks, 10% geeksforgeeks(practice), 10% leetcode, 10% codechef, 10% hackerrank
        // 1477 3400 13.03235
        try{
            double cf   =   (p.getCodeforcesRating()/(double)codeforcesMaxRating)  *100  ;
            double gfgs =   (p.getGeeksForGeeksScore()/(double)GFGMaxScore)        *100  ;
            double gfgp =   (p.getGeeksForGeekspScore()/(double)GFGpMaxScore)      *100  ;
            double lc   =   (p.getLeetcodeRating()/(double)leetcodeMaxRating)      *100  ;
            double cc   =   (p.getCodeChefRating()/(double)codechefMaxRating)      *100  ;
            double hr   =   (p.getHackerrankScore()/(double)hackerrankMaxScore)    *100  ;
            double percentile ;
            if( hackerrankchk ) percentile = ( cf * 0.3 + gfgs*0.3  + gfgp*0.1 + lc*0.1 + cc*0.1 + hr*0.1 );
            else                percentile = ( cf * 0.35 + gfgs*0.3  + gfgp*0.1 + lc*0.15 + cc*0.1 );
            p.percentile = percentile;
            return percentile;
        }catch (Exception e) {

            return 0;
        }
    }

    private List<Participant> loadExcelSheet(String excelSheetPath) {
        // Format of excel sheet must be : {Handle, GFG_Handle, Codeforces_Handle, LeetCode_Handle, CodeChef_Handle}
        ArrayList<Participant> participants = new ArrayList<>( );

        try {
            try ( FileInputStream excelFile = new FileInputStream(excelSheetPath);  Workbook workbook = WorkbookFactory.create(excelFile)) {
                // Assuming the data is in the first sheet (index 0)
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

                // Assuming 'Handle' is in column A (index 0) and Other handles start from column B (index 1)
                Iterator<Row> rowIterator = sheet.iterator();
                int handleInd = 0;
                int gfgInd = 1;
                int codeforcesInd = 2;
                int leetcodeInd = 3;
                int codechefInd = 4;
                int hackerrankInd = 5;

                if ( handleInd==-1 || gfgInd == -1 || codeforcesInd == -1 || leetcodeInd == -1 || codechefInd == -1 || sheet.getRow(0).getCell(codeforcesInd) == null || sheet.getRow(0).getCell(gfgInd) == null || sheet.getRow(0).getCell(leetcodeInd) == null || sheet.getRow(0).getCell(codechefInd) == null || sheet.getRow(0).getCell(handleInd) == null  ) {

                    return new ArrayList<>();
                }

                if( (hackerrankchk && sheet.getRow(0).getCell(hackerrankInd) == null   ) ){

                    return new ArrayList<>();
                }

                if (rowIterator.hasNext()) {
                    rowIterator.next();
                }

                int ind = 0;
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    Cell handleCell = row.getCell(handleInd, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell gfgCell = row.getCell(gfgInd, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell codeforcesCell = row.getCell(codeforcesInd, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell leetcodeCell = row.getCell(leetcodeInd, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell codechefCell = row.getCell(codechefInd, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell hackerrankCell = row.getCell(hackerrankInd, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                    Participant participant = new Participant();
                    if( handleCell != null ){
                        participant.setHandle(handleCell.toString().replace(" ", "").toLowerCase());
                    } else break;
                    if ( codeforcesCell != null ) {
                        String cfhandle = codeforcesCell.toString().replace(" ", "").toLowerCase();
                        cfhandle = cfhandle.replace("#n/a", "");
                        cfhandle = cfhandle.replace("@cmritonline.ac.in", "");
                        codeforcesDB.put(cfhandle, ind);
                        participant.setCodeforcesHandle(cfhandle);
                    }
                    if ( gfgCell != null ) {
                        String gfghandle = gfgCell.toString().replace(" ", "").toLowerCase();
                        gfghandle = gfghandle.replace("#n/a", "");
                        gfghandle = gfghandle.replace("@cmritonline.ac.in", "");
                        participant.setGeeksForGeeksHandle(gfghandle);
                        geeksforgeeksDB.put(gfghandle, ind);
                    }
                    if ( leetcodeCell != null ){
                        String lthandle = leetcodeCell.toString().replace(" ", "").toLowerCase();
                        lthandle = lthandle.replace("#n/a", "");
                        lthandle = lthandle.replace("@cmritonline.ac.in", "");
                        participant.setLeetcodeHandle(lthandle);
                    }
                    if ( codechefCell != null ){
                        String cchandle = codechefCell.toString().replace(" ", "").toLowerCase();
                        cchandle = cchandle.replace("#n/a", "");
                        cchandle = cchandle.replace("@cmritonline.ac.in", "");
                        participant.setCodeChefHandle(cchandle);
                    }
                    if ( hackerrankCell != null && hackerrankchk ){
                        String hrhandle = hackerrankCell.toString().replace(" ", "").toLowerCase();
                        hrhandle = hrhandle.replace("#n/a", "");
                        if( hrhandle.length()>1 && hrhandle.charAt(0) == '@') hrhandle = hrhandle.substring(1);
                        hrhandle = hrhandle.replace("@cmritonline.ac.in", "");
                        participant.setHackerrankHandle(hrhandle);
                        hackerrankDB.put(hrhandle, ind);
                    }
                    participants.add(participant);
                    ind++;
                }
            }
        } catch ( Exception e ) {

            return new ArrayList<>();
        }
        return participants;
    }

    private void assignRanks(List<Participant> leaderboard) {
        Collections.sort(leaderboard, (a, b)-> -Double.compare(participantRank(a),participantRank(b) ) );
        try {
            for (int i = 0; i < leaderboard.size(); i++) {
                leaderboard.get(i).setRank(i + 1);
            }
        } catch (Exception e) {

        }
    }

    private class Participant {
        private String handle;

        private String codechef_handle;
        private String codeforces_handle;
        private String leetcode_handle;
        private String geeksforgeeks_handle;
        private String hackerrank_handle;

        private int codechefrating;
        private int codeforcesrating;
        private int leetcoderating;
        private int geeksforgeeksscore;  // Contest Score
        private int geeksforgeekspscore; // Practice Score
        private int hackerrankscore;
        private double percentile;

        private int rank;

        private Participant() {
            this.percentile = 0;
            this.hackerrankscore = 0;
            this.geeksforgeekspscore = 0;
            this.geeksforgeeksscore = 0;
            this.codeforcesrating = 0;
            this.codechefrating = 0;
            this.leetcoderating = 0;
        }

        public void setPercentile(double p){
            this.percentile = p;
        }

        public double getPercentile(){
            return this.percentile;
        }

        public void setHandle(String handle) {
            this.handle = handle;
        }

        public void setHackerrankHandle(String handle) {
            this.hackerrank_handle = handle;
        }

        public void setGeeksForGeeksHandle(String handle) {
            this.geeksforgeeks_handle = handle;
        }

        public void setLeetcodeHandle(String handle) {
            this.leetcode_handle = handle;
        }

        public void setCodeChefHandle(String handle) {
            this.codechef_handle = handle;
        }

        public void setCodeforcesHandle(String handle) {
            this.codeforces_handle = handle;
        }

        public String getGeeksForGeeksHandle() {
            return this.geeksforgeeks_handle;
        }

        public String getLeetcodeHandle() {
            return this.leetcode_handle;
        }

        public String getHackerrankHandle() {
            return this.hackerrank_handle;
        }

        public String getCodeChefHandle() {
            return this.codechef_handle;
        }

        public String getCodeforcesHandle() {
            return this.codeforces_handle;
        }

        public void setGeeksForGeeksScore(int score) {
            this.geeksforgeeksscore = score;
        }

        public void setHackerrankScore(int score) {
            this.hackerrankscore = score;
        }

        public void setGeeksForGeekspScore(int score){
            this.geeksforgeekspscore = score;
        }

        public void setLeetcodeRating(int rating) {
            this.leetcoderating = rating;
        }

        public void setCodeChefRating(int rating) {
            this.codechefrating = rating;
        }

        public void setCodeforcesRating(int rating) {
            this.codeforcesrating = rating;
        }

        public String getHandle() {
            return handle;
        }

        public int getGeeksForGeeksScore() {
            return this.geeksforgeeksscore;
        }

        public int getHackerrankScore() {
            return this.hackerrankscore;
        }

        public int getGeeksForGeekspScore() {
            return this.geeksforgeekspscore;
        }

        public int getLeetcodeRating() {
            return this.leetcoderating;
        }

        public int getCodeChefRating() {
            return this.codechefrating;
        }

        public int getCodeforcesRating() {
            return this.codeforcesrating;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }
    }
    public static void main(String[] args) {
        try {
        if (args.length > 1){
            System.out.println("Invalid Arguments!");
            System.exit(0);
        }
        else if (args.length == 1){
            // Calling the constructor with minimal Excel sheet
            if (Objects.equals(args[0], "minimal")) {
                new CMRITLeaderboard2025(true);
            }
        }
        else {
            // Calling the constructor with default Excel sheet
            new CMRITLeaderboard2025(false);
        }
        } catch (Exception e) {
            System.out.println("Something went wrong!" + "Error!!!");
        }
    }
}
