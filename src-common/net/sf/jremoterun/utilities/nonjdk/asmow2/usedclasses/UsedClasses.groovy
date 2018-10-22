package net.sf.jremoterun.utilities.nonjdk.asmow2.usedclasses

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.asmow2.EmptyClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.Remapper

import java.util.logging.Logger;

@CompileStatic
class UsedClasses extends Remapper{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public HashSet<String> usedTypes = new HashSet<>()
    public HashSet<Type> usedTypes2 = new HashSet<>()

    @Override
    String mapType(String internalName) {
        usedTypes.add(internalName)
        return super.mapType(internalName)
    }

    @Override
    public String mapDesc(final String descriptor) {
        return mapTypeJrr(Type.getType(descriptor)).getDescriptor();
    }

    @Override
    public String mapMethodDesc(final String methodDescriptor) {
        if ("()V".equals(methodDescriptor)) {
            return methodDescriptor;
        }

        StringBuilder stringBuilder = new StringBuilder("(");
        for (Type argumentType : Type.getArgumentTypes(methodDescriptor)) {
            stringBuilder.append(mapTypeJrr(argumentType).getDescriptor());
        }
        Type returnType = Type.getReturnType(methodDescriptor);
        if (returnType == Type.VOID_TYPE) {
            stringBuilder.append(")V");
        } else {
            stringBuilder.append(')').append(mapTypeJrr(returnType).getDescriptor());
        }
        return stringBuilder.toString();
    }

    @Override
    public Object mapValue(final Object value) {
        if (value instanceof Type) {
            return mapTypeJrr((Type) value);
        }
        return super.mapValue(value)
    }

    /**
     * {@link Remapper#mapType(org.objectweb.asm.Type)}
     */
    protected Type mapTypeJrr(final Type type) {
        usedTypes2.add(type)
        switch (type.getSort()) {
            case Type.ARRAY:
                StringBuilder remappedDescriptor = new StringBuilder();
                for (int i = 0; i < type.getDimensions(); ++i) {
                    remappedDescriptor.append('[');
                }
                remappedDescriptor.append(mapTypeJrr(type.getElementType()).getDescriptor());
                return Type.getType(remappedDescriptor.toString());
            case Type.OBJECT:
                usedTypes.add(type.getInternalName())
                String remappedInternalName = map(type.getInternalName());
                return remappedInternalName != null ? Type.getObjectType(remappedInternalName) : type;
            case Type.METHOD:
                return Type.getMethodType(mapMethodDesc(type.getDescriptor()));
            default:
                return type;
        }
    }

    static UsedClasses remapClassNoRedefine(byte[] bytes) {
        UsedClasses usedClasses = new UsedClasses()
        ClassReader classReader = new ClassReader(bytes);
        remapClassNoRedefine1(classReader,usedClasses);
        usedClasses.usedTypes.remove(classReader.getClassName())
        return usedClasses;
    }


    static void remapClassNoRedefine1(ClassReader classReader, UsedClasses usedClasses ) {
        ClassVisitor cw = new EmptyClassVisitor();
        ClassRemapper classRemapper = new ClassRemapper(cw, usedClasses)
        classReader.accept(classRemapper, 0);
    }


//    static void remapClassNoRedefine2(ClassReader cr, UsedClasses4 usedClasses ) {
//        ClassNode classNode = new ClassNode();
//        cr.accept(classNode, 0);
//
//        classNode.methods.each { usedClasses.mapMethodDesc( it.desc)}
//        classNode.fields.each { usedClasses.mapDesc( it.desc)}
//    }


}
