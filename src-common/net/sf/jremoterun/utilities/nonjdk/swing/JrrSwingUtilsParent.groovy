package net.sf.jremoterun.utilities.nonjdk.swing

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.awt.*
import java.util.logging.Logger

@CompileStatic
class JrrSwingUtilsParent {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    /**
     * Exclude arg
     */
    static <T> T findParentWindow(Component component,Class<T> clazz) {
        Container windowParent = component.getParent();
        if (windowParent == null) {
            return null;
        }

        if (clazz.isInstance(windowParent)) {
            return windowParent as T;

        }
        return findParentWindow(windowParent, clazz);
    }


    /**
     * Include arg
     */
    static <T> T findParentComponent(Component component,Class<T> clazz) {
        if (clazz.isInstance(component)) {
            return component as T;

        }
        return findParentWindow(component, clazz);
    }

}
