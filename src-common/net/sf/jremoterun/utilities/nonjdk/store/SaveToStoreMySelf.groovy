package net.sf.jremoterun.utilities.nonjdk.store


interface SaveToStoreMySelf {

    String save(String beanVarName, JavaBean javaBean,Writer3 writer3, ObjectWriter objectWriter);

}