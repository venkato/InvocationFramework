package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.mdep.ivy.IBiblioRepository;

import java.util.logging.Logger;

@CompileStatic
class UserBintrayRepo implements IBiblioRepository{

    public final String name;

    /**
     * in format : 'jfrog/bintray-tools'
     */
    UserBintrayRepo(String name) {
        this.name = name
        assert name.contains('/')
    }

    @Override
    String name() {
        return name;
    }

    @Override
    String getUrl() {
        return "https://dl.bintray.com/${name}"
    }

    @Override
    String toString() {
        return name
    }

    boolean equals(o) {
        if(o==null){
            return false
        }
        if (this.is(o)) return true;
        if (getClass() != o.getClass()) return false;

        UserBintrayRepo that = (UserBintrayRepo) o;

        if (name != that.name) return false;
        return true
    }

    int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }
}
