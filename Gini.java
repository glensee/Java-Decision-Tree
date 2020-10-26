import java.util.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Gini {
    public static void main(String[] args) {
        // Remember to Change csvFile Path to local directory
        String csvFile = "/Users/sheryll/Desktop/SMU/Y2SEM1/CS201/Project/DSA/data/data.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ","; // use comma as separator

        try {
            List<String[]> rows = new ArrayList<>();

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] dataLine = line.split(cvsSplitBy);
                rows.add(dataLine);
            }

            // System.out.println("Row:  "+ rows.get(3)[14]); // For checking

            List<Double> acousticnessList = new ArrayList<Double>();
            for (int i = 1; i <rows.size(); i ++) {
                Double acousticness = Double.parseDouble(rows.get(i)[0]);
                acousticnessList.add(acousticness);
            }

            List<Double> danceability = new ArrayList<Double>();
            for (int i = 1; i <rows.size(); i ++) {
                Double dance = Double.parseDouble(rows.get(i)[1]);
                danceability.add(dance);
            }

            List<Integer> durationInMS = new ArrayList<Integer>();
            for (int i = 1; i <rows.size(); i ++) {
                Integer duration = Integer.parseInt(rows.get(i)[2]);
                durationInMS.add(duration);
            }

            List<Double> energy = new ArrayList<Double>();
            for (int i = 1; i <rows.size(); i ++) {
                Double e = Double.parseDouble(rows.get(i)[3]);
                energy.add(e);
            }

            List<Integer> explictVal = new ArrayList<Integer>();
            for (int i = 1; i <rows.size(); i ++) {
                Integer explicitV = Integer.parseInt(rows.get(i)[4]);
                explictVal.add(explicitV);
            }

            List<String> idList = new ArrayList<String>();
            for (int i = 1; i <rows.size(); i ++) {
                idList.add(rows.get(i)[5]);
            }

            List<Double> instrumentalness = new ArrayList<Double>();
            for (int i = 1; i <rows.size(); i ++) {
                Double instrumental = Double.parseDouble(rows.get(i)[6]);
                instrumentalness.add(instrumental);
            }

            List<Integer> keyList = new ArrayList<Integer>();
            for (int i = 1; i <rows.size(); i ++) {
                Integer key = Integer.parseInt(rows.get(i)[7]);
                keyList.add(key);
            }

            List<Double> livenessList = new ArrayList<Double>();
            for (int i = 1; i <rows.size(); i ++) {
                Double liveness = Double.parseDouble(rows.get(i)[8]);
                livenessList.add(liveness);
            }

            List<Double> loudnessList = new ArrayList<Double>();
            for (int i = 1; i <rows.size(); i ++) {
                Double loudness = Double.parseDouble(rows.get(i)[9]);
                loudnessList.add(loudness);
            }

            List<Integer> modeList = new ArrayList<Integer>();
            for (int i = 1; i <rows.size(); i ++) {
                Integer mode = Integer.parseInt(rows.get(i)[10]);
                modeList.add(mode);
            }

            List<Integer> popularityList = new ArrayList<Integer>();
            for (int i = 1; i <rows.size(); i ++) {
                Integer popular = Integer.parseInt(rows.get(i)[11]);
                popularityList.add(popular);
            }

            List<String> releaseDateList = new ArrayList<String>();
            for (int i = 1; i <rows.size(); i ++) {
                releaseDateList.add(rows.get(i)[12]);
            }

            List<Double> speechinessList = new ArrayList<Double>();
            for (int i = 1; i <rows.size(); i ++) {
                Double speechiness = Double.parseDouble(rows.get(i)[13]);
                speechinessList.add(speechiness);
            }

            List<Double> tempoList = new ArrayList<Double>();
            for (int i = 1; i <rows.size(); i ++) {
                Double tempo = Double.parseDouble(rows.get(i)[14]);
                tempoList.add(tempo);
            }

            List<Double> valenceList = new ArrayList<Double>();
            for (int i = 1; i <rows.size(); i ++) {
                Double valence = Double.parseDouble(rows.get(i)[15]);
                valenceList.add(valence);
            }

            List<Integer> yearList = new ArrayList<Integer>();
            for (int i = 1; i <rows.size(); i ++) {
                Integer yearVal = Integer.parseInt(rows.get(i)[11]);
                yearList.add(yearVal);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}