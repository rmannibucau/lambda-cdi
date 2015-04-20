package com.github.rmannibucau.blog.lambda.cdi;

import com.github.rmannibucau.blog.lambda.cdi.internal.AnnotatedMethodImpl;
import com.github.rmannibucau.blog.lambda.cdi.internal.Parameters;
import sun.reflect.ConstantPool;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

public final class Lambdas {
    private static final Method GET_CONSTANT_POOL;
    private static final Method GET_ARGUMENTS_TYPE;
    private static final Method GET_CLASS_NAME;

    static {
        try {
            Class<?> TYPE = ClassLoader.getSystemClassLoader().loadClass("jdk.internal.org.objectweb.asm.Type");
            GET_CONSTANT_POOL = Class.class.getDeclaredMethod("getConstantPool");
            GET_ARGUMENTS_TYPE = TYPE.getMethod("getArgumentTypes", String.class);
            GET_CLASS_NAME = TYPE.getMethod("getClassName");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        if (!GET_CONSTANT_POOL.isAccessible()) {
            GET_CONSTANT_POOL.setAccessible(true);
        }
    }

    public static void invokeLambda(Object lambda, Object...args) {
        Class<?> lambdaClass = lambda.getClass();
        for (Method method : lambdaClass.getMethods()) {
            Class<?> declaringClass = method.getDeclaringClass();
            if (declaringClass == Object.class) {
                continue;
            }
            if (declaringClass == Serializable.class) {
                continue;
            }
            if (method.isDefault()) {
                continue;
            }

            Type[] types;
            if (lambdaClass.getName().contains("$$Lambda")) {
                types = methodTypeParameters(lambda);
            } else {
                types = method.getGenericParameterTypes();
            }
            try (Parameters params = createParameters(types.length - (args == null ? 0 : args.length), method, types, args)) {
                try {
                    method.invoke(lambda, params.getInstances().toArray(new Object[params.getInstances().size()]));
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return;
        }
        throw new IllegalArgumentException(lambda + " not a lambda");
    }

    private static Parameters createParameters(int cdiBeanNumber, Method method, java.lang.reflect.Type[] types, Object[] args) {
        Parameters parameters = new Parameters();
        try {
            if (cdiBeanNumber > 0) {
                BeanManager bm = CDI.current().getBeanManager();

                AnnotatedMethod annotatedMethod = new AnnotatedMethodImpl<>(bm.createAnnotatedType(method.getDeclaringClass()), method, cdiBeanNumber, types);
                List<AnnotatedParameter<?>> annotatedMethodParameters = annotatedMethod.getParameters();

                for (int i = 0; i < cdiBeanNumber; i++) {
                    CreationalContext<?> creational = bm.createCreationalContext(null);
                    // Object instance = bm.getInjectableReference(new MethodParamInjectionPoint(types[i], new Annotation[0], i, bm), creational);
                    Object instance = bm.getInjectableReference(bm.createInjectionPoint(annotatedMethodParameters.get(i)), creational);
                    parameters.addInstance(instance, creational);
                }
            }
            for (int i = 0; i < types.length - cdiBeanNumber; i++) {
                parameters.addInstance(args[i], null);
            }
            return parameters;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private  static java.lang.reflect.Type[] methodTypeParameters(Object lambdaInstance) {
        try {
            ConstantPool pool = ConstantPool.class.cast(GET_CONSTANT_POOL.invoke(lambdaInstance.getClass()));
            String[] methodRef = pool.getMemberRefInfoAt(pool.getSize() - 2);

            // Type[] types = jdk.internal.org.objectweb.asm.Type.getArgumentTypes(methodRef[2]);
            Object[] argTypes = (Object[]) GET_ARGUMENTS_TYPE.invoke(null, methodRef[2]);

            Collection<java.lang.reflect.Type> types = new ArrayList<>(argTypes.length);
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            for (Object argType : argTypes) {
                Class<?> clazz = loader.loadClass(String.valueOf(GET_CLASS_NAME.invoke(argType)));
                types.add(clazz);
            }
            return types.toArray(new java.lang.reflect.Type[types.size()]);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Lambdas() {
        // no-op
    }
}
