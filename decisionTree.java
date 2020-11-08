import java.util.*;

public class decisionTree {
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
            for (ArrayList<Double> row : dataset) {

                // finding best split point manually
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


    public  static Double predict(HashMap<String, Object> tree, ArrayList<Double> row) {
        Integer index = (Integer) tree.get("index");
        Double value = (Double) tree.get("value");



        if (row.get(index) <  value) {
            if (tree.get("left") instanceof HashMap ) {

                return predict((HashMap<String, Object>) tree.get("left"), row);
            } else {
                return (Double) tree.get("left");
            }
        } else {
            if (tree.get("right") instanceof HashMap ) {
                return predict((HashMap<String, Object>) tree.get("right"), row);
            } else {
                return  (Double) tree.get("right");
            }
        }

    }


    public  static ArrayList<Double> decision_tree(ArrayList<ArrayList<Double>> train, ArrayList<ArrayList<Double>> test, int max_depth, int min_size) {
        ArrayList<Double> predictions = new ArrayList<>();
        HashMap<String, Object> tree = buildTree(train, max_depth, min_size);

        for (ArrayList<Double> row : test) {
            Double prediction = predict(tree, row);
            predictions.add(prediction);
        }

        return predictions;
    }


    public  static Double accuracy_metrics(ArrayList<Double> actual, ArrayList<Double> predicted) {
        Double count = 0.0 ;

        for (int i = 0 ; i < actual.size(); i++) {
            if (actual.get(i)== predicted.get(i)) {
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

    private  static ArrayList<ArrayList<Double>> dataSet;
    public static void main(String[] args) {
        dataSet = new ArrayList<ArrayList<Double>>();
        Double test_size = 0.25;


        //insertion of the arraylist
        // doubleArray(2.771244718,1.784783929,0.0);
        // doubleArray(1.728571309,1.169761413,0.0);
        // doubleArray(3.678319846,2.81281357,0.0);
        // doubleArray(3.961043357,2.61995032,0.0);
        // doubleArray(2.999208922,2.209014212,0.0);
        // doubleArray(7.497545867,3.162953546,1.0);
        // doubleArray(9.00220326,3.339047188,1.0);
        // doubleArray(7.444542326,0.476683375,1.0);
        // doubleArray(10.12493903,3.234550982,1.0);
        // doubleArray(6.642287351,3.319983761,1.0);

        // ArrayList<ArrayList<ArrayList<Double>>> train_test = train_test_split(dataSet, test_size);
        // ArrayList<ArrayList<Double>> train = train_test.get(0);
        // ArrayList<ArrayList<Double>> test = train_test.get(1);

        // System.out.println(train);
        // System.out.println(test);
        // HashMap<String, Object> tree = buildTree(dataSet,10,1);
        // ArrayList<Double> predictions = decision_tree(dataSet,dataSet,10,1); // for checking

        // System.out.println(tree);
        // System.out.println(predictions); // for checking

        // System.out.println(dataSet);


        // Testing using data
        ArrayList<ArrayList<Double>> data = DataTransformation.getData("/Users/sheryll/Desktop/SMU/Y2SEM1/CS201/Project_/DSA/data/data_updated.csv");
        // System.out.println(data.get(1));
        // System.out.println(data.get(100));

        // System.out.println(data.size());
        // System.out.println(data.get(0).size());

        // HashMap<String, Object> tree2 = buildTree(data,15,10);
        // System.out.println(tree2.get(1));

        // ArrayList<ArrayList<ArrayList<Double>>> train_test = train_test_split(data, test_size);
        // ArrayList<ArrayList<Double>> train = train_test.get(0);
        // ArrayList<ArrayList<Double>> test = train_test.get(1);

        // ArrayList<Double> predicted = decision_tree(train,test,15,10); // for checking
        // ArrayList<Double> actual = new ArrayList<>();
        // for (ArrayList<Double> row: test) { // refactor to get last column
        //     actual.add(row.get(row.size() - 1));
        // }

        // data = (ArrayList<ArrayList<Double>>) data.subList(0, 50);



        ArrayList<ArrayList<Double>> newdata = DataTransformation.getData("/Users/sheryll/Desktop/SMU/Y2SEM1/CS201/Project_/DSA/data/small_dataset.csv");
        long startTime = System.nanoTime();

        ArrayList<Double> predicted = decision_tree(newdata,newdata,100,1); // for checking
        ArrayList<Double> actual = last_column(newdata);

        System.out.println(accuracy_metrics(actual, predicted));
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000);

    }


    // to add the double values into the arraylist
    private static void doubleArray(Double d, Double d1, Double i){
        ArrayList<Double> doubleArray = new ArrayList<Double>();
        doubleArray.add(d);
        doubleArray.add(d1);
        doubleArray.add(i);

        dataSet.add(doubleArray);
    }
}