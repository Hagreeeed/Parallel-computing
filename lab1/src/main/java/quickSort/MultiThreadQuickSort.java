package quickSort;

public class MultiThreadQuickSort extends QuickSort{

    final private int THRESHOLD = 1000;

    public MultiThreadQuickSort(int[] arr, int left, int right) {
        super(arr, left, right);
    }

    @Override
    protected void compute() {

        if((right - left) < THRESHOLD){
            sort(left, right);
            return;
        }

        int pivotIndex = partition(arr, left, right);

        MultiThreadQuickSort leftArray = new MultiThreadQuickSort(arr, left, pivotIndex -1);
        MultiThreadQuickSort rightArray = new MultiThreadQuickSort(arr, pivotIndex, right);

        invokeAll(leftArray, rightArray);

    }

    private void sort(int left, int right){
        if(left >= right) {
            return;
        }
        int pivotIndex = partition(arr, left, right);
        sort(left, pivotIndex-1);
        sort(pivotIndex, right);

    }
}

