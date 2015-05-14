package ru.ifmo.ctddev.salinskii.implementor;

import java.io.File;

/**
 * Class to run implementation creation with using {@link ru.ifmo.ctddev.salinskii.implementor.JarImplementor} and {@link ru.ifmo.ctddev.salinskii.implementor.Implementor}.
 * Created by Alimantu on 23.04.2015.
 * @author Alexander Salinskii
 */
public class ImplementorRunner {

    public static final File CURRENT_DIR = new File(System.getProperty("user.dir"));
    public static final String DEFAULT_DIR = "impl";
    public static final JarImplementor JAR_IMPLEMENTOR = new JarImplementor();

    public static void main(final String[] arg){
        try {
            if (arg.length == 1) {
                new Implementor().toFileImplement(Class.forName(arg[0]), new File(DEFAULT_DIR));
                return;
            } else if (arg.length == 3 && arg[0].equals("-jar")) {
                File fileName = new File(CURRENT_DIR, arg[2]);
                JAR_IMPLEMENTOR.implementJar(Class.forName(arg[1]), fileName);
                return;
            }
            System.err.println("Argument format: |-jar + \"class name\" + \"|*.jar file\"");
        } catch (Exception e){
            System.err.println("Error during implementation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
