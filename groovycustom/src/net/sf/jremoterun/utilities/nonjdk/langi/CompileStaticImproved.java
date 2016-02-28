package net.sf.jremoterun.utilities.nonjdk.langi;

import groovy.transform.TypeCheckingMode;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE,
        ElementType.CONSTRUCTOR
})
@GroovyASTTransformationClass("net.sf.jremoterun.utilities.nonjdk.langi.JrrStaticCompileTransformation")
public @interface CompileStaticImproved {

    TypeCheckingMode value() default TypeCheckingMode.PASS;


    /**
     * The list of (classpath resources) paths to type checking DSL scripts, also known
     * as type checking extensions.
     * @return an array of paths to groovy scripts that must be on compile classpath
     */
    String[] extensions() default {};



}
