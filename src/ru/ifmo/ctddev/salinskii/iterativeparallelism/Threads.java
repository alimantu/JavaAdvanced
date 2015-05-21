package ru.ifmo.ctddev.salinskii.iterativeparallelism;

import ru.ifmo.ctddev.salinskii.iterativeparallelism.Operations.ListHandler;
import ru.ifmo.ctddev.salinskii.iterativeparallelism.Operations.ParallelOperations;

import java.util.List;

/**
 * Class that creates new thread and evaluate assigned {@link ru.ifmo.ctddev.salinskii.iterativeparallelism.Operations.ParallelOperations} on it.
 * Implements <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html">Runnable</a>
 * Created by Alimantu on 18.05.2015.
 * @author Alexander Salinskii
 */
public class Threads implements Runnable{
    private ParallelOperations operation;
    Thread thread;

    /**
     * Constructor, simply assigns the local <tt>operation</tt> and set the internal values of it to the assigned <tt>list</tt>, <tt>shift</tt> and <tt>listHandler</tt>, than start the new thread to evaluate the result of the operation.
     * @param operation the operation to evaluate
     * @param list the part of list for running <tt>operation</tt>
     * @param shift the number of the first element of the <tt>list</tt> in the original <tt>List</tt>
     * @param listHandler the class that contains the original <tt>list</tt> and the result of multi-threading operations
     * @param <T> the type of the elements in the <tt>list</tt>
     */
    public <T> Threads(ParallelOperations operation, List<? extends T> list, int shift, ListHandler<T> listHandler){
        this.operation = operation;
        this.operation.setList(list, shift, listHandler);
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Simple run of our thread.
     */
    @Override
    public void run() {
        operation.evaluate();
    }
}
