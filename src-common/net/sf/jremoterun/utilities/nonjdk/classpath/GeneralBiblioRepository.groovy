package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.mdep.ivy.IBiblioRepository;

import java.util.logging.Logger;

@CompileStatic
class GeneralBiblioRepository implements IBiblioRepository {

    public final String name;
    public final String url;

    GeneralBiblioRepository(String name, String url) {
        this.name = name
        this.url = url
    }

    @Override
    String name() {
        return name;
    }

    String getUrl() {
        return url
    }

    @Override
    String toString() {
        return name
    }

    boolean equals(o) {
        if(o==null){
            return false
        }
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        GeneralBiblioRepository that = (GeneralBiblioRepository) o

        if (name != that.name) return false
        if (url != that.url) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + (url != null ? url.hashCode() : 0)
        return result
    }
}
