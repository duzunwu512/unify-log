package com.gzl.log.util;

import java.util.Collection;
        import java.util.Iterator;
        import java.util.function.Supplier;
public class Assert {
    private Assert() {
    }

    public static void notEmpty(CharSequence string, String message) {
        if (string==null || "".equals(string)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(CharSequence string, Supplier<String> messageSupplier) {
        if (string==null || "".equals(string)) {
            throw new IllegalArgumentException((String)messageSupplier.get());
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object, Supplier<String> messageSupplier) {
        if (object == null) {
            throw new IllegalArgumentException((String)messageSupplier.get());
        }
    }

    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Object[] array, Supplier<String> messageSupplier) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException((String)messageSupplier.get());
        }
    }

    public static void notEmpty(int[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void noNullElements(Object[] array, String message) {
        if (array != null) {
            Object[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Object element = var2[var4];
                if (element == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }

    }

    public static void noNullElements(Object[] array, Supplier<String> messageSupplier) {
        if (array != null) {
            Object[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Object element = var2[var4];
                if (element == null) {
                    throw new IllegalArgumentException((String)messageSupplier.get());
                }
            }
        }

    }

    public static void noNullElements(Collection<?> c, String message) {
        if (c != null) {
            Iterator var2 = c.iterator();

            while(var2.hasNext()) {
                Object element = var2.next();
                if (element == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }

    }

    public static void noNullElements(Collection<?> c, Supplier<String> messageSupplier) {
        if (c != null) {
            Iterator var2 = c.iterator();

            while(var2.hasNext()) {
                Object element = var2.next();
                if (element == null) {
                    throw new IllegalArgumentException((String)messageSupplier.get());
                }
            }
        }

    }

    public static void isTrue(boolean value, String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean value, Supplier<String> messageSupplier) {
        if (!value) {
            throw new IllegalArgumentException((String)messageSupplier.get());
        }
    }

    public static void assertState(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    public static void assertState(boolean condition, Supplier<String> messageSupplier) {
        if (!condition) {
            throw new IllegalStateException((String)messageSupplier.get());
        }
    }
}