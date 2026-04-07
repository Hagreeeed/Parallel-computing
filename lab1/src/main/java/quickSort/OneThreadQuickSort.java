package quickSort;

public class OneThreadQuickSort extends QuickSort{


    public OneThreadQuickSort(int[] arr, int left, int right) {
        super(arr, left, right);
    }

    @Override
    protected void compute() {
        quickSort(left, right);
    }

    private void quickSort(int left, int right) {
        if(left >= right) {
            return;
        }
        int pivotIndex = partition(arr, left, right);

        quickSort(left, pivotIndex - 1);
        quickSort(pivotIndex, right);

    }
}
