import java.util.*;
import java.lang.Math;

public class BestSplit {
    
    /**
     * Alternative 1
     * A simple algorithm that assumed that the distributions of large datasets would be normal
     * If so, then an optimal split point would be the intersection of the two normal distributions
     * as at this intersection, there would be the least amount of incorrectly classified observations.
     * 
     * RESULTS:
     * this algorithm fared VERY poorly. with accuracies of less than 50% at times. 
     * Not surprisingly as the assumption of normality is significant and most of the time does not 
     * hold. 
     * 
     * Compelexity: 
     * O(n) < O(n^2) {complexity of brute force}
     * 
     * @param dataset
     * @param i represents the i-th column of the dataframe
     * @return
     */
    public static HashMap<String,Object> split(ArrayList<ArrayList<Double>> dataset, int i) {
        HashMap<String, Object> map = new HashMap<>();

        int last_index = dataset.get(0).size() - 1;
        int n = dataset.size();

        // variables to solve for the mean and variance
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

        //formula to obtain the intersection of two normal distributions
        double split = (mean_2 * var_1 - std_2 * (mean_1 * std_2 + std_1 * Math.sqrt( Math.pow(mean_1 - mean_2, 2) + 
                        2 * (var_1 - var_2) * Math.log(std_1 / std_2)))) / (var_1 - var_2);

        ArrayList<ArrayList<ArrayList<Double>>> groups = DecisionTreeApplication.test_split((Integer) i, split, dataset);


        map.put("index", i);
        map.put("value", split);
        map.put("groups", groups);

        return map;

    }

    
    /**
     * Alternative 2: An attempt to solve the optimization problem using binary search
     * 
     * The algorithm has two parts.
     * 1) Sorting the data (using quicksort) 
     * 2) finding the minimum gini using binary search
     * 
     * As most optimization problems go, finding the minima does not require finding the global minima
     * hence, this algorithm may perform decently when pitched against superior methods of gradient descent
     * (which I tried to implement and failed). Convergence has not been proven to be guaranteed (as far as
     * I know) but the experimentation with this algorithm has performed favourably. 
     * 
     * Complexity:
     * Average case: O(nlogn) < O(n^2) {brute force}
     * 
     * 
     * @param dataset
     * @param i
     * @param class_values
     * @return
     */
    public static HashMap<String,Object> split_v2(ArrayList<ArrayList<Double>> dataset, int i, ArrayList<Double> class_values) {
        HashMap<String, Object> map = new HashMap<>();

        int n = dataset.size();

        // start by sorting the dataset.  this will allow the minima to become more pronounced
        // O(nlogn) on average case; O(n^2) in worse case
        QuickSort sort = new QuickSort(i);
        sort.sort(dataset, 0, n-1);

        // Start by finding the gini at the first, last and middle index
        // apologies for the lack of refactoring due to time constriants
        ArrayList<ArrayList<ArrayList<Double>>> groups = DecisionTreeApplication.test_split((Integer) i, (Double) dataset.get(0).get(i), dataset);
        Double start = DecisionTreeApplication.gini_index(groups, class_values);
        int start_index = 0;
         
        groups = DecisionTreeApplication.test_split((Integer) i, (Double) dataset.get(dataset.size() - 1).get(i), dataset);
        Double end = DecisionTreeApplication.gini_index(groups, class_values);
        int end_index = dataset.size() - 1;

        groups = DecisionTreeApplication.test_split((Integer) i, (Double) dataset.get(dataset.size() / 2 ).get(i), dataset);
        Double middle = DecisionTreeApplication.gini_index(groups, class_values);
        int middle_index = dataset.size() / 2;

        // 2 is a magic number which was arbitrarily chosen (again, time constraint)
        // complexity: O(logn)
        while (end_index - middle_index > 2 && middle_index - start_index > 2) {

            // essentially, we choose the largest gini to be "removed" and 
            // the two points with the smaller gini will be become the new end points
            if (middle <= end && start <= end) {
                end = middle;
                end_index = middle_index;
                middle_index = (start_index + end_index) / 2;
            } else if (middle <= start && end <= start) {
                start = middle;
                start_index = middle_index;
                middle_index = (start_index + end_index) / 2;

                // in the case that the middle is the largest gini, then 
                // we choose the smaller of the two end points
                // this allows us to reach a local minima (at least)
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

            // upon determining the index that yields the "lowest" (local minima) gini, 
            // calculate the relevant info to return to the tree
            groups = DecisionTreeApplication.test_split((Integer) i, (Double) dataset.get(middle_index).get(i), dataset);
            middle = DecisionTreeApplication.gini_index(groups, class_values);
        }

        map.put("index", i);
        map.put("value", dataset.get(middle_index).get(i));
        map.put("groups", groups);
        map.put("gini", middle);

        return map;
    }
    
}
