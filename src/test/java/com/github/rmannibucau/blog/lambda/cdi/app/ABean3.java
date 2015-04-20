package com.github.rmannibucau.blog.lambda.cdi.app;

import javax.enterprise.context.Dependent;

@Dependent
public class ABean3 {
    public void call(String arg) {
        Stack.list.add(ABean3.class.getName() + '-' + arg);
    }
}
