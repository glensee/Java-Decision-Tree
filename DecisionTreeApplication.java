import java.util.*;
import java.lang.Math;

public class DecisionTreeApplication {
    public  static void split(HashMap<String, Object> node, int maxDepth, int minSize, int depth) {
        ArrayList<ArrayList<Double>> left = ((ArrayList<ArrayList<ArrayList<Double>>>) node.get("groups")).get(0);
        ArrayList<ArrayList<Double>> right = ((ArrayList<ArrayList<ArrayList<Double>>>) node.get("groups")).get(1);

        node.remove("groups");

        // Check for a no split
        if (left == null) {
            node.put("left", toTerminal(right));
            node.put("right", toTerminal(right));

            return;
        } else if (right == null) {
            node.put("right", toTerminal(left));
            node.put("left", toTerminal(left));

            return;
        }

        // Check for max depth
        if (depth >= maxDepth) {
            node.put("left", toTerminal(left));
            node.put("right", toTerminal(right));
            return;
        }

        // Process left child
        if (left.size() <= minSize) {
            node.put("left", toTerminal(left));
        } else {
            node.put("left", getSplit(left));
            split((HashMap<String, Object>) node.get("left"), maxDepth, minSize, depth + 1);
        }

        // Process right child
        if (right.size() <= minSize) {
            node.put("right", toTerminal(right));
        } else {
            node.put("right", getSplit(right));
            split((HashMap<String, Object>) node.get("right"), maxDepth, minSize, depth + 1);
        }

    }


    public  static HashMap<String, Object> buildTree(ArrayList<ArrayList<Double>> train, int maxDepth, int minSize) {
        HashMap<String, Object> root = getSplit(train);
        split(root, maxDepth, minSize, 1);
        return root;
    }


    public static  Double toTerminal(ArrayList<ArrayList<Double>> group) {

        HashMap<Double, Integer> map = new HashMap<>();

        for (ArrayList<Double> row : group) {
            Double label = row.get(row.size() - 1);
            if (map.containsKey(label)) {
                map.put(label, map.get(label) + 1);
            } else {
                map.put(label, 1);
            }
        }

        Double max = 0.0;
        Double mostFrequentLabel = 0.0;
        for (Double key: map.keySet()) {
            if (map.get(key) > max ) {
                mostFrequentLabel = key;
            }
        }

        return mostFrequentLabel;
    }


    public  static ArrayList<ArrayList<ArrayList<Double>>> test_split(Integer index, Double value, ArrayList<ArrayList<Double>> dataset) {
        ArrayList<ArrayList<Double>> left = new ArrayList<>();
        ArrayList<ArrayList<Double>> right = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Double>>> result = new ArrayList<>(); // optimise by making it fixed size of 2
        result.add(left);
        result.add(right);

        for (ArrayList<Double> row : dataset) {
            if ((Double) row.get(index) < value) { // we only work with numerical values, no categorical
                left.add(row);
            } else {
                right.add(row);
            }
        }

        return result;
    }


