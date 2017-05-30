package pl.edu.agh.mpso.utils;
import java.util.*;

/**
 * Created by Roksana on 2017-05-30.
 */

public class ListTranspose {
//    public static void main(String[] args) {
//        //   [A, 2, 3, 4],
//        //   [B, 3, 5, 7]]"
//        table = transpose(table);
//        System.out.println(table); //  [[Title, A, B],
//        //   [Data1, 2, 3],
//        //   [Data2, 3, 5],
//        //   [Data3, 4, 7]]
//    }
    public static <T> List<List<T>> transpose(List<List<T>> table) {
        List<List<T>> ret = new ArrayList<List<T>>();
        final int N = table.get(0).size();
        for (int i = 0; i < N; i++) {
            List<T> col = new ArrayList<T>();
            for (List<T> row : table) {
                col.add(row.get(i));
            }
            ret.add(col);
        }
        return ret;
    }
}