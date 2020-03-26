package gov.anzong.androidnga.common.util;

import java.lang.reflect.Method;

public class ReflectUtils {

    public static void invokeMethod(Class<?> clz, Object obj, String methodName, Class<?>[] parameterTypes, Object[] params) {
        try {
            Method method = clz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            method.invoke(obj, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void invokeMethod(Class<?> clz, String methodName) {
        invokeMethod(clz, null, methodName, null, null);
    }

    public static Object invokeMethodAndGetResult(Class<?> clz, Object obj, String methodName, Class<?>[] parameterTypes, Object[] params) {
        try {
            Method method = clz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object invokeMethodAndGetResult(Class<?> clz, Object obj, String methodName) {
        return invokeMethodAndGetResult(clz, obj, methodName, null, null);
    }

    public static Object invokeMethodAndGetResult(Class<?> clz, String methodName) {
        return invokeMethodAndGetResult(clz, null, methodName, null, null);
    }
}
