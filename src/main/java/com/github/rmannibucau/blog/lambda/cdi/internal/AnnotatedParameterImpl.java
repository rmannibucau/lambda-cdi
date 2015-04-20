package com.github.rmannibucau.blog.lambda.cdi.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;

public class AnnotatedParameterImpl<T> implements AnnotatedParameter<T> {
    private final AnnotatedMethod<T> method;
    private final int position;
    private final Set<Type> types = new HashSet<>();
    private final Set<Annotation> annotations;
    private final Type baseType;

    public AnnotatedParameterImpl(AnnotatedMethod<T> method, int position, Type baseType, Set<Annotation> annotations) {
        this.method = method;
        this.baseType = baseType;
        this.position = position;

        this.types.add(baseType);
        this.types.add(Object.class);

        this.annotations = new HashSet<>(annotations);
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public AnnotatedCallable<T> getDeclaringCallable() {
        return method;
    }

    @Override
    public Type getBaseType() {
        return baseType;
    }

    @Override
    public Set<Type> getTypeClosure() {
        return types;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for (Annotation a : annotations) {
            if (a.annotationType().getName().equals(annotationType.getName())) {
                return (T) a;
            }
        }
        return null;
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return getAnnotation(annotationType) != null;
    }
}
