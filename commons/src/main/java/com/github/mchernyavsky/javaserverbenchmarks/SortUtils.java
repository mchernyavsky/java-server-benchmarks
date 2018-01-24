package com.github.mchernyavsky.javaserverbenchmarks;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SortUtils {

    public <T extends Comparable<? super T>> void insertionSort(@NotNull final List<T> data) {
        for (int i = 1; i < data.size(); i++) {
            for (int j = i - 1; j >= 0 && data.get(j).compareTo(data.get(j + 1)) > 0; j--) {
                Collections.swap(data, i, j);
            }
        }
    }

    public <T extends Comparable<? super T>> void selectionSort(@NotNull final List<T> data) {
        for (int i = 0; i < data.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < data.size(); j++) {
                if (data.get(j).compareTo(data.get(minIndex)) < 0) {
                    Collections.swap(data, j, minIndex);
                    minIndex = j;
                }
            }
        }
    }

    public <T extends Comparable<? super T>> void bubbleSort(@NotNull final List<T> data) {
        for (int i = 1; i < data.size() - 1; i++) {
            for (int j = 1; j < data.size() - i - 1; j++) {
                if (data.get(j).compareTo(data.get(j + 1)) > 0) {
                    Collections.swap(data, j, j + 1);
                }
            }
        }
    }
}
