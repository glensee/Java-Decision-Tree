import java.util.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat.Style;

public class Gini {
    public static void main(String[] args) {

        // Remember to Change csvFile Path to local directory
        String csvFile = "/Users/sheryll/Desktop/SMU/Y2SEM1/CS201/Project/DSA/data/data.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ","; // use comma as separator

        try {
            List<String[]> rows = new ArrayList<>();
            int headerCount = 0;

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] dataLine = line.split(cvsSplitBy);
                rows.add(dataLine);
                headerCount = dataLine.length;
            }

            // Add headers into ArrayList
            ArrayList<String> headers = new ArrayList<>();
            for (int h = 0; h < headerCount; h++) {
                headers.add(rows.get(0)[h]);
            }

            List<Double> acousticnessList = new ArrayList<>();
            List<Double> danceabilityList = new ArrayList<>();
            List<Double> energyList = new ArrayList<>();
            List<Double> instrumentalnessList = new ArrayList<>();
            List<Double> livenessList = new ArrayList<>();
            List<Double> loudnessList = new ArrayList<>();
            List<Double> speechinessList = new ArrayList<>();
            List<Double> tempoList = new ArrayList<>();
            List<Double> valenceList = new ArrayList<>();

            List<Integer> durationInMSList = new ArrayList<>();
            List<Integer> explictValList = new ArrayList<>();
            List<Integer> keyList = new ArrayList<>();
            List<Integer> modeList = new ArrayList<>();
            List<Integer> popularityList = new ArrayList<>();
            List<Integer> yearList = new ArrayList<>();

            /*
             * Generate Respective Arraylists
             */
            int headerCounter = 0;
            while (headerCounter != headerCount) {
                for (int i = 1; i < rows.size(); i++) {
                    String regexInt = "^-?\\d+$";
                    String regexDouble = "^-?\\d*\\.\\d+|\\d+\\.\\d*$";
                    String regexDoubleWithLetters = "\\.?\\b[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?[fD]?\\b";
                    String str = rows.get(i)[headerCounter];
                    String header = headers.get(headerCounter);
                    if (str.matches(regexDouble) || str.matches(regexDoubleWithLetters)) {
                        Double doubleValue = Double.parseDouble(str);
                        switch (header) {
                            case "acousticness":
                                acousticnessList.add(doubleValue);
                                break;
                            case "danceability":
                                danceabilityList.add(doubleValue);
                                break;
                            case "energy":
                                energyList.add(doubleValue);
                                break;
                            case "instrumentalness":
                                instrumentalnessList.add(doubleValue);
                                break;
                            case "liveness":
                                livenessList.add(doubleValue);
                                break;
                            case "loudness":
                                loudnessList.add(doubleValue);
                                break;
                            case "speechiness":
                                speechinessList.add(doubleValue);
                                break;
                            case "tempo":
                                tempoList.add(doubleValue);
                                break;
                            case "valence":
                                valenceList.add(doubleValue);
                                break;
                        }

                        if (str.matches(regexInt)) {
                            Integer intValue = Integer.parseInt(str);
                            switch (header) {
                                case "duration_ms":
                                    durationInMSList.add(intValue);
                                    break;
                                case "explicit":
                                    explictValList.add(intValue);
                                    break;
                                case "key":
                                    keyList.add(intValue);
                                    break;
                                case "mode":
                                    modeList.add(intValue);
                                    break;
                                case "popularity":
                                    popularityList.add(intValue);
                                    break;
                                case "year":
                                    yearList.add(intValue);
                                    break;
                            }
                        }
                    }
                }
                headerCounter++;
            }

            ArrayList<List<Double>> doubleCols = new ArrayList<>();
            doubleCols.add(acousticnessList);
            doubleCols.add(danceabilityList);
            doubleCols.add(energyList);
            doubleCols.add(instrumentalnessList);
            doubleCols.add(livenessList);
            doubleCols.add(loudnessList);
            doubleCols.add(speechinessList);
            doubleCols.add(tempoList);
            doubleCols.add(valenceList);

            ArrayList<List<Integer>> intCols = new ArrayList<>();
            intCols.add(durationInMSList);
            intCols.add(explictValList);
            intCols.add(keyList);
            intCols.add(modeList);
            intCols.add(popularityList);
            intCols.add(yearList);

            /*
             * Call GINI methods
             */

            System.out.println(getGiniIndexForDouble(acousticnessList, popularityList) + "\n");
            System.out.println(getGiniIndexForDouble(danceabilityList, popularityList) + "\n");
            System.out.println(getGiniIndexForDouble(energyList, popularityList) + "\n");
            System.out.println(getGiniIndexForDouble(instrumentalnessList, popularityList) + "\n");
            System.out.println(getGiniIndexForDouble(livenessList, popularityList) + "\n");
            System.out.println(getGiniIndexForDouble(loudnessList, popularityList) + "\n");
            System.out.println(getGiniIndexForDouble(speechinessList, popularityList) + "\n");
            System.out.println(getGiniIndexForDouble(tempoList, popularityList) + "\n");
            System.out.println(getGiniIndexForDouble(valenceList, popularityList) + "\n");

