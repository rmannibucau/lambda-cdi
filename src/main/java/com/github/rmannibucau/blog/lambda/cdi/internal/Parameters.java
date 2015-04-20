package com.github.rmannibucau.blog.lambda.cdi.internal;

import java.util.Collection;
import java.util.LinkedList;
import javax.enterprise.context.spi.CreationalContext;

public class Parameters implements AutoCloseable {
    private final Collection<Object> instances = new LinkedList<>();
    private final Collection<CreationalContext<?>> creationalContexts = new LinkedList<>();

    public Collection<Object> getInstances() {
        return instances;
    }

    public void addInstance(Object param, CreationalContext<?> cc) {
        instances.add(param);
        if (cc != null) {
            creationalContexts.add(cc);
        }
    }

    @Override
    public void close() {
        creationalContexts.stream().forEach(CreationalContext::release);
    }
}
