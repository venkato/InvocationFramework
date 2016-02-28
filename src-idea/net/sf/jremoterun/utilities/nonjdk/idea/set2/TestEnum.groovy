package net.sf.jremoterun.utilities.nonjdk.idea.set2;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.EnumIdea
import net.sf.jremoterun.utilities.nonjdk.store.EnumIdeaSupport2;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class TestEnum implements EnumIdea{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String nnaa;

    @Override
    EnumIdeaSupport2 getEnumIdeaSupport2() {
        return null
    }

    @Override
    String getName() {
        return nnaa
    }

    static TestEnum getE(String s){
        TestEnum y = new TestEnum()
        y.nnaa = s
        return y
    }
}
