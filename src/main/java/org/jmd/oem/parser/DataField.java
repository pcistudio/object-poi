package org.jmd.oem.parser;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DataField {
    String name() default "";
    String format() default "";

    boolean required() default false;
}
