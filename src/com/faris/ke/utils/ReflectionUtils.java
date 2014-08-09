package com.faris.ke.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KingFaris10
 * @version 1.0.0
 */
@SuppressWarnings("rawtypes")
public class ReflectionUtils {
    private static String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
    private static String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");

    /**
     * Set all the fields in the Map (key) to the accessibility in the Map (value).
     *
     * @param fieldMap - The Map with the fields and the accessibility.
     */
    public static void doneFields(Map<Field, Boolean> fieldMap) {
        if (fieldMap != null) {
            for (Map.Entry<Field, Boolean> fieldEntry : fieldMap.entrySet()) {
                if (fieldEntry.getKey() != null) {
                    try {
                        fieldEntry.getKey().setAccessible(fieldEntry.getValue());
                    } catch (Exception ex) {
                        continue;
                    }
                }
            }
        }
    }

    /**
     * Set all the methods in the Map to the accessibility in the Map.
     *
     * @param methodMap - The Map with the methods and the accessibility.
     */
    public static void doneMethods(Map<Method, Boolean> methodMap) {
        if (methodMap != null) {
            for (Map.Entry<Method, Boolean> methodEntry : methodMap.entrySet()) {
                if (methodEntry.getKey() != null) {
                    try {
                        methodEntry.getKey().setAccessible(methodEntry.getValue());
                    } catch (Exception ex) {
                        continue;
                    }
                }
            }
        }
    }

    /**
     * Get a class from OBC.
     *
     * @param className - The name of the class.
     * @return The class from OBC package.
     */
    public static Class getBukkitClass(String className) {
        try {
            return Class.forName(OBC_PREFIX + "." + className);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Get a field from a class by the field name.
     *
     * @param clazz - The class.
     * @param fieldName - The name of the method.
     * @return The field from a class.
     */
    public static FieldAccess getField(Class clazz, String fieldName) {
        if (clazz != null && fieldName != null) {
            do {
                try {
                    Field field = clazz.getField(fieldName);
                    if (field == null) field = clazz.getDeclaredField(fieldName);
                    if (field != null) return new FieldAccess(field);
                } catch (NoSuchFieldException ex) {
                    try {
                        Field field = clazz.getDeclaredField(fieldName);
                        if (field != null) return new FieldAccess(field);
                    } catch (Exception ex2) {
                    }
                } catch (Exception ex) {
                }
            } while (clazz.getSuperclass() != Object.class && ((clazz = clazz.getSuperclass()) != null));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    /**
     * Get a field as a type.
     * @param instance - The instance as an Object.
     * @param fieldName - The field name.
     * @return The field's value for that instance.
     */
    public static <T> T getField(Object instance, String fieldName) {
        Class<?> checkClass = instance.getClass();
        do {
            try {
                Field field = checkClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return (T) field.get(instance);
            } catch (Exception e) {
            }
        } while (checkClass.getSuperclass() != Object.class && ((checkClass = checkClass.getSuperclass()) != null));
        return null;
    }

    /**
     * Get all the fields in a class.
     *
     * @param clazz - The class.
     * @return The Map of the fields and the old accessibility from a class.
     */
    public static Map<Field, Boolean> getFields(Class clazz) {
        Map<Field, Boolean> fieldMap = new HashMap<Field, Boolean>();
        if (clazz != null) {
            try {
                for (Field field : clazz.getFields()) {
                    if (field != null) {
                        boolean wasAccessible = field.isAccessible();
                        if (!wasAccessible) field.setAccessible(true);
                        fieldMap.put(field, wasAccessible);
                    }
                }
                for (Field field : clazz.getDeclaredFields()) {
                    if (field != null) {
                        boolean wasAccessible = field.isAccessible();
                        if (!wasAccessible) field.setAccessible(true);
                        fieldMap.put(field, wasAccessible);
                    }
                }
            } catch (Exception ex) {
            }
        }
        return fieldMap;
    }

    /**
     * Get a method from a class by the method name.
     *
     * @param clazz - The class.
     * @param methodName - The name of the method.
     * @return The method from a class.
     */
    @SuppressWarnings("unchecked")
    public static MethodInvoker getMethod(Class clazz, String methodName, Class... parameterTypes) {
        if (clazz != null && methodName != null) {
            try {
                Method method = clazz.getMethod(methodName, parameterTypes);
                if (method == null) method = clazz.getDeclaredMethod(methodName, parameterTypes);
                if (method != null) return new MethodInvoker(method);
                else if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class)
                    return getMethod(clazz.getSuperclass(), methodName, parameterTypes);
            } catch (NoSuchMethodException ex) {
                try {
                    Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
                    if (method != null) return new MethodInvoker(method);
                    else if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class)
                        return getMethod(clazz.getSuperclass(), methodName, parameterTypes);
                } catch (Exception ex2) {
                }
            } catch (Exception ex) {
            }
        }
        return null;
    }

    /**
     * Get all the methods in a class.
     *
     * @param clazz - The class.
     * @return The Map of the methods and the old accessibility from a class.
     */
    public static Map<Method, Boolean> getMethods(Class clazz) {
        return getMethods(clazz, false);
    }

    /**
     * Get all the methods in a class and optionally its super class.
     *
     * @param clazz - The class.
     * @param showSuperclassMethods - Return methods from the super class.
     * @return The Map of the methods and the old accessibility from a class.
     */
    public static Map<Method, Boolean> getMethods(Class clazz, boolean showSuperclassMethods) {
        Map<Method, Boolean> methodMap = new HashMap<Method, Boolean>();
        if (clazz != null) {
            try {
                for (Method method : clazz.getMethods()) {
                    if (method != null) {
                        if (showSuperclassMethods && clazz.getSuperclass() != null) {
                            for (Method superMethod : clazz.getSuperclass().getMethods()) {
                                if (superMethod != null && superMethod.getName().equals(method.getName())) break;
                            }
                        }
                        boolean wasAccessible = method.isAccessible();
                        if (!wasAccessible) method.setAccessible(true);
                        methodMap.put(method, wasAccessible);
                    }
                }
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method != null) {
                        if (showSuperclassMethods && clazz.getSuperclass() != null) {
                            for (Method superMethod : clazz.getSuperclass().getMethods()) {
                                if (superMethod != null && superMethod.getName().equals(method.getName())) break;
                            }
                        }
                        boolean wasAccessible = method.isAccessible();
                        if (!wasAccessible) method.setAccessible(true);
                        methodMap.put(method, wasAccessible);
                    }
                }
            } catch (Exception ex) {
            }
        }
        return methodMap;
    }

