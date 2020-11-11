import java.util.*;
import java.lang.Math;

public class BestSplit {
    
    /**
     * Alternative 1
     * @param dataset
     * @param i represents the i-th column of the dataframe
     * @return
     */
    public static HashMap<String,Object> split(ArrayList<ArrayList<Double>> dataset, int i) {
        HashMap<String, Object> map = new HashMap<>();

        int last_index = dataset.get(0).size() - 1;
        int n = dataset.size();


        double sum_1 = 0; // class 0 
        double sum_2 = 0; // class 1
        double sq_sum_1 = 0;
        double sq_sum_2 = 0;

        for (ArrayList<Double> row : dataset) {
            if (row.get(last_index).equals(0.0)) {
                sum_1 += row.get(i);
                sq_sum_1 += row.get(i) * row.get(i);
            } else {
                sum_2 += row.get(i);
                sq_sum_2 += row.get(i) * row.get(i);
            }
        }

        double mean_1 = sum_1 / n;
        double mean_2 = sum_2 / n;

        double var_1 = sq_sum_1 / n - mean_1 * mean_1;
        double var_2 = sq_sum_2 / n - mean_2 * mean_2;

        double std_1 = Math.sqrt(var_1);
        double std_2 = Math.sqrt(var_2);

        double split = (mean_2 * var_1 - std_2 * (mean_1 * std_2 + std_1 * Math.sqrt( Math.pow(mean_1 - mean_2, 2) + 
                        2 * (var_1 - var_2) * Math.log(std_1 / std_2)))) / (var_1 - var_2);

        ArrayList<ArrayList<ArrayList<Double>>> groups = DecisionTreeApplication.test_split((Integer) i, split, dataset);

        map.put("index", i);
        map.put("value", split);
        map.put("groups", groups);

        return map;

    }
    public static HashMap<String,Object> split_v2(ArrayList<ArrayList<Double>> dataset, int i, ArrayList<Double> class_values) {
        HashMap<String, Object> map = new HashMap<>();

        int last_index = dataset.get(0).size() - 1;
        int n = dataset.size();

        QuickSort sort = new QuickSort(i);

        sort.sort(dataset, 0, n-1);

        ArrayList<ArrayList<ArrayList<Double>>> groups = DecisionTreeApplication.test_split((Integer) i, (Double) dataset.get(0).get(i), dataset);
        Double start = DecisionTreeApplication.gini_index(groups, class_values);
        int start_index = 0;
         
        groups = DecisionTreeApplication.test_split((Integer) i, (Double) dataset.get(dataset.size() - 1).get(i), dataset);
        Double end = DecisionTreeApplication.gini_index(groups, class_values);
        int end_index = dataset.size() - 1;

        groups = DecisionTreeApplication.test_split((Integer) i, (Double) dataset.get(dataset.size() / 2 ).get(i), dataset);
        Double middle = DecisionTreeApplication.gini_index(groups, class_values);
        int middle_index = dataset.size() / 2;
        while (end_index - middle_index > 2 && middle_index - start_index > 2) {

            if (middle <= end && start <= end) {
                end = middle;
                end_index = middle_index;
                middle_index = (start_index + end_index) / 2;
            } else if (middle <= start && end <= start) {
                start = middle;
                start_index = middle_index;
                middle_index = (start_index + end_index) / 2;

            } else if (middle >= start && middle >= end) {
                if (start > end) {
                    start = middle;
                    start_index = middle_index;
                    middle_index = (start_index + end_index) / 2;
                } else {
                    end = middle;
                    end_index = middle_index;
                    middle_index = (start_index + end_index) / 2;
                }
            }

            groups = DecisionTreeApplication.test_split((Integer) i, (Double) dataset.get(middle_index).get(i), dataset);
            middle = DecisionTreeApplication.gini_index(groups, class_values);
        }

        map.put("index", i);
        map.put("value", dataset.get(middle_index).get(i));
        map.put("groups", groups);
        map.put("gini", middle);








        

        // map.put("index", i);
        // map.put("value", split);
        // map.put("groups", groups);

        return map;
    }
}

