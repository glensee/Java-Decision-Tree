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
    
    /**
     * Gradient Descent or something like it at least
     * @param dataset
     * @param i
     */
    public static HashMap<String, Object> split_v2(ArrayList<ArrayList<Double>> dataset, int i, ArrayList<Double> classes) {
        HashMap<String, Object> map = new HashMap<>();

        int last_index = dataset.get(0).size() - 1;
        int n = dataset.size();

        QuickSort sort = new QuickSort(i);

        sort.sort(dataset, 0, n-1);

        int width = (int) Math.ceil(Math.log(n)) ;

        double momentum = Math.log(n);
        int current = n / 2;
        // Double value = dataset.get(current).get(i);
        int step = (int) Math.ceil(Math.log(n)) ;
        
        
        int iter = 250;
        Double center = 0.0;
        ArrayList<ArrayList<ArrayList<Double>>> groups;


        do {
            iter--;
            try {
                 groups = DecisionTreeApplication.test_split((Integer) i, dataset.get(current - width).get(i), dataset);

                Double left = DecisionTreeApplication.gini_index(groups, classes);

                groups = DecisionTreeApplication.test_split((Integer) i, dataset.get(current + width).get(i), dataset);

                Double right = DecisionTreeApplication.gini_index(groups, classes);

                groups = DecisionTreeApplication.test_split((Integer) i, dataset.get(current).get(i), dataset);

                center = DecisionTreeApplication.gini_index(groups, classes);

                if (left > center && right > center) {
                    width --;
                    momentum --;
                    current += momentum;
                } else if (left > center && center > right) {
                    current += step + momentum;
                } else if (right > center && center > left) {
                    current -= step + momentum;
                } else if (current < n / 2) {
                    current += step + momentum;
                    iter -= 10;
                } else if (current > n / 2) {
                    current -= step + momentum;
                    iter -= 10;

                }

                if (width == 1) {
                    
                    map.put("gini", center);
                    map.put("index", i);
                    map.put("value", dataset.get(current).get(i));
                    map.put("groups", groups);
                    return map;
                }

            } catch (IndexOutOfBoundsException e) {
                System.out.println("AAAA");
                return null;
            }
            

        } while (iter > 0) ;

        

            map.put("gini", center);
            map.put("index", i);
            map.put("value", dataset.get(current).get(i));
            map.put("groups", groups);
            return map;

            
        
    }

}
