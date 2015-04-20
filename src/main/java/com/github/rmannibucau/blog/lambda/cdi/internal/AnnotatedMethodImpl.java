package com.github.rmannibucau.blog.lambda.cdi.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

import static java.util.Arrays.asList;

public class AnnotatedMethodImpl<T> implements AnnotatedMethod<T> {
    private final AnnotatedType<T> annotatedType;
    private final Method method;
    private final Set<Annotation> annotations;
    private final List<AnnotatedParameter<T>> parameters;

    public AnnotatedMethodImpl(AnnotatedType<T> annotatedType, Method method, int paramCount, Type[] paramTypes) {
        this.annotatedType = annotatedType;
        this.method = method;
        this.annotations = new HashSet<>(asList(method.getAnnotations()));
        this.parameters = new LinkedList<>();

        for (int i = 0; i < paramCount; i++) {
            this.parameters.add(new AnnotatedParameterImpl<>(this, i, paramTypes[i], new HashSet<>(asList(method.getParameterAnnotations()[i]))));
        }
    }

    @Override
    public List<AnnotatedParameter<T>> getParameters() {
        return parameters;
    }

    @Override
    public Method getJavaMember() {
        return method;
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(method.getModifiers());
    }

    @Override
    public AnnotatedType<T> getDeclaringType() {
        return annotatedType;
    }

    @Override
    public Type getBaseType() {
        return method.getDeclaringClass();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return Collections.singleton(getBaseType());
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return (T) annotations.stream().filter(a -> a.annotationType() == annotationType).findFirst().orElse(null);
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return annotations.stream().filter(a -> a.annotationType() == annotationType).findFirst().isPresent();
    }
}
