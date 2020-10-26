import java.util.*;

public class Split {
    public ArrayList<ArrayList<ArrayList<Integer>>> test_split(int index,int value, ArrayList<ArrayList<Integer>> dataset) {
        ArrayList<ArrayList<Integer>> left = new ArrayList<>();
        ArrayList<ArrayList<Integer>> right = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Integer>>> result = new ArrayList<>();
        result.add(left);
        result.add(right);

        for (ArrayList<Integer> row : dataset) {
            if (row.get(index) < value) {
                left.add(row);
            } else {
                right.add(row);
            }
        }

        return result;
    }

    public HashMap<String, Object> getSplit(ArrayList<ArrayList<Integer>> dataset) {
        Map<String, Object> result = new HashMap<>();
        Set<Integer> hashSet = new HashSet<>(); 

        for (ArrayList<Integer> row : dataset) {
            hashSet.add(row.get(row.size() - 1));
        }

        ArrayList<Integer> class_values = new ArrayList<>(hashSet);
        int b_index, b_value, b_score;
        b_index = b_value = b_score = 999;
        ArrayList<ArrayList<ArrayList<Integer>>> b_groups = null;

        for (int i = 0; i < dataset.get(0).size(); i++) {
            for (ArrayList<Integer> row : dataset) {
                ArrayList<ArrayList<ArrayList<Integer>>> groups = test_split(i, row.get(i), dataset);
                Integer gini = gini_index(groups, class_values);
                if (gini < b_score) {
                    b_index = i;
                    b_value = row.get(i);
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

    public static void main(String[] args) {
        Split s = new Split();
        ArrayList<ArrayList<Integer>> left = new ArrayList<>();
        System.out.println(s.test_split(1,2,left).size());
    }
}

