package ru.ifmo.ctddev.salinskii.iterativeparallelism.Operations;

import java.util.List;

/**
 * Interface for the future realisations of the parallel operations for the {@link ru.ifmo.ctddev.salinskii.iterativeparallelism.Threads}
 * Created by Alimantu on 18.05.2015.
 * @author Alexander Salinskii
 */
public interface ParallelOperations<T> {

    /**
     * Process of evaluating our operation on the <tt>list</tt> writing the final result to the <tt>listHandler</tt>
     */
    public void evaluate();

    /**
     * Simple setting the starting values of the local variables
     * @param list the list for the computing our operation
     * @param shift the number of the first element of the <tt>list</tt> in the original list.
     * @param listHandler the variable uses for storing the result of the computing.
     * @see ru.ifmo.ctddev.salinskii.iterativeparallelism.Operations.ListHandler
     */
    public void setList(List<? extends T> list, int shift, ListHandler<T> listHandler);

}
