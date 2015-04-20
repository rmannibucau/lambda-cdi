package com.github.rmannibucau.blog.lambda.cdi.app;

import javax.enterprise.context.Dependent;

@Dependent
public class ABean2 {
    public void call() {
        Stack.list.add(ABean2.class.getName());
    }
}
