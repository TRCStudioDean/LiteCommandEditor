package studio.trc.bukkit.litecommandeditor.module.tool;

import java.util.ArrayList;
import java.util.List;

public abstract class Sortable 
{
    public abstract int compareTo(Sortable sortTarget);
    
    public static <T extends Sortable> List<T> sortArray(List<T> array) {
        List<T> sortedArray = new ArrayList(array);
        for (int i = 0;i < sortedArray.size();i++) {
            for (int j = 0; j < sortedArray.size() - i - 1; j++) {
                if (sortedArray.get(j) != null && sortedArray.get(j + 1) != null && sortedArray.get(j).compareTo(sortedArray.get(j + 1)) < 0) {
                    T temp = sortedArray.get(j);
                    sortedArray.set(j, sortedArray.get(j + 1));
                    sortedArray.set(j + 1, temp);
                }
            }
        }
        return sortedArray;
    }
}
