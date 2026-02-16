package quickSort;

import java.util.concurrent.RecursiveAction;

public abstract class QuickSort extends RecursiveAction {


    protected int[] arr;
    protected int left;
    protected int right;

    public QuickSort(int[] arr, int left, int right){
        this.arr = arr;
        this.left = left;
        this.right = right;
    }


    protected static int partition(int[] arr, int from, int to){

            int middle = from + (to - from) / 2;
            int pivot = arr[middle];

            int rightIndex = to, leftIndex = from;
            while (leftIndex <= rightIndex){

                while (arr[leftIndex] < pivot){
                    leftIndex++;
                }
                while (arr[rightIndex] > pivot){
                    rightIndex--;
                }

                if (leftIndex <= rightIndex){
                    swap(arr, leftIndex, rightIndex);
                    leftIndex++;
                    rightIndex--;
                }
            }

        return leftIndex;

    }

    private static void swap(int[] arr, int i, int j){
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }


    public static int getElementById(int[] arr, int k){
        return arr[k];
    }

}
