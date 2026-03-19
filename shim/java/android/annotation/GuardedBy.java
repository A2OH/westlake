package android.annotation;
import java.lang.annotation.*;
/** AOSP compilation stub. */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface GuardedBy {
    String value() default "";
}
