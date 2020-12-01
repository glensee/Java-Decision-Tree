import java.util.*;
import java.lang.Math;

public class DecisionTreeApplication {

    /**
     * create child splits for a node or make terminal
     * 
     * 
     * @param node
     * @param maxDepth maximum depth of tree
     * @param minSize minimum size in a node
     * @param depth current depth
     */
    public static void split(HashMap<String, Object> node, int maxDepth, int minSize, int depth) {
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

    /**
     * Build a decision tree
     * 
     * @param train training dataset 
     * @param maxDepth maximum depth of tree
     * @param minSize minimum size in a node
     * @return
     */
    public static HashMap<String, Object> buildTree(ArrayList<ArrayList<Double>> train, int maxDepth, int minSize) {
        HashMap<String, Object> root = getSplit(train);
        split(root, maxDepth, minSize, 1);
        return root;
    }

    /**
     * create a terminal node value
     * possible room for improvement as this method
     * creates one more split that has both leaves to 
     * be the same value.
     * 
     * @param group
     * @return
     */
    public static Double toTerminal(ArrayList<ArrayList<Double>> group) {

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
        for (Double key : map.keySet()) {
            if (map.get(key) > max) {
                mostFrequentLabel = key;
            }
        }

        return mostFrequentLabel;
    }

    /**
     * split a dataset based on an attribute and an attribute value
     * Complexity: O(n)
     * 
     * @param index the index of the column that denotes the attribute
     * @param value
     * @param dataset
     * @return
     */
    public static ArrayList<ArrayList<ArrayList<Double>>> test_split(Integer index, Double value,
            ArrayList<ArrayList<Double>> dataset) {
        ArrayList<ArrayList<Double>> left = new ArrayList<>();
        ArrayList<ArrayList<Double>> right = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Double>>> result = new ArrayList<>(); 
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

    /**
     * select the best split point for a dataset
     * 
     * @param dataset
     * @return
     */
    public static HashMap<String, Object> getSplit(ArrayList<ArrayList<Double>> dataset) {
        HashMap<String, Object> result = new HashMap<>();
        Set<Double> hashSet = new HashSet<>();

        for (ArrayList<Double> row : dataset) { 
            hashSet.add(row.get(row.size() - 1));
        }

        ArrayList<Double> class_values = new ArrayList<>(hashSet);

        Integer b_index = 999;
        Double b_value, b_score;
        b_value = b_score = 999.0;

        ArrayList<ArrayList<ArrayList<Double>>> b_groups = null;


        for (int i = 0; i < dataset.get(0).size(); i++) {

            /**
             * This is the brute force algorithm, our initial algorithm, to find the minimum gini. 
             * It iterates through all the data, calculating gini each time
             * 
             * Uncomment to use
             * 
             * Complexity: 
             * O(n^2)
             */
            /*
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
            */



            /**
             * This is the binary search algorithm, an improved algorithm (x-factor) to find the minimum gini
             * More details can be found in BestSplit.java 
             * 
             * Comment out if you want to try the brute force algorithm
             * 
             * Complexity:
             * O(nlogn) Average case
             * O(n^2) Worse case
             */

            HashMap<String, Object> map = BestSplit.split_v2(dataset, i, class_values);

            if ((Double) map.get("gini") < b_score) {
                b_index = (Integer) i;
                b_value = (Double) map.get("value");
                b_score = (Double) map.get("gini");
                b_groups = (ArrayList<ArrayList<ArrayList<Double>>>) map.get("groups");
            }
        }

        result.put("index", b_index);
        result.put("value", b_value);
        result.put("groups", b_groups);

        return result;
    }

    /**
     * make a prediction with the decision tree
     * 
     * @param tree decision tree trained
     * @param row a single observation
     * @return
     */
    public static Double predict_row(HashMap<String, Object> tree, ArrayList<Double> row) {
        Integer index = (Integer) tree.get("index");
        Double value = (Double) tree.get("value");

        if (row.get(index) < value) {
            if (tree.get("left") instanceof HashMap) {

                return predict_row((HashMap<String, Object>) tree.get("left"), row);
            } else {
                return (Double) tree.get("left");
            }
        } else {
            if (tree.get("right") instanceof HashMap) {
                return predict_row((HashMap<String, Object>) tree.get("right"), row);
            } else {
                return (Double) tree.get("right");
            }
        }

    }

    /**
     * Make a prediction of a test dataset
     * 
     * @param tree decision tree trained
     * @param test testing dataset
     * @return
     */
    public static ArrayList<Double> predict(HashMap<String, Object> tree, ArrayList<ArrayList<Double>> test) {
        ArrayList<Double> predictions = new ArrayList<>();

        for (ArrayList<Double> row : test) {
            Double prediction = predict_row(tree, row);
            predictions.add(prediction);
        }
        return predictions;
    }

    /**
     * Simple function to calculate the accuracy of the decision tree
     * 
     * @param actual the actual results expected
     * @param predicted the predicted results from the decision tree
     * @return
     */
    public static Double accuracy_metrics(ArrayList<Double> actual, ArrayList<Double> predicted) {
        Double count = 0.0;

        for (int i = 0; i < actual.size(); i++) {
            if (actual.get(i).equals(predicted.get(i))) {
                count += 1;
            }
        }

        return count / actual.size() * 100;
    }

    /**
     * Calculate the gini index for a split dataset
     * 
     * @param groups
     * @param classes
     * @return
     */
    public static Double gini_index(ArrayList<ArrayList<ArrayList<Double>>> groups, ArrayList<Double> classes) {
        //count all samples at split point
        double n_instances = 0;
        for (ArrayList<ArrayList<Double>> group : groups) {
            n_instances += group.size();
        }
        double gini = 0.0;
        
        //sum weighted gini index for each group 
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
                double p = count / size;
                score += p * p;
            }

            // weigh the group score by its relative size
            gini += (1.0 - score) * size / n_instances;
        }
        return gini;
    }

    /**
     * Splits the dataset into a training and testing dataset
     * 
     * @param dataset
     * @param test_size proportion of dataset to be included in test set
     * @return
     */
    public static ArrayList<ArrayList<ArrayList<Double>>> train_test_split(ArrayList<ArrayList<Double>> dataset,
            double test_size) {
        // Create a new copy and shuffle the elements
        ArrayList<ArrayList<Double>> randomized = (ArrayList) dataset.clone();
        Collections.shuffle(randomized);

        // Find the index where the test size should stop
        double testEnd = dataset.size() * test_size;
        int testIndex = (int) testEnd;

        // Separate the 2 datasets
        ArrayList<ArrayList<Double>> test = new ArrayList<>(randomized.subList(0, testIndex));
        ArrayList<ArrayList<Double>> train = new ArrayList<>(randomized.subList(testIndex, randomized.size()));

        ArrayList<ArrayList<ArrayList<Double>>> result = new ArrayList<>();

        // Add them into the output result
        result.add(train);
        result.add(test);

        return result;
    }

    /**
     * Simple function to extract the last column of a dataset
     * the last column is the column of labels
     * 
     * @param dataframe
     * @return
     */
    public static ArrayList<Double> last_column(ArrayList<ArrayList<Double>> dataframe) {
        ArrayList<Double> last_column = new ArrayList<>();
        int last_index = dataframe.get(0).size() - 1;
        for (ArrayList<Double> row : dataframe) {
            last_column.add(row.get(last_index));
        }
        return last_column;
    }

    public static void main(String[] args) {
        Double test_size = 0.25;

        // Pick the dataset required
        ArrayList<ArrayList<Double>> small_data = DataTransformation.getData("./data/small_dataset.csv"); // 1000
        ArrayList<ArrayList<Double>> data_test = DataTransformation.getData("./data/data_test.csv"); // 8000
        ArrayList<ArrayList<Double>> medium_data = DataTransformation.getData("./data/medium_dataset.csv"); // 16 000
        ArrayList<ArrayList<Double>> big_data = DataTransformation.getData("./data/data_updated.csv"); // 40 000
        // Caution: use of "data_updated.csv" may cause stackoverflow.


        ArrayList<ArrayList<Double>> spotify_data = DataTransformation.getData("./data/spotify.csv");

        // variables to calculate the mean and variance of the accuracy and time complexity
        double mean = 0;
        double var = 0;
        double sum_sq = 0;

        double mean_t = 0;
        double var_t = 0;
        double sum_sq_t = 0;

        // spotify 
        double spotify_mean = 0;
        double spotify_var = 0;
        double spotify_sum_sq = 0;

        for (int i = 0; i < 30; i++) {

            // Replace data_test if the preferred dataset
            ArrayList<ArrayList<ArrayList<Double>>> train_test = train_test_split(medium_data, test_size);
            ArrayList<ArrayList<Double>> train = train_test.get(0);
            ArrayList<ArrayList<Double>> test = train_test.get(1);

            long startTime = System.nanoTime();

            HashMap<String, Object> tree = buildTree(train, 1000, 1);
            ArrayList<Double> predicted = predict(tree, test);
            ArrayList<Double> actual = last_column(test);

            double accuracy = accuracy_metrics(actual, predicted);
            System.out.println("Accuracy: ");
            System.out.println(accuracy);
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000);


            // calculation of sum and sum of squares (for mean and variance)
            mean += accuracy;
            sum_sq += Math.pow(accuracy, 2);

            mean_t += timeElapsed / 1000000;
            sum_sq_t += Math.pow(timeElapsed / 1000000, 2);

            // spotify
            ArrayList<Double> spotify_predicted = predict(tree, spotify_data);
            ArrayList<Double> spotify_actual = last_column(spotify_data);
            double spotify_accuracy = accuracy_metrics(spotify_actual, spotify_predicted);
            System.out.println("Spotify Accuracy: ");
            System.out.println(spotify_accuracy);

            spotify_mean += spotify_accuracy;
            spotify_sum_sq += Math.pow(spotify_accuracy, 2);
            System.out.println();
        }

        // calculation of mean and variance of the accuracy and time complexity
        mean /= 30;
        var = sum_sq / 30 - mean * mean;

        mean_t /= 30;
        var_t = sum_sq_t / 30 - mean_t * mean_t;

        System.out.println("Mean Accuracy: " + mean);
        System.out.println("Variance Accuracy: " + var);

        System.out.println("Mean Time: " + mean_t);
        System.out.println("Variance Time: " + var_t);
        System.out.println();

        // spotify
        spotify_mean /= 30;
        spotify_var = spotify_sum_sq / 30 - spotify_mean * spotify_mean;

        System.out.println("Spotify Mean Accuracy: " + spotify_mean);
        System.out.println("Spotify Variance Accuracy: " + spotify_var);

    }
}