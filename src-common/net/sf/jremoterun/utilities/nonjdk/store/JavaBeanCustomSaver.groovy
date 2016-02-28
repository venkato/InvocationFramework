package net.sf.jremoterun.utilities.nonjdk.store


interface JavaBeanCustomSaver {

    List<String> save(String beanVarName, Writer3 writer3, ObjectWriter objectWriter);

}