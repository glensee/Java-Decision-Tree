import java.util.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HardyBuildTree {
    public void split(HashMap<String, Object> node, int maxDepth, int minSize, int depth) {
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

    public HashMap<String, Object> buildTree(ArrayList<ArrayList<Double>> train, int maxDepth, int minSize) {
        HashMap<String, Object> root = getSplit(train);
        split(root, maxDepth, minSize, 1);
        return root;
    }
a
    public Double toTerminal(ArrayList<ArrayList<Double>> group) {

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
}