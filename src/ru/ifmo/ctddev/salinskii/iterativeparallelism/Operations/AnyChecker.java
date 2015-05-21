package ru.ifmo.ctddev.salinskii.iterativeparallelism.Operations;

import java.util.List;
import java.util.function.Predicate;

/**
 * Operation of checking <tt>list</tt> for contains any element that apply the specified predicate.
 * Created by Alimantu on 18.05.2015.
 * @author Alexander Salinskii
 */
public class AnyChecker<T> implements ParallelOperations<T> {

    private List<? extends T> list;
    private Predicate<? super T> predicate;
    private ListHandler<T> listHandler;

    public AnyChecker(Predicate<? super T> predicate){
        this.predicate = predicate;
    }

    @Override
    public void evaluate() {
        for(T elem: list){
            if(listHandler.getPredicateInd()){
                break;
            } else if(predicate.test(elem)){
                listHandler.tryPredicateAny(true);
            }
        }
    }

    @Override
    public void setList(List<? extends T> list, int shift, ListHandler<T> listHandler) {
        this.list = list;
        this.listHandler = listHandler;
    }
}
