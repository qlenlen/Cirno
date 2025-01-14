package nep.timeline.cirno.utils;

import java.lang.reflect.Method;

import nep.timeline.cirno.log.Log;

public class ReflectUtils {
    public static Object[] findParameterTypesOrDefault(Class<?> clazz, String methodName, Object... parameter) {
        try {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();

                    if (parameter.length <= parameterTypes.length) {
                        boolean isCompatible = true;

                        for (int i = 0; i < parameter.length; i++) {
                            Object obj = parameter[i];
                            Class<?> expectedType = parameterTypes[i];

                            if ((obj instanceof String && !expectedType.getName().equals(obj))
                                    || (!(obj instanceof String) && !expectedType.isInstance(obj))) {
                                isCompatible = false;
                                break;
                            }
                        }

                        if (isCompatible) {
                            return parameterTypes;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Find parameter", e);
        }

        return parameter;
    }
}
