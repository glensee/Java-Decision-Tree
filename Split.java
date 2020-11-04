import java.util.*;

public class Split {
    public ArrayList<ArrayList<ArrayList<Double>>> test_split(Integer index, Double value, ArrayList<ArrayList<Double>> dataset) {
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

    public HashMap<String, Object> getSplit(ArrayList<ArrayList<Double>> dataset) {
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

    // public static void main(String[] args) {
    //     Split s = new Split();
    //     ArrayList<ArrayList<Integer>> left = new ArrayList<>();
    //     System.out.println(s.test_split(1,2,left).size());
    // }
}