            System.out.println(getGiniIndexForInt(durationInMSList, popularityList) + "\n");
            System.out.println(getGiniIndexForInt(explictValList, popularityList) + "\n");
            System.out.println(getGiniIndexForInt(keyList, popularityList) + "\n");
            System.out.println(getGiniIndexForInt(modeList, popularityList) + "\n");
            System.out.println(getGiniIndexForInt(popularityList, popularityList) + "\n");
            System.out.println(getGiniIndexForInt(yearList, popularityList) + "\n");

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

    public static List<Double> getGiniIndexForDouble(List<Double> characteristic, List<Integer> popularity) {
        double characteristic1_Popular = 0.0; // have the characteristic and is Popular
        double characteristic1_notPopular = 0.0; // have the characteristic and is not Popular
        double characteristic0_Popular = 0.0; // doesn't have the characteristic and is Popular
        double characteristic0_notPopular = 0.0; // doesn't have the characteristic and is not Popular

        int characteristic1Pop1 = 0; // to get the number of songs with the characteristic and is Popular
        int characteristic1Pop0 = 0; // to get the number of songs with the characteristic and is NOT Popular
        int characteristic0Pop1 = 0; // to get the number of songs withOUT the characteristic and is Popular
        int characteristic0Pop0 = 0; // to get the number of songs withOUT the characteristic and is NOT Popular

        double mean = getDoubleMean(characteristic); // to get the mean of the characteristics

        // to get the sum of the different proportions of the classes with respect to
        // the given characteristic and popularity
        for (int i = 0; i < characteristic.size(); i++) {
            if (characteristic.get(i) >= mean && popularity.get(i) > 0) {
                characteristic1Pop1++;
            }
            if (characteristic.get(i) < mean && popularity.get(i) > 0) {
                characteristic0Pop1++;
            }
            if (characteristic.get(i) < mean && popularity.get(i) == 0) {
                characteristic0Pop0++;
            }
            if (characteristic.get(i) >= mean && popularity.get(i) == 0) {
                characteristic1Pop0++;
            }
        }

        // to get the proportion
        characteristic1_Popular = characteristic1Pop1 / (double) characteristic.size();
        characteristic1_notPopular = characteristic1Pop0 / (double) characteristic.size();
        characteristic0_Popular = characteristic0Pop1 / (double) characteristic.size();
        characteristic0_notPopular = characteristic0Pop0 / (double) characteristic.size();

        // to get the gini index for each group
        double a1p1_gini_group = (1.0 - characteristic1_Popular * characteristic1_Popular)
                * (characteristic1Pop1 / (double) characteristic.size());
        double a1p0_gini_group = (1.0 - characteristic1_notPopular * characteristic1_Popular)
                * (characteristic1Pop0 / (double) characteristic.size());
        double a0p1_gini_group = (1.0 - characteristic0_Popular * characteristic1_Popular)
                * (characteristic0Pop1 / (double) characteristic.size());
        double a0p0_gini_group = (1.0 - characteristic0_notPopular * characteristic1_Popular)
                * (characteristic0Pop0 / (double) characteristic.size());

        List<Double> giniList = new ArrayList<Double>(); // to insert the different gini into the arraylist

        giniList.add(a1p1_gini_group); // giniList.get(0) will return the gini index for have the characteristic and is
                                       // Popular
        giniList.add(a1p0_gini_group); // giniList.get(1) will return the gini index for have the characteristic and is
                                       // NOT Popular
        giniList.add(a0p1_gini_group); // giniList.get(2) will return the gini index for doesn't have the
                                       // characteristic and is Popular
        giniList.add(a0p0_gini_group); // giniList.get(3) will return the gini index for doesn't have the
                                       // characteristic and is NOT Popular

        return giniList;
    }

    public static double getDoubleMean(List<Double> characteristic) {
        double total = 0;
        for (int i = 0; i < characteristic.size(); i++) {
            total += characteristic.get(i);
        }
        System.out.println(total / characteristic.size());
        return total / characteristic.size();
    }

