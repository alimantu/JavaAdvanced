package ru.ifmo.ctddev.salinskii.iterativeparallelism.Operations;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Operation finding max element of the <tt>list</tt> with specified comparator.
 * Created by Alimantu on 18.05.2015.
 * @author Alexander Salinskii
 */
public class Maximum<T> implements ParallelOperations<T> {
    private List<? extends T> list;
    private Comparator<? super T> comparator;
    private ListHandler<T> listHandler;
    private int shift;

    public Maximum(Comparator<? super T> comparator){
        this.comparator = comparator;
    }

    @Override
    public void evaluate() {
        T subResult = Collections.max(list, comparator);
        int subIndex = list.indexOf(subResult) + shift;
        if(comparator.compare(subResult, listHandler.getResult()) > 0 || (comparator.compare(subResult, listHandler.getResult()) == 0  && subIndex < listHandler.getResultIndex())) {
            listHandler.tryMaxResult(subResult, subIndex);
        }
    }

    @Override
    public void setList(List<? extends T> list, int shift, ListHandler<T> listHandler) {
        this.list = list;
        this.shift = shift;
        this.listHandler = listHandler;
    }

}
