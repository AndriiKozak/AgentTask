/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaagenttask;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

/**
 *
 * @author Andrii_Kozak1
 */
public class StacktraceClassTransformer implements ClassFileTransformer {

    private final ClassPool pool;

    public StacktraceClassTransformer() {
        pool = ClassPool.getDefault();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        pool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
        try {
            CtClass cclass = pool.get(className.replace("/", "."));
            if (!cclass.isFrozen()) {
                for (CtMethod method : cclass.getDeclaredMethods()) {
                    if (!Modifier.isNative(method.getModifiers()) && !method.isEmpty()) {
                        method.insertBefore(createJavaString(method));
                    }
                }
            }
            return cclass.toBytecode();
        } catch (Exception ex) {
            System.out.println("!!!" + className);
            ex.printStackTrace();
        }
        return null;
    }

    private String createJavaString(CtMethod method) {
        StringBuilder sb = new StringBuilder();
        sb.append("System.out.println(\"");
        sb.append(method.getName()).append(" \"");
        try {
            if (method.getParameterTypes().length > 0) {
                sb.append("+java.util.Arrays.toString($args)+\" \"");
            }
        } catch (NotFoundException ex) {
            Logger.getLogger(StacktraceClassTransformer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (Modifier.isStatic(method.getModifiers())) {
            sb.append("+\"static\"");
        } else {
            sb.append("+$0");
        }
        sb.append(");");
        return sb.toString();
    }

}
