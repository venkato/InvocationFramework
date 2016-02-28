package net.sf.jremoterun.utilities.jrrbean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import net.sf.jremoterun.utilities.MBeanFromJavaBean;

public class JrrBeanMethods {


    private final Map<String, Object> propertiesInfo;

    public static String objectS = "object";

    public static String methodsMapS = "methodsMap";

    public static String mbeanS = "mbean";

    public static String classS = "class";

    public JrrBeanMethods(final Class clazz) {
        propertiesInfo = JrrBeanMaker.getObjectBeanFieldsMap(clazz);
    }

    public MBeanFromJavaBean getMBean() {
        return (MBeanFromJavaBean) propertiesInfo.get(mbeanS);
    }

    public void setMBean(final MBeanFromJavaBean object) {
        propertiesInfo.put(mbeanS, object);
    }

    public Object getObject() {
        return propertiesInfo.get(objectS);
    }

    public void setObject(final Object object) {
        propertiesInfo.put(objectS, object);
    }

    public Map<ArrayList<String>, Method> getMethodsMap() {
        return (Map) propertiesInfo.get(methodsMapS);
    }

    public void setMethodsMap(final Map methodsMap) {
        propertiesInfo.put(methodsMapS, methodsMap);
    }

    public Class getJavaBeanClass() {
        return (Class) propertiesInfo.get(classS);
    }

    public void setJavaBeanClass(final Class clazz) {
        propertiesInfo.put(classS, clazz);
    }
}

/* 
 * JRemoteRun.sf.net. License:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
 