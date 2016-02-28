package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.ListStore;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class FileStore extends ListStore{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<File> currentList

    FileStore(File file) {
        super(file)
        currentList =  loadsettings()
    }

    void saveList6(){
        saveList5(currentList)
    }

    void saveList5(List<File> list){
        int last = Math.min(list.size(),5)
        saveToFile list.subList(0,last)
    }
}
