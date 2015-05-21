package ru.ifmo.ctddev.salinskii.iterativeparallelism.Operations;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * The class uses for store the original list during parallel operations, and the final results of it.
 * Created by Alimantu on 19.05.2015.
 * @author Alexander Salisnkii
 */
public class ListHandler<T> {
    private List<? extends T> list;
    private T result;
    private int resultIndex;
    private boolean predicateInd;
    private Comparator<? super T> comparator;
    private Predicate<? super T> predicate;

    /**
     * Simple constructor of the class, assigning the local variables to the starting values.
     * @param list the original list, assigns to the local <tt>list</tt> and setting the <tt>result</tt> to the first element of it, <tt>resultIndex</tt> to 0(if <tt>list</tt> isn't empty)
     * @param comparator the comparator for the correct comparing elements of the <tt>list</tt>
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html">Comparator</a>
     */
    public ListHandler(List<? extends T> list, Comparator<? super T> comparator){
        this.comparator = comparator;
        this.list = list;
        if(!list.isEmpty()){
            result = list.get(0);
            resultIndex = 0;
        }
    }

    /**
     * Constructor that assigns the starting values to the local variables if we are using <tt>Predicate</tt>.
     * @param list the original list, assigns to the local <tt>list</tt>.
     * @param predicate the predicate that used for the <tt>list</tt> verifying.
     * @param predicateInd the starting value of the result of the check.
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html">Presicate</a>
     */
    public ListHandler(List<? extends T> list, Predicate<? super T> predicate, boolean predicateInd){
        this.predicate = predicate;
        this.list = list;
        this.predicateInd = predicateInd;
    }

    /**
     * Rsturn the element of the <tt>list</tt> with specified <tt>index</tt>.
     * @param index the index of the searching element.
     * @return element of the <tt>list</tt> on the <tt>index</tt> position.
     */
    public T get(int index){
        return list.get(index);
    }

    /**
     * @return value of the <tt>result</tt>
     */
    public T getResult(){
        return result;
    }

    /**
     * @return the index of the <tt>result</tt> in the <tt>list</tt>
     */
    public int getResultIndex(){
        return resultIndex;
    }

    /**
     * @return the value of the <tt>predicateInd</tt> that indicates the result of the <tt>list</tt> check using <tt>predicate</tt>
     */
    public boolean getPredicateInd(){
        return predicateInd;
    }

    /**
     * Assigning the <tt>predicateInd</tt> to the logical and of it and <tt>newPredicateAll</tt>, uses synchronization for multi-threading using.
     * @param newPredicateAll the result of checking <tt>predicate</tt> on the some part of the <tt>list</tt>
     */
    public synchronized void tryPredicateAll(boolean newPredicateAll){
        predicateInd &= newPredicateAll;
    }

    /**
     * Assigning the <tt>predicateInd</tt> to the logical or of it and <tt>newPredicateAll</tt>, uses synchronization for multi-threading using.
     * @param newPredicateAny the result of checking <tt>predicate</tt> on the some part of the <tt>list</tt>
     */
    public synchronized void tryPredicateAny(boolean newPredicateAny){
        predicateInd |= newPredicateAny;
    }

    /**
     * Assigning the <tt>newResult</tt> of the some part of the list search for maximum if it is greater than current <tt>result</tt> or <tt>newIndex</tt> is less than current <tt>index</tt>.
     * @param newResult the maximum of the some part of the <tt>list</tt>
     * @param newIndex the index of the <tt>newResult</tt> in the <tt>list</tt>
     */
    public synchronized void tryMaxResult(T newResult, int newIndex){
        if((comparator.compare(newResult, result) > 0) || (comparator.compare(newResult, result) == 0  && newIndex < resultIndex)){
            result = newResult;
            resultIndex = newIndex;
        }
    }

     /**
     * Assigning the <tt>newResult</tt> of the some part of the list search for minimum if it is less than current <tt>result</tt> or <tt>newIndex</tt> is less than current <tt>index</tt>.
     * @param newResult the minimum of the some part of the <tt>list</tt>
     * @param newIndex the index of the <tt>newResult</tt> in the <tt>list</tt>
     */
    public synchronized void tryMinResult(T newResult, int newIndex){
        if((comparator.compare(newResult, result) < 0) || (comparator.compare(newResult, result) == 0  && newIndex < resultIndex)){
            result = newResult;
            resultIndex = newIndex;
        }
    }
}
