package ru.ifmo.ctddev.salinskii.iterativeparallelism.Operations;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Operation of finding minimum element of the <tt>list</tt> with specified <tt>comparator</tt>
 * Created by Alimantu on 18.05.2015.
 * @author Alexander Salinskii
 */
public class Minimum<T> implements ParallelOperations<T> {
    private List<? extends T> list;
    private Comparator<? super T> comparator;
    private int shift;
    private ListHandler<T> listHandler;

    public Minimum(Comparator<? super T> comparator){
        this.comparator = comparator;
    }

    @Override
    public void evaluate() {
        T subResult = Collections.min(list, comparator);
        int subIndex = list.indexOf(subResult) + shift;
        if(comparator.compare(subResult, listHandler.getResult()) < 0 || (comparator.compare(subResult, listHandler.getResult()) == 0  && subIndex < listHandler.getResultIndex())){
            listHandler.tryMinResult(subResult, subIndex);
        }
    }

    @Override
    public void setList(List<? extends T> list, int shift, ListHandler<T> listHandler) {
        this.list = list;
        this.shift = shift;
        this.listHandler = listHandler;
    }

}
