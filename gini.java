import java.util.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class gini {
    public static void main(String[] args) {
        // Remember to Change csvFile Path to local directory
        String csvFile = "/Users/cheyennejanlee/Desktop//CS201/proj/archive/data.csv";
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
            giniIndexDoubleList(acousticnessList, popularityList);
            giniIndexIntList(yearList,popularityList);


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

    public static double giniIndexDoubleList(List<Double> characteristic, List<Integer> popularity){
        double characteristic1_Popular = 0.0;       //have the characteristic and is Popular
        double characteristic1_notPopular = 0.0;    //have the characteristic and is not Popular
        double characteristic0_Popular = 0.0;       //doesn't have the characteristic and is Popular
        double characteristic0_notPopular = 0.0;    //doesn't have the characteristic and is not Popular
        
        int characteristic1Pop1 = 0;
        int characteristic1Pop0 = 0;
        int characteristic0Pop1 = 0;
        int characteristic0Pop0 = 0;

        // to get the sum of the different proportions of the classes with respect to the given characteristic and popularity 
        for(int i = 0; i < characteristic.size(); i++){
            if(characteristic.get(i) >= 0.5 && popularity.get(i) > 0){
                characteristic1Pop1++;
            }
            if(characteristic.get(i) < 0.5 && popularity.get(i) > 0){
                characteristic0Pop1++;
            }
            if(characteristic.get(i) < 0.5 && popularity.get(i) == 0){
                characteristic0Pop0++;
            }
            if(characteristic.get(i) >= 0.5 && popularity.get(i) == 0){
                characteristic1Pop0++;
            }

        }
        // // printing the number of songs that has the given characteristic and the given popularity 
        // System.out.println(characteristic1Pop1);
        // System.out.println(characteristic0Pop1);
        // System.out.println(characteristic0Pop0);
        // System.out.println(characteristic1Pop0);
        // System.out.println(characteristic.size());

        // to get the proportion 
        characteristic1_Popular = characteristic1Pop1 /(double)characteristic.size();
        characteristic1_notPopular = characteristic1Pop0 /(double)characteristic.size();
        characteristic0_Popular = characteristic0Pop1 /(double)characteristic.size();
        characteristic0_notPopular = characteristic0Pop0 /(double)characteristic.size();

        // to print out the proportion
        // System.out.println(characteristic1_Popular);
        // System.out.println(characteristic1_notPopular);
        // System.out.println(characteristic0_Popular);
        // System.out.println(characteristic0_notPopular);

        //to get the gini index for each group 
        double a1p1_gini_group = (1.0 - characteristic1_Popular * characteristic1_Popular) * (characteristic1Pop1/(double)characteristic.size());
        double a1p0_gini_group = (1.0 - characteristic1_notPopular * characteristic1_Popular) * (characteristic1Pop0/(double)characteristic.size());
        double a0p1_gini_group = (1.0 - characteristic0_Popular * characteristic1_Popular) * (characteristic0Pop1/(double)characteristic.size());
        double a0p0_gini_group = (1.0 - characteristic0_notPopular * characteristic1_Popular) * (characteristic0Pop0/(double)characteristic.size());

        //printing the gini index for each group
        System.out.println("a1p1_gini_group == " + a1p1_gini_group);
        System.out.println("a1p0_gini_group == " + a1p0_gini_group);
        System.out.println("a0p1_gini_group == " + a0p1_gini_group);
        System.out.println("a0p0_gini_group == " + a0p0_gini_group);
        
        return a0p0_gini_group;
    }
    // to get the gini index when the characteristic list is filled with Integers
    public static double giniIndexIntList(List<Integer> characteristic, List<Integer> popularity){
        double characteristic1_notPopular = 0.0;  
        double characteristic1_Popular = 0.0;
        double characteristic0_notPopular = 0.0;
        double characteristic0_Popular = 0.0;
        int characteristic1Pop1 = 0;
        int characteristic0Pop1 = 0;
        int characteristic0Pop0 = 0;
        int characteristic1Pop0 = 0;

        // to get the sum of the different proportions of the classes with respect to the given characteristic and popularity 
        for(int i = 0; i < characteristic.size(); i++){
            if(characteristic.get(i) >= 0.5 && popularity.get(i) > 0){
                characteristic1Pop1++;
            }
            if(characteristic.get(i) < 0.5 && popularity.get(i) > 0){
                characteristic0Pop1++;
            }
            if(characteristic.get(i) < 0.5 && popularity.get(i) == 0){
                characteristic0Pop0++;
            }
            if(characteristic.get(i) >= 0.5 && popularity.get(i) == 0){
                characteristic1Pop0++;
            }

        }
        // // printing the number of songs that has the given characteristic and the given popularity 
        // System.out.println(characteristic1Pop1);
        // System.out.println(characteristic0Pop1);
        // System.out.println(characteristic0Pop0);
        // System.out.println(characteristic1Pop0);
        // System.out.println(characteristic.size());

        // to get the proportion 
        characteristic1_Popular = characteristic1Pop1 /(double)characteristic.size();
        characteristic1_notPopular = characteristic1Pop0 /(double)characteristic.size();
        characteristic0_Popular = characteristic0Pop1 /(double)characteristic.size();
        characteristic0_notPopular = characteristic0Pop0 /(double)characteristic.size();

        // to print out the proportion
        // System.out.println(characteristic1_Popular);
        // System.out.println(characteristic1_notPopular);
        // System.out.println(characteristic0_Popular);
        // System.out.println(characteristic0_notPopular);

        //to get the gini index for each group 
        double a1p1_gini_group = (1.0 - characteristic1_Popular * characteristic1_Popular) * (characteristic1Pop1/(double)characteristic.size());
        double a1p0_gini_group = (1.0 - characteristic1_notPopular * characteristic1_Popular) * (characteristic1Pop0/(double)characteristic.size());
        double a0p1_gini_group = (1.0 - characteristic0_Popular * characteristic1_Popular) * (characteristic0Pop1/(double)characteristic.size());
        double a0p0_gini_group = (1.0 - characteristic0_notPopular * characteristic1_Popular) * (characteristic0Pop0/(double)characteristic.size());

        //printing the gini index for each group
        System.out.println("a1p1_gini_group == " + a1p1_gini_group);
        System.out.println("a1p0_gini_group == " + a1p0_gini_group);
        System.out.println("a0p1_gini_group == " + a0p1_gini_group);
        System.out.println("a0p0_gini_group == " + a0p0_gini_group);
        
        return a0p0_gini_group;
    }

}
        // //to get the gini index for each child node
        // double a1p1_gini = characteristic1_Popular * (1.0 - characteristic1_Popular);
        // double a1p0_gini = characteristic1_notPopular * (1.0 - characteristic1_Popular);
        // double a0p1_gini = characteristic0_Popular * (1.0 - characteristic1_Popular);
        // double a0p0_gini = characteristic0_notPopular * (1.0 - characteristic1_Popular);

        // //printing the gini index for the child nodes
        // System.out.println("a1p1_gini == " + a1p1_gini);
        // System.out.println("a1p0_gini == " + a1p0_gini);
        // System.out.println("a0p1_gini == " + a0p1_gini);
        // System.out.println("a0p0_gini == " + a0p0_gini);