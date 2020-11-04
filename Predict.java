import java.util.*;

public class Predict {

    public Integer predict(HashMap<String, Object> map, Integer[] row) {
        Integer index = (Integer) map.get("index");
        Double value = (Double) map.get("value");
         


        if (row[index] <  value) {
            if (map.get("left") instanceof HashMap ) {
                
                return predict((HashMap<String, Object>) map.get("left"), row);
            } else {
                return (Integer) map.get("left");
            }
        } else {
            if (map.get("right") instanceof HashMap ) {
                return predict((HashMap<String, Object>) map.get("right"), row);
            } else {
                return  (Integer) map.get("right");
            }
        }

    }

    public static void main(String[] args) {

    }

}