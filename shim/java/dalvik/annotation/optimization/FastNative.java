package dalvik.annotation.optimization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Stub for AOSP compilation. Marks native methods for fast JNI call. */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface FastNative {
}
