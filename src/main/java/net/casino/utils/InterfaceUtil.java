package net.casino.utils;

public class InterfaceUtil {

    public static Class<?> getInterfaceByClass(Class<?> originalClass, Class<?> interfaceClazz) {
        for (Class<?> interfaces : originalClass.getInterfaces()) {
            if (interfaces.getClass() == interfaceClazz.getClass()) {
                return interfaces;
            }
        }
        return null;
    }
}
