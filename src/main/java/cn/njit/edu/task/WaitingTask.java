package cn.njit.edu.task;

import javafx.concurrent.Task;

import java.util.function.Consumer;

public class WaitingTask extends Task {

    private final Consumer consumer;

    public WaitingTask(Consumer<Task> consumer){

        this.consumer = consumer;
    }



    @Override
    protected Object call() throws Exception {
        this.consumer.accept(this);
        return null;
    }
}
