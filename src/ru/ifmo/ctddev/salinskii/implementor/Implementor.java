package ru.ifmo.ctddev.salinskii.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Implementation of {@link info.kgeorgiy.java.advanced.implementor.Impler}
 * Implements only interfaces.
 * Created by Alimantu on 3.12.2015
 * @author Alexander Salinskii
 */
public class Implementor implements Impler {

    /**
     * This suffix must be added to the name of the implemented class.
     */
    private static final String IMPLEMENTATION_SUFFIX = "Impl";

    @Override
    public void implement(Class<?> token, File root) throws ImplerException {
        toFileImplement(token, root);
    }

    /**
     * Creates the implementation of the <tt>token</tt> writing it to the to the <tt>output</tt>
     * @param token the token of the implemented class
     * @param output the <a href="https://docs.oracle.com/javase/8/docs/api/java/io/Writer.html">Writer</a> we need to write our implementation
     * @throws IOException if there are some problems with creation of the implementation
     */
    public void implement(Class<?> token, Writer output) throws IOException {
        final String implementationName = getImplementationName(token);

        output.write("package " + token.getPackage().getName() + ";\n\n");
        output.write("public class " + implementationName + (token.isInterface() ? " implements " : " extends ") + token.getSimpleName() + " {\n");

        Set<String> nonAbstractMethods = new HashSet<>();
        Map<String, Method> allMethods = new HashMap<>();
        Queue<Class<?>> tokenQueue = new ArrayDeque<>();

        tokenQueue.add(token);
        while(!tokenQueue.isEmpty()){
            Class<?> currToken = tokenQueue.remove();
            for(Method method: currToken.getDeclaredMethods()){
                String methodKey = getMethodKey(method);
                allMethods.put(methodKey, method);
                if(!Modifier.isAbstract(method.getModifiers())){
                    nonAbstractMethods.add(methodKey);
                }
            }

            Collections.addAll(tokenQueue, currToken.getInterfaces());
            Class<?> superClass = currToken.getSuperclass();
            if(superClass != null){
                tokenQueue.add(superClass);
            }
        }

        for(Map.Entry<String, Method> entry : allMethods.entrySet()){
            if(!nonAbstractMethods.contains(entry.getKey()))
                overrideMethod(entry.getValue(), output);
        }

        output.write("}");
    }

    /**
     * Override the methods with default return value.
     * <p>
     * <tt>false</tt> for boolean, <tt>0</tt> for other primitives, <tt>null</tt> for non-primitives.
     * @param method    the method to be overrided.
     * @param output    output <a href="https://docs.oracle.com/javase/8/docs/api/java/io/Writer.html">Writer</a> object.
     * @throws IOException if there is some problems during writing.
     */
    private void overrideMethod(Method method, Writer output) throws IOException {
        output.write("\n\t@Override\n\tpublic ");

        //Type of return value of the method
        output.write(getTypeName(method.getReturnType()) + " ");

        //Method name
        output.write(method.getName());

        //Method args
        output.write("(" + getArguments(method.getParameterTypes(), method.isVarArgs()) + ") {\n");

        //Method body
        if(!method.getReturnType().equals(void.class)){
            output.write("\t\treturn ");
            if(!method.getReturnType().isPrimitive()){
                output.write("null");
            } else if(method.getReturnType().equals(boolean.class)){
                output.write("false");
            } else {
                output.write("0");
            }
            output.write(";\n");
        }
        output.write("\t}\n");
    }

    /**
     * Create a <a href="http://docs.oracle.com/javase/8/docs/api/java/lang/String.html">String</a> with enumeration of all the <tt>arguments</tt> with specified types.
     * <p>
     * Example
     * <p>
     * input: <code>[Integer.class, String.class, Object.class]</code>, <code>varArgs == true</code>:
     * <p>
     * output: "java.lang.Integer arg0, java.lang.String arg1, java.lang.Object... arg2"
     * @param arguments the array of the argument types.
     * @param varArgs   <code>true</code> if this method or constructor was declared to take
     *                  a variable number of arguments; <code>false</code> otherwise
     * @return  the resulting <a href="http://docs.oracle.com/javase/8/docs/api/java/lang/String.html">String</a> with arguments enumeration.
     */
    private String getArguments(Class<?>[] arguments, boolean varArgs) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arguments.length; i++){
            String argumentType = getTypeName(arguments[i]);
            if(varArgs && i == arguments.length - 1){
                argumentType = argumentType.replaceFirst("\\[\\]$", "...");
            }

            stringBuilder.append(argumentType + " arg" + i);

            if(i != arguments.length - 1){
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Creates the <a href="http://docs.oracle.com/javase/8/docs/api/java/lang/String.html">String</a> with <tt>argumentType</tt> name.
     * @param argumentType the type witch name we are searching for
     * @return <a href="http://docs.oracle.com/javase/8/docs/api/java/lang/String.html">String</a> with name of the type
     */
    private String getTypeName(Class<?> argumentType) {
        if (argumentType.isArray()){
            try{
                Class<?> clazz = argumentType;
                int dimensions = 0;
                while(clazz.isArray()){
                    clazz = clazz.getComponentType();
                    dimensions++;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(clazz.getName());
                for(int i = 0; i < dimensions; i++){
                    stringBuilder.append("[]");
                }
                return stringBuilder.toString();
            } catch (Throwable e){
                e.printStackTrace();
            }
        }
        return argumentType.getName();
    }

    /**
     * Return the created key of the <tt>method</tt>.
     * @param method    the method for key creation
     * @return the <a href="http://docs.oracle.com/javase/8/docs/api/java/lang/String.html">String</a> representation of the method key.
     */
    private String getMethodKey(Method method) {
        return method.getName() + "$"
                + Arrays.toString(method.getParameterTypes()) + "$"
                + method.isVarArgs();
    }

    /**
     * Create the implementation of the <tt>token</tt> class to the <a href="https://docs.oracle.com/javase/8/docs/api/java/io/File.html">File</a>, then return it.
     * @param token type token of class for implement
     * @param root  the root directory.
     * @return  file with the implementation of the <tt>token</tt> class
     * @throws ImplerException  if there are some problems with implementation creation.
     */
    protected File toFileImplement(Class<?> token, File root) throws ImplerException {
        checkImplementable(token);

        File outputFile = new File(root.getAbsolutePath() + "//" + getPathName(token));
        outputFile.getParentFile().mkdirs();

        try(Writer output = new BufferedWriter(new FileWriter(outputFile))){
            implement(token, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    /**
     * Creates the path of the implementation depends from class package.
     * @param token the token of the implemented class
     * @return path for the implementation
     */
    protected String getPathName(Class<?> token) {
        return token.getPackage().getName().replace(".", "//")
                + "//"
                + getImplementationName(token)
                + ".java";

    }

    /**
     * Creates the name of the implemented class concatenating the name of the <tt>token</tt> with <tt>IMPLEMENTATION_SUFFIX</tt>.
     * @param token the token of the implemented class
     * @return the result of the concatenation
     */
    private String getImplementationName(Class<?> token) {
        return token.getSimpleName() + IMPLEMENTATION_SUFFIX;
    }

    /**
     * Checks that we can implement <tt>token</tt> class.
     * @param token the token of the implemented class
     * @throws ImplerException  if we can't implement the <tt>token</tt> class
     */
    private void checkImplementable(Class<?> token) throws ImplerException{
        if(Modifier.isFinal(token.getModifiers())){
            throw new ImplerException("Can't implement final class");
        }
        if(!Modifier.isPublic(token.getModifiers())){
            throw new ImplerException("Can't implement non public class");
        }
        if(token.equals(Class.class)){
            throw new ImplerException("Can't extends from Class");
        }
        if(token.equals(Enum.class)){
            throw new ImplerException("Can't extends from Enum class");
        }
    }
}