    /**
     * Get a class from NMS.
     *
     * @param className - The name of the class.
     * @return The class from NMS package.
     */
    public static Class getMinecraftClass(String className) {
        try {
            return Class.forName(NMS_PREFIX + "." + className);
        } catch (Exception ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    /**
     * Check if a class is a subclass of a super class.
     * @param clazz - The class.
     * @param superClass - The super class.
     * @return Whether the class is a subclass of a super class.
     */
    public static boolean instanceOf(Class clazz, Class superClass) {
        return clazz != null && superClass != null && clazz.isAssignableFrom(superClass);
    }

    public static interface ReflectionAccess {
        boolean wasAccessible = false;

        public ReflectionAccess setAccessible();

        public ReflectionAccess setAccessible(boolean flag);

        public boolean wasAccessible();
    }

    public static class MethodInvoker implements ReflectionAccess {
        private Method method = null;
        private boolean wasAccessible = false;

        public MethodInvoker(Method method) {
            this(method, method.isAccessible());
        }

        public MethodInvoker(Method method, boolean wasAccessible) {
            this.method = method;
            this.wasAccessible = wasAccessible;
        }

        public Method getMethod() {
            return this.method;
        }

        public Object invoke(Object instance, Object... paramValues) throws Exception {
            this.method.setAccessible(true);
            Object invoked = this.method.invoke(instance, paramValues);
            this.method.setAccessible(this.wasAccessible());
            return invoked;
        }

        public MethodInvoker setAccessible() {
            return this.setAccessible(this.wasAccessible);
        }

        public MethodInvoker setAccessible(boolean flag) {
            this.method.setAccessible(flag);
            return this;
        }

        public boolean wasAccessible() {
            return this.wasAccessible;
        }
    }

    public static class FieldAccess implements ReflectionAccess {
        private Field field = null;
        private boolean wasAccessible = false;

        public FieldAccess(Field method) {
            this(method, method.isAccessible());
        }

        public FieldAccess(Field method, boolean wasAccessible) {
            this.field = method;
            this.wasAccessible = wasAccessible;
        }

        @SuppressWarnings("unchecked")
        public <T> T get(Class unused) throws Exception {
            return unused == String.class ? (T) this.getObject(null).toString() : (unused == Integer.class ? (T) new Integer(Integer.parseInt(this.getObject(null).toString())) : unused == Boolean.class ? (T) new Boolean(Boolean.parseBoolean(this.getObject(null).toString())) : (T) this.getObject(null));
        }

        public Object getObject(Object instance) throws Exception {
            this.field.setAccessible(true);
            try {
                Object value = this.field.get(instance);
                this.field.setAccessible(this.wasAccessible());
                return value;
            } catch (Exception ex) {
                this.field.setAccessible(this.wasAccessible());
                throw ex;
            }
        }

        public Field getField() {
            return this.field;
        }

        public void set(Object value) throws Exception {
            this.set(null, value);
        }

        public void set(Object instance, Object value) throws Exception {
            this.field.setAccessible(true);
            this.field.set(instance, value);
            this.field.setAccessible(this.wasAccessible());
        }

        public void setFinal(Object value) throws Exception {
            this.setFinal(null, value);
        }

        public void setFinal(Object instance, Object value) throws Exception {
            FieldAccess modifiersFieldAccess = ReflectionUtils.getField(Field.class, "modifiers");
            Field modifiersField = modifiersFieldAccess.getField();
            modifiersField.setAccessible(true);

            int previousModifier = modifiersField.getInt(this.field);
            modifiersField.setInt(this.field, this.field.getModifiers() & ~Modifier.FINAL);

            this.field.setAccessible(true);
            this.field.set(instance, value);
            this.field.setAccessible(this.wasAccessible());

            modifiersField.setInt(this.field, previousModifier);
            modifiersField.setAccessible(modifiersFieldAccess.wasAccessible());
        }

        public FieldAccess setAccessible() {
            return this.setAccessible(this.wasAccessible);
        }

        public FieldAccess setAccessible(boolean flag) {
            this.field.setAccessible(flag);
            return this;
        }

        public boolean wasAccessible() {
            return this.wasAccessible;
        }
    }
}
