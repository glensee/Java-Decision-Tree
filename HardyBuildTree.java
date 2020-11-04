import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jdk.nashorn.internal.parser.TokenStream;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

public class HardyBuildTree {
    public void split(HashMap<String, Object> node, double maxDepth, int minSize, double depth) {
        HashMap<String, Object> left = (HashMap<String, Object>) node.get("groups");
        HashMap<String, Object> right = (HashMap<String, Object>) node.get("groups");
        node.remove("groups");
        // Check for a no split
        if (left == null || right == null) {
            node.replace("right", toTerminal(left.putAll(right)));
            node.replace("left", node.get("right"));
            return;
        }

        // Check for max depth
        if (depth >= maxDepth) {
            node.replace("left", toTerminal(left));
            node.replace("right", toTerminal(right));
            return;
        }

        // Process left child
        if (left.size() <= minSize) { 
            node.replace("left", toTerminal(left));
        } else {
            node.replace("left", getSplit(left));
            split((HashMap<String, Object>)node.get("left"), maxDepth, minSize, depth + 1);
        }   

        // Process right child
        if (right.size() <= minSize) {
            node.replace("right", toTerminal(right));
        } else {
            node.replace("right", getSplit(right));
            split((HashMap<String, Object>)node.get("right"), maxDepth, minSize, depth + 1);
        }

    }

    public HashMap<String, Object> buildTree(ArrayList<ArrayList<Integer>> train, double maxDepth, int minSize) {
        HashMap<String, Object> root = getSplit(train);
        split(root, maxDepth, minSize, 1);
        return root;
    }

    public Object toTerminal(HashMap<String, Object> group) {
        ArrayList<Object> outcomes = new ArrayList<>();
        for (String row : group.keySet()) {
            Object object = ((TokenStream) group.get(row)).get();
            outcomes.add(object);
        } 
        
        int max = 0;
        for (Object o : outcomes) {
            if ((int)o > max) {
                max = (int)o;
            }
        }

        return max;
    }
}