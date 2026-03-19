package android.compat.annotation;
import java.lang.annotation.*;
/** AOSP compilation stub. */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface EnabledAfter {
    int targetSdkVersion() default 0;
}
