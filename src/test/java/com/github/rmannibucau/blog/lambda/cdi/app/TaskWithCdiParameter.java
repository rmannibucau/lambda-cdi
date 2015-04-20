package com.github.rmannibucau.blog.lambda.cdi.app;

@FunctionalInterface
public interface TaskWithCdiParameter<T> {
    void work(T cdiBean);
}
