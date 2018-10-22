package net.infonode.properties.propertymap

import groovy.transform.CompileStatic
import net.infonode.properties.propertymap.PropertyMapManager
import net.infonode.util.collection.map.base.ConstMap;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilities

import javax.swing.SwingUtilities;
import java.util.logging.Logger;

@CompileStatic
class JrrIdwPropertyMapManager extends PropertyMapManager {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static PropertyMapManager propertyMapManagerDefault = getInstance();
    public static JrrIdwPropertyMapManager jrrIdwPropertyMapManager;

    public PropertyMapManager nested
    public boolean checkThreadPropAddMapChanges = true
    public boolean checkThreadPropOnBegin = false



    static void setManager(){

        if(jrrIdwPropertyMapManager==null) {
            jrrIdwPropertyMapManager = new JrrIdwPropertyMapManager()
            jrrIdwPropertyMapManager.nested = getInstance();
            JrrClassUtils.setFieldValue(PropertyMapManager, 'INSTANCE',jrrIdwPropertyMapManager)
        }else{
            PropertyMapManager propsManager = JrrClassUtils.getFieldValue(PropertyMapManager, 'INSTANCE') as PropertyMapManager
            if (propsManager instanceof JrrIdwPropertyMapManager) {

            }else{
                throw new Exception("Strange manager : ${propsManager.getClass()}")
            }
        }
    }

    boolean checkThread() {
        if (SwingUtilities.isEventDispatchThread()) {
            return true
        }
        Thread thread = Thread.currentThread()
        log.severe("Wrong thread ${thread.getId()} ${thread.getName()}", new Exception("Wrong idw tread : ${thread.getId()} ${thread.getName()}"))
        return false

    }

    @Override
    void beginBatch() {
        if(checkThreadPropOnBegin) {
            checkThread()
        }
        nested.beginBatch()
    }

    @Override
    void endBatch() {
        nested.endBatch()
    }

    @Override
    void addMapChanges(PropertyMapImpl propertyMap, ConstMap mapChanges) {
        if(checkThreadPropAddMapChanges) {
            checkThread()
        }
        try {
            nested.addMapChanges(propertyMap, mapChanges)
        } catch (Throwable e) {
            Thread thread = Thread.currentThread()
            try {

                log.severe("failed summary on thread : ${thread.getId()} ${thread.getName()}", e)
                log.severe("failed with details on thread : ${thread.getId()} ${thread.getName()} with props: ${propertyMap}, changes: ${mapChanges} with ex: ${e}")
                JrrUtilities.showException("failed on thread : ${thread.getId()} ${thread.getName()}", e)
            }catch(Throwable e2){
                log.severe("failed on thread v2 : ${thread.getId()} ${thread.getName()}", e2)
            }
        }
    }
}
