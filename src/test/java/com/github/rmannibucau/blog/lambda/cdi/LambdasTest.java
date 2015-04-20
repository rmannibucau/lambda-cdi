package com.github.rmannibucau.blog.lambda.cdi;

import com.github.rmannibucau.blog.lambda.cdi.app.ABean1;
import com.github.rmannibucau.blog.lambda.cdi.app.ABean2;
import com.github.rmannibucau.blog.lambda.cdi.app.ABean3;
import com.github.rmannibucau.blog.lambda.cdi.app.MyFlowBuilder;
import com.github.rmannibucau.blog.lambda.cdi.app.Stack;
import com.github.rmannibucau.blog.lambda.cdi.app.TaskWithCdiParameter;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class LambdasTest {
    @Test
    public void run() {
        WebBeansContext webBeansContext = new WebBeansContext();
        try {
            webBeansContext.getService(ContainerLifecycle.class).startApplication(null);

            final MyFlowBuilder builder = new MyFlowBuilder() {
                @Override
                public void defineFlow() {
                    work((ABean1 bean) -> bean.call());
                    work((ABean1 bean1, ABean2 bean2) -> {
                        bean1.call();
                        bean2.call();
                    });
                    work((ABean3 bean, String arg) -> bean.call(arg), "param");
                    work(new TaskWithCdiParameter<ABean1>() { // java 7 style
                        @Override
                        public void work(ABean1 cdiBean) {
                            cdiBean.call();
                        }
                    });
                }
            };
            builder.defineFlow();
            builder.run();
            assertEquals(asList(
                    "com.github.rmannibucau.blog.lambda.cdi.app.ABean1",
                    "com.github.rmannibucau.blog.lambda.cdi.app.ABean1", "com.github.rmannibucau.blog.lambda.cdi.app.ABean2",
                    "com.github.rmannibucau.blog.lambda.cdi.app.ABean3-param",
                    "com.github.rmannibucau.blog.lambda.cdi.app.ABean1"),
                    Stack.list);
        } finally {
            webBeansContext.getService(ContainerLifecycle.class).stopApplication(null);
        }
    }
}
