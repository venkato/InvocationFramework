package idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib

enum ReflectionElement {

    setFieldValue, findField, getFieldValue, invokeJavaMethod, findMethodByParamTypes1, findMethodByParamTypes2, findMethodByCount,
    ;

    public static EnumSet<ReflectionElement> reFields = EnumSet.of(ReflectionElement.setFieldValue,
            ReflectionElement.getFieldValue, ReflectionElement.findField);

    public static EnumSet<ReflectionElement> reMethods = EnumSet.of(ReflectionElement.invokeJavaMethod,
            findMethodByCount,findMethodByParamTypes1,findMethodByParamTypes2);


}
