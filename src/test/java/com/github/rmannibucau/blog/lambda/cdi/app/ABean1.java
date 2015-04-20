package com.github.rmannibucau.blog.lambda.cdi.app;

import javax.enterprise.context.Dependent;

@Dependent
public class ABean1 {
    public void call() {
        Stack.list.add(ABean1.class.getName());
    }
}