    public  static HashMap<String, Object> getSplit(ArrayList<ArrayList<Double>> dataset) {
        HashMap<String, Object> result = new HashMap<>();
        Set<Double> hashSet = new HashSet<>();

        for (ArrayList<Double> row : dataset) { // is there a simpler way to do this?
            hashSet.add(row.get(row.size() - 1));
        }

        ArrayList<Double> class_values = new ArrayList<>(hashSet);

        Integer b_index = 999;
        Double b_value, b_score;
        b_value = b_score = 999.0;

        ArrayList<ArrayList<ArrayList<Double>>> b_groups = null;

        for (int i = 0; i < dataset.get(0).size(); i++) {

            // Alternative 1: method greatly reduces complexity but also accuracy
            // // using mean and std to find best split 
            // // https://stats.stackexchange.com/questions/103800/calculate-probability-area-under-the-overlapping-area-of-two-normal-distributi
            // HashMap<String, Object> map = BestSplit.split(dataset, i);
            // Double gini = gini_index( (ArrayList<ArrayList<ArrayList<Double>>>) map.get("groups"), class_values);
            // if (gini < b_score) {
            //             b_index = (Integer) i;
            //             b_value = (Double) map.get("value");
            //             b_score = gini;
            //             b_groups = (ArrayList<ArrayList<ArrayList<Double>>>) map.get("groups");
            // }




            // finding best split point manually

            for (ArrayList<Double> row : dataset) {

                
                ArrayList<ArrayList<ArrayList<Double>>> groups = test_split((Integer) i, (Double) row.get(i), dataset);
                Double gini = gini_index(groups, class_values);
                if (gini < b_score) {
                    b_index = (Integer) i;
                    b_value = (Double) row.get(i);
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


    public  static Double predict_row(HashMap<String, Object> tree, ArrayList<Double> row) {
        Integer index = (Integer) tree.get("index");
        Double value = (Double) tree.get("value");



        if (row.get(index) <  value) {
            if (tree.get("left") instanceof HashMap ) {

                return predict_row((HashMap<String, Object>) tree.get("left"), row);
            } else {
                return (Double) tree.get("left");
            }
        } else {
            if (tree.get("right") instanceof HashMap ) {
                return predict_row((HashMap<String, Object>) tree.get("right"), row);
            } else {
                return  (Double) tree.get("right");
            }
        }

    }

    public  static ArrayList<Double> predict(HashMap<String, Object> tree, ArrayList<ArrayList<Double>> test) {
        ArrayList<Double> predictions = new ArrayList<>();

        for (ArrayList<Double> row : test) {
            Double prediction = predict_row(tree, row);
            predictions.add(prediction);
        }
        return predictions;
    }


    // public  static ArrayList<Double> decision_tree(ArrayList<ArrayList<Double>> train, ArrayList<ArrayList<Double>> test, int max_depth, int min_size) {
    //     ArrayList<Double> predictions = new ArrayList<>();
    //     HashMap<String, Object> tree = buildTree(train, max_depth, min_size);

    //     for (ArrayList<Double> row : test) {
    //         Double prediction = predict(tree, row);
    //         predictions.add(prediction);
    //     }

    //     return predictions;
    // }


    public  static Double accuracy_metrics(ArrayList<Double> actual, ArrayList<Double> predicted) {
        Double count = 0.0 ;

        for (int i = 0 ; i < actual.size(); i++) {
            if (actual.get(i).equals(predicted.get(i))) {
                count += 1;
            }
        }

        return count / actual.size() * 100;
    }

    public  static Double gini_index(ArrayList<ArrayList<ArrayList<Double>>> groups, ArrayList<Double> classes) {
        double n_instances = 0;
        for (ArrayList<ArrayList<Double>> group : groups) {
            n_instances += group.size();
        }
        double gini = 0.0;

        for (ArrayList<ArrayList<Double>> group : groups) {
            double size = group.size();

            if (size == 0) {
                continue;
            }

            double score = 0.0;

            for (Double label : classes) {
                int last_index = group.get(0).size() - 1;
                int count = 0;

                for (ArrayList<Double> row : group) {
                    if (row.get(last_index) == label) {
                        count++;
                    }
                }
                double p = count/size;
                score += p * p;
            }
            gini += (1.0 - score) * size / n_instances;
        }
        return gini;
    }

    public static ArrayList<ArrayList<ArrayList<Double>>> train_test_split(ArrayList<ArrayList<Double>> dataset, double test_size) {
        // Create a new copy and shuffle the elements
        ArrayList<ArrayList<Double>> randomized = (ArrayList) dataset.clone();
        Collections.shuffle(randomized);

        // Find the index where the test size should stop
        double testEnd = dataset.size() * test_size;
        int testIndex = (int) testEnd;

        // Separate the 2 datasets
        ArrayList<ArrayList<Double>> test = new ArrayList<>(randomized.subList(0,testIndex));
        ArrayList<ArrayList<Double>> train = new ArrayList<>(randomized.subList(testIndex,randomized.size()));

        ArrayList<ArrayList<ArrayList<Double>>> result = new ArrayList<>();

        // Add them into the output result
        result.add(train);
        result.add(test);

        return result;
    }

    public static ArrayList<Double> last_column(ArrayList<ArrayList<Double>> dataframe) {
        ArrayList<Double> last_column = new ArrayList<>();
        int last_index = dataframe.get(0).size() - 1;
        for (ArrayList<Double> row: dataframe) {
            last_column.add(row.get(last_index));
        }
        return last_column;
    }

    public static void main(String[] args) {
        Double test_size = 0.25;

        // Testing using data
        // ArrayList<ArrayList<Double>> data = DataTransformation.getData("/Users/sheryll/Desktop/SMU/Y2SEM1/CS201/Project_/DSA/data/data_updated.csv");


        // /Users/sheryll/Desktop/SMU/Y2SEM1/CS201/Project_
        // /Users/young/OneDrive/Documents/GitHub/DSA/data
        ArrayList<ArrayList<Double>> small_data = DataTransformation.getData("/Users/young/OneDrive/Documents/GitHub/DSA/data/small_dataset.csv");
        ArrayList<ArrayList<Double>> data_test = DataTransformation.getData("/Users/young/OneDrive/Documents/GitHub/DSA/data/data_test.csv");
        ArrayList<ArrayList<Double>> big_data = DataTransformation.getData("/Users/young/OneDrive/Documents/GitHub/DSA/data/data_updated.csv");

        double mean = 0;
        double var = 0;
        double sum_sq = 0;

        double mean_t = 0;
        double var_t = 0;
        double sum_sq_t = 0;
        for (int i = 0 ; i < 30; i++) {
            

        
            ArrayList<ArrayList<ArrayList<Double>>> train_test = train_test_split(small_data, test_size);
            ArrayList<ArrayList<Double>> train = train_test.get(0);
            ArrayList<ArrayList<Double>> test = train_test.get(1);


            long startTime = System.nanoTime();

            HashMap<String, Object> tree = buildTree(train, 1000, 1);
            ArrayList<Double> predicted  = predict(tree, test);
            ArrayList<Double> actual = last_column(test);

            double accuracy = accuracy_metrics(actual, predicted);
            System.out.println("Accuracy: ");
            System.out.println(accuracy);
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 );

            mean += accuracy;
            sum_sq += Math.pow(accuracy, 2);

            mean_t += timeElapsed / 1000000;
            sum_sq_t += Math.pow(timeElapsed / 1000000 , 2);
        }

        mean /= 30;
        var = sum_sq / 30 - mean * mean;

        mean_t /= 30;
        var_t = sum_sq_t / 30 - mean_t * mean_t;

        // run through the sample test
        System.out.println("Mean Accuracy: " + mean);
        System.out.println("Variance Accuracy: " + var);

        System.out.println("Mean Time: " + mean_t);
        System.out.println("Variance Time: " + var_t);



    }


    // // to add the double values into the arraylist
    // private static void doubleArray(Double d, Double d1, Double i){
    //     ArrayList<Double> doubleArray = new ArrayList<Double>();
    //     doubleArray.add(d);
    //     doubleArray.add(d1);
    //     doubleArray.add(i);

    //     dataSet.add(doubleArray);
    // }
}