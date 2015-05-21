package ru.ifmo.ctddev.salinskii.iterativeparallelism.Operations;

import java.util.List;
import java.util.function.Predicate;

/**
 * Operation that checks all elements of the <tt>list</tt> applying specified predicate
 * Created by Alimantu on 18.05.2015.
 * @author Alexander Salinskii
 */
public class AllChecker<T> implements ParallelOperations<T> {
    private List<? extends T> list;
    private Predicate<? super T> predicate;
    //volatile static boolean result;
    private ListHandler<T> listHandler;

    public AllChecker(Predicate<? super T> predicate){
        this.predicate = predicate;
       // result = true;
    }

    @Override
    public void evaluate() {
        for(T elem: list){
            if(!listHandler.getPredicateInd()){
                break;
            } else if (!predicate.test(elem)){
                listHandler.tryPredicateAll(false);
            }
        }
    }

    @Override
    public void setList(List<? extends T> list, int shift, ListHandler<T> listHandler) {
        this.list = list;
        this.listHandler = listHandler;
    }
}
