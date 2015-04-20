package com.github.rmannibucau.blog.lambda.cdi.app;

import com.github.rmannibucau.blog.lambda.cdi.Lambdas;

import java.util.Collection;
import java.util.LinkedList;

public abstract class MyFlowBuilder implements Runnable {
    private final Collection<Runnable> tasks = new LinkedList<>();

    // cdi parameter(s)

    protected <T> void work(final TaskWithCdiParameter<T> task) {
        tasks.add(() -> Lambdas.invokeLambda(task));
    }

    protected <T1, T2> void work(final TaskWith2CdiParameters<T1, T2> task) {
        tasks.add(() -> Lambdas.invokeLambda(task));
    }

    // cdi params + args

    protected <T, A> void work(final TaskWithCdiParameterAndArgument<T, A> task, A arg) {
        tasks.add(() -> Lambdas.invokeLambda(task, arg));
    }

    public abstract void defineFlow();

    @Override
    public void run() {
        tasks.stream().forEach(Runnable::run);
    }
}
