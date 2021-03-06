import java.util.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.text.NumberFormat.Style;
import java.time.temporal.Temporal;

public class DataTransformation {

    public static ArrayList<ArrayList<Double>> getData(String csvFilePath) {
        // Remember to Change csvFile Path to local directory
        String csvFile = csvFilePath;
        BufferedReader br = null;
        String line = "";
        ArrayList<ArrayList<Double>> data = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(csvFile));
            br.readLine();
            while ((line = br.readLine()) != null) {
                ArrayList<Double> rows = new ArrayList<>();
                String[] strLine = line.split(",");

                // make sure that <50 == 0 , >50 == 1 for popularity to change it into a boolean
                // 0 being not popular and 1 being popular
                if (Double.parseDouble(strLine[10]) >= 50) {
                    strLine[10] = Integer.toString(1);
                } else {
                    strLine[10] = Integer.toString(0);
                }

                // to swap the position of popular and year columns
                var temp = strLine[10];
                strLine[10] = strLine[14];
                strLine[14] = temp;

                // to add the elements into the rows
                // Removal of categorical values
                // Mode (4), Explicit (9), Year (10) Columns
                for (int i = 0; i < strLine.length; i++) {
                    if (i == 4 || i == 9 || i == 10) {
                        continue;
                    } else {
                        rows.add(Double.parseDouble(strLine[i]));
                    }
                }

                // to add the rows into the dataset
                data.add(rows);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return data;
    }

}