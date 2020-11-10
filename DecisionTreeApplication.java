import java.util.*;

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

        // Loops through the dataset, O(n)
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
        System.out.println("+++++++++++++++++++++++++++++++++");

        // Another O(n)
        for (int i = 0; i < dataset.get(0).size(); i++) {
            System.out.println("------------------------------");

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

            BestSplit.split_v2(dataset, i);


            // finding best split point manually

            // Another O(n)
            for (ArrayList<Double> row : dataset) {
                
                ArrayList<ArrayList<ArrayList<Double>>> groups = test_split((Integer) i, (Double) row.get(i), dataset); // O(n)
                Double gini = gini_index(groups, class_values); // O(n), total O(n^3)
                System.out.println(row.get(i) + " " + gini);
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


    public static ArrayList<Double> predict(HashMap<String, Object> tree, ArrayList<ArrayList<Double>> test) {
        ArrayList<Double> predictions = new ArrayList<Double>();

        for (ArrayList<Double> row : test) {
            predictions.add(predict_row(tree, row));
        } 

        return predictions;
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


    // public  static ArrayList<Double> decision_tree(ArrayList<ArrayList<Double>> train, ArrayList<ArrayList<Double>> test, int max_depth, int min_size) {
    //     ArrayList<Double> predictions = new ArrayList<>();
    //     HashMap<String, Object> tree = buildTree(train, max_depth, min_size);

    //         for (ArrayList<Double> row : test) {
    //             Double prediction = predict(tree, row);
    //             predictions.add(prediction);
    //         }

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

    // // test size is in float, ie. 0.20 means 20%
    // public ArrayList<ArrayList<ArrayList<Double>>> train_test_split(ArrayList<ArrayList<Double>> dataset, double test_size) {

    // }

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

                // Loops kn/2 + kn/2 = O(n)
                for (ArrayList<Double> row : group) {
                    if (row.get(last_index).equals(label)) {
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
        Double small = 0.99;

        ArrayList<ArrayList<Double>> small_data = DataTransformation.getData("/D:/Data Structure and Algo/DSA/data/small_dataset.csv");
        ArrayList<ArrayList<Double>> data_test = DataTransformation.getData("/Users/young/OneDrive/Documents/GitHub/DSA/data/data_test.csv");
        ArrayList<ArrayList<Double>> big_data = DataTransformation.getData("/Users/young/OneDrive/Documents/GitHub/DSA/data/data_updated.csv");
        
        ArrayList<ArrayList<ArrayList<Double>>> train_test = train_test_split(small_data, small);
        ArrayList<ArrayList<Double>> train = train_test.get(0);
        ArrayList<ArrayList<Double>> test = train_test.get(1);

        long startTime = System.nanoTime();
        HashMap<String, Object> tree = buildTree(train, 1, 1);

        ArrayList<Double> predicted = predict(tree, test); // for checking
        ArrayList<Double> actual = last_column(test);

        System.out.println("Accuracy: ");
        System.out.println(accuracy_metrics(actual, predicted));
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 );



        // run through the sample test
        System.out.println("Testing with Spotify data");

        // ArrayList<ArrayList<Double>> testData = new ArrayList<ArrayList<Double>>();
        // // acousticness,danceability,duration_ms,energy,explicit,instrumentalness,key,liveness,loudness,mode,popularity,speechiness,tempo,valence,year
        // // BTS Dynamite - 0t1kP63rueHleOhQkYSXFY
        // ArrayList<Double> s1 = new ArrayList<>(Arrays.asList(0.0112, 0.746, 199054.0, 0.765, 0.0, 0.0, 6.0, 0.0936, -4.41, 0.0, 1.0, 0.0993, 114.044, 0.737, 2020.0));
        // // BlackPink How you like that - 6y6k14YN6MbKpGIv1Rk00Q
        // ArrayList<Double> s2 = new ArrayList<>(Arrays.asList(0.0694, 0.828, 181264.0, 0.782, 0.0, 0.0000341, 11.0, 0.0544, -4.014, 1.0, 1.0, 0.0918, 130.013, 0.351, 2020.0));
        // // NCT Make a wish
        // ArrayList<Double> s3 = new ArrayList<>(Arrays.asList(0.0632, 0.732, 229400.0, 0.795, 0.0, 0.0, 8.0, 0.0569, -2.263, 1.0, 1.0, 0.1, 100.954, 0.693, 2020.0));
        // // Ariana Grande Position
        // ArrayList<Double> s4 = new ArrayList<>(Arrays.asList(0.468, 0.737, 172325.0, 0.802, 1.0, 0.0, 0.0, 0.0931, -4.771, 1.0, 1.0, 0.0878, 144.015, 0.682, 2020.0));

        // testData.add(s1);
        // testData.add(s2);
        // testData.add(s3);
        // testData.add(s4);

        ArrayList<ArrayList<Double>> testData = DataTransformation.getData("/D:/Data Structure and Algo/DSA/data/spotify.csv");

        startTime = System.nanoTime();
        predicted = predict(tree, testData); // for checking
        actual = last_column(testData);

        System.out.println("Accuracy: ");
        System.out.println(accuracy_metrics(actual, predicted));
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000 );

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