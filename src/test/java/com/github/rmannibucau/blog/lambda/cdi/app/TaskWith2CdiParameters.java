package com.github.rmannibucau.blog.lambda.cdi.app;

@FunctionalInterface
public interface TaskWith2CdiParameters<T1, T2> {
    void work(T1 param1, T2 param2);
}