    public static List<Double> getGiniIndexForInt(List<Integer> characteristic, List<Integer> popularity) {
        double characteristic1_Popular = 0.0; // have the characteristic and is Popular
        double characteristic1_notPopular = 0.0; // have the characteristic and is not Popular
        double characteristic0_Popular = 0.0; // doesn't have the characteristic and is Popular
        double characteristic0_notPopular = 0.0; // doesn't have the characteristic and is not Popular

        int characteristic1Pop1 = 0; // to get the number of songs with the characteristic and is Popular
        int characteristic1Pop0 = 0; // to get the number of songs with the characteristic and is NOT Popular
        int characteristic0Pop1 = 0; // to get the number of songs withOUT the characteristic and is Popular
        int characteristic0Pop0 = 0; // to get the number of songs withOUT the characteristic and is NOT Popular

        double mean = getIntMean(characteristic); // to get the mean of the characteristics

        // to get the sum of the different proportions of the classes with respect to
        // the given characteristic and popularity
        for (int i = 0; i < characteristic.size(); i++) {
            if (characteristic.get(i) >= mean && popularity.get(i) > 0) {
                characteristic1Pop1++;
            }
            if (characteristic.get(i) < mean && popularity.get(i) > 0) {
                characteristic0Pop1++;
            }
            if (characteristic.get(i) < mean && popularity.get(i) == 0) {
                characteristic0Pop0++;
            }
            if (characteristic.get(i) >= mean && popularity.get(i) == 0) {
                characteristic1Pop0++;
            }
        }

        // to get the proportion
        characteristic1_Popular = characteristic1Pop1 / (double) characteristic.size();
        characteristic1_notPopular = characteristic1Pop0 / (double) characteristic.size();
        characteristic0_Popular = characteristic0Pop1 / (double) characteristic.size();
        characteristic0_notPopular = characteristic0Pop0 / (double) characteristic.size();

        // to get the gini index for each group
        double a1p1_gini_group = (1.0 - characteristic1_Popular * characteristic1_Popular)
                * (characteristic1Pop1 / (double) characteristic.size());
        double a1p0_gini_group = (1.0 - characteristic1_notPopular * characteristic1_Popular)
                * (characteristic1Pop0 / (double) characteristic.size());
        double a0p1_gini_group = (1.0 - characteristic0_Popular * characteristic1_Popular)
                * (characteristic0Pop1 / (double) characteristic.size());
        double a0p0_gini_group = (1.0 - characteristic0_notPopular * characteristic1_Popular)
                * (characteristic0Pop0 / (double) characteristic.size());

        List<Double> giniList = new ArrayList<Double>(); // to insert the different gini into the arraylist

        giniList.add(a1p1_gini_group); // giniList.get(0) will return the gini index for have the characteristic and is
                                       // Popular
        giniList.add(a1p0_gini_group); // giniList.get(1) will return the gini index for have the characteristic and is
                                       // NOT Popular
        giniList.add(a0p1_gini_group); // giniList.get(2) will return the gini index for doesn't have the
                                       // characteristic and is Popular
        giniList.add(a0p0_gini_group); // giniList.get(3) will return the gini index for doesn't have the
                                       // characteristic and is NOT Popular

        return giniList;
    }

    public static double getIntMean(List<Integer> characteristic) {
        double total = 0;
        for (int i = 0; i < characteristic.size(); i++) {
            total += characteristic.get(i);
        }
        System.out.println(total / characteristic.size());
        return total / characteristic.size();
    }

    public ArrayList<ArrayList<Integer>> test_split(int index, double value, List<String[]> rows) {
        ArrayList<Integer> left = new ArrayList<>();
        ArrayList<Integer> right = new ArrayList<>();
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        result.add(left);
        result.add(right);

        for (int i = 0; i < rows.size(); i++) {
            double curr = Double.parseDouble(rows.get(i)[index]);
            if (curr < value) {
                left.add(i);
            } else {
                right.add(i);
            }
        }

        return result;
    }

    public HashMap<String, Object> getSplit(List<String[]> rows, List<Integer> popularity,
            ArrayList<List<Double>> doubleCols, ArrayList<List<Integer>> intCols) {
        HashMap<String, Object> result = new HashMap<>();

        int b_index, b_value, b_score;
        b_index = b_value = b_score = 999;
        ArrayList<ArrayList<String[]>> b_groups = null; // [left,right] left is a list of rows

        // Loop through each of the attributes
        for (int i = 0; i < doubleCols.size(); i++) {
            // Loop through each of the rows with that attribute
            for (double value : doubleCols.get(i)) {

                // Returns me a left and right with a list of the index
                ArrayList<ArrayList<Integer>> groups = test_split(i, value, rows);
            }
        }

        for (int i = 0; i < dataset.get(0).size(); i++) {
            for (ArrayList<Integer> row : dataset) {
                ArrayList<ArrayList<ArrayList<Integer>>> groups = test_split(i, row.get(i), dataset);
                Integer gini = getGiniIndex(groups, popularity); // Parameters still need some work
                if (gini < b_score) {
                    b_index = i;
                    b_value = row.get(i);
                    b_score = gini;
                    b_groups = groups;
                }

            }
        }

        result.put("index", b_index);
        result.put("value", b_value);
        result.put("groups", b_groups);

        return result;
    }
}
