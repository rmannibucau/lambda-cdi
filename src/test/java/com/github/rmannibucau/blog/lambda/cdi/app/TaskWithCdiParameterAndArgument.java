package com.github.rmannibucau.blog.lambda.cdi.app;

@FunctionalInterface
public interface TaskWithCdiParameterAndArgument<T, A> {
    void work(T cdiBean, A arg);
}
