import quickSort.MultiThreadQuickSort;
import quickSort.OneThreadQuickSort;
import quickSort.QuickSort;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Зчитуємо параметри
        System.out.print("Введіть розмір масиву (наприклад, 10.000.000): ");
        int n;
        try {
            n = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Помилка. Використовується: 10.000.000");
            n = 10_000_000;
        }

        System.out.print("Введіть k (який по порядку елемент знайти): ");
        int k = 1;
        try {
            k = scanner.nextInt();
            if (k < 1 || k > n) k = 1;
        } catch (Exception e) {
            k = 1;
        }

        System.out.println("\nГенерація масиву на " + n + " елементів...");

        // 2. Генерація даних
        int[] arrSingle = new Random().ints(n, 1, 100000).toArray();
        // Робимо копії для чесного змагання
        int[] arrMulti = Arrays.copyOf(arrSingle, arrSingle.length);
        int[] arrJava = Arrays.copyOf(arrSingle, arrSingle.length);

        System.out.println("Дані згенеровано. Починаємо тест.\n");

        ForkJoinPool pool = new ForkJoinPool();

        // --- 1. SINGLE THREAD ---
        OneThreadQuickSort singleSorter = new OneThreadQuickSort(arrSingle, 0, arrSingle.length - 1);

        long start = System.nanoTime();
        pool.invoke(singleSorter);
        long end = System.nanoTime();

        double singleTime = (end - start) / 1_000_000_000.0;
        System.out.printf("Single Thread: %.4f с\n", singleTime);
        System.out.println("Елемент №" + k + ": " + arrSingle[k-1]);

        // --- 2. MULTI THREAD ---
        MultiThreadQuickSort multiSorter = new MultiThreadQuickSort(arrMulti, 0, arrMulti.length - 1);

        start = System.nanoTime();
        pool.invoke(multiSorter);
        end = System.nanoTime();

        double multiTime = (end - start) / 1_000_000_000.0;
        System.out.printf("Multi Thread:  %.4f с\n", multiTime);
        System.out.println("Елемент №" + k + ": " + arrMulti[k-1]);

        start = System.nanoTime();
        Arrays.parallelSort(arrJava);
        end = System.nanoTime();

        double javaTime = (end - start) / 1e9;
        System.out.printf("Java Native:   %.4f с\n", javaTime);
        System.out.println("Елемент №" + k + ": " + arrJava[k-1]);


        System.out.println("\n------------------------------------------------");
        System.out.printf("Мультипотік швидший за Однопотік у %.2f разів.\n", singleTime / multiTime);

        if (multiTime < javaTime) {
            System.out.println("Мультипотік швидше за Java Native");
        } else {
            System.out.printf("Java Native швидша за Мультипотік у %.2f разів.\n", multiTime / javaTime);
        }
        System.out.println("------------------------------------------------");
    }
}