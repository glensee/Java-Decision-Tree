import java.util.*;

public class QuickSort {
    private int index;

    public QuickSort(int index) {
        this.index = index;
    }


    
    /* This function takes last element as pivot, 
       places the pivot element at its correct 
       position in sorted array, and places all 
       smaller (smaller than pivot) to left of 
       pivot and all greater elements to right 
       of pivot */
    public int partition(ArrayList<ArrayList<Double>> arr, int low, int high) 
    { 
        Double pivot = arr.get(high).get(index);
        int i = (low-1); // index of smaller element 
        for (int j=low; j<high; j++) 
        { 
            // If current element is smaller than the pivot 
            if (arr.get(j).get(index) < pivot) 
            { 
                i++; 
  
                // swap arraylist at i and j
                ArrayList<Double> row_i = arr.get(i);
                ArrayList<Double> row_j = arr.set(j, row_i);
                arr.set(i, row_j);

            } 
        } 
  
        // swap arr[i+1] and arr[high] (or pivot) 
        ArrayList<Double> row_i = arr.get(i+1);
        ArrayList<Double> row_high = arr.set(high, row_i);
        arr.set(i+1, row_high);

  
        return i+1; 
    } 
  
  
    /* The main function that implements QuickSort() 
      arr[] --> Array to be sorted, 
      low  --> Starting index, 
      high  --> Ending index */
    public void sort(ArrayList<ArrayList<Double>> arr, int low, int high) 
    { 
        if (low < high) 
        { 
            /* pi is partitioning index, arr[pi] is  
              now at right place */
            int pi = partition(arr, low, high); 
  
            // Recursively sort elements before 
            // partition and after partition 
            sort(arr, low, pi-1); 
            sort(arr, pi+1, high); 
        } 
    } 
}
    

