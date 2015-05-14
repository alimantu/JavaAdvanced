package ru.ifmo.ctddev.salinskii.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Random;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Implementation of {@link info.kgeorgiy.java.advanced.implementor.JarImpler}
 * Implements only interfaces.
 * Created by Alimantu on 4.9.2015.
 * @author Alexander Salinskii
 */
public class JarImplementor extends ru.ifmo.ctddev.salinskii.implementor.Implementor implements JarImpler {

    private static final Random RANDOM = new Random();
    private static final int BUFF_SIZE = 1024;

    @Override
    public void implementJar(Class<?> token, File jarFile) throws ImplerException {
        File parentDirectory = jarFile.getParentFile();
        parentDirectory.mkdirs();
        File outputDirectory = new File(parentDirectory, "out" + RANDOM.nextInt());
        outputDirectory.deleteOnExit();

        File implementationFile = toFileImplement(token, outputDirectory);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        System.out.println(System.getProperty("java.home"));
        Objects.requireNonNull(compiler);
        Objects.requireNonNull(implementationFile);
        if(compiler.run(null, null, null, implementationFile.getAbsolutePath()) != 0){
            throw new ImplerException("Can't compile " + token.getName() + " implementation!");
        }

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        File classFile = new File(changeExtension(implementationFile.toString(), ".java", ".class"));

        try(JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarFile), manifest);
            InputStream inputStream = new FileInputStream(classFile)){
            String name = changeExtension(getPathName(token).replace("//", "/"), ".java", ".class");
            JarEntry jarEntry = new JarEntry(name);
            jarEntry.setTime(System.currentTimeMillis());
            jarOutputStream.putNextEntry(jarEntry);

            int cnt;
            byte[] buff = new byte[BUFF_SIZE];
            while((cnt = inputStream.read(buff)) >= 0){
                jarOutputStream.write(buff, 0, cnt);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            deleteDirectory(outputDirectory.toPath());
        }
    }

    /**
     * Deleting all the files in <tt>path</tt>
     * @param path  path in witch all files will be deleted.
     */
    private void deleteDirectory(Path path) {
        try{
            Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
                    Files.delete(path);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
                    Files.delete(path);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Replaces the <tt>oldExtension</tt> in the <tt>fileName</tt> with the <tt>newExtension</tt>.
     * @param fileName      the modified string
     * @param oldExtension  the substring that will be changed
     * @param newExtension  the substring that substitute the <tt>oldExtention</tt>
     * @return the resulting <a href="http://docs.oracle.com/javase/8/docs/api/java/lang/String.html">String</a>
     */
    private String changeExtension(String fileName, String oldExtension, String newExtension) {
        int oldExtensionIndex = fileName.lastIndexOf(oldExtension);
        if(oldExtensionIndex < 0){
          return fileName;
        }
        String extention = fileName.substring((oldExtensionIndex)).replaceFirst(oldExtension, newExtension);
        return fileName.substring(0, oldExtensionIndex) + extention;
    }

}
