package com.grupoA;


import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import testsmells.TestSmellDetector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Mojo(name = "test-smell-list", defaultPhase = LifecyclePhase.TEST)
public class TestSmell extends AbstractMojo {

    /**
     * Location of the test source directory.
     */
    @Parameter(defaultValue = "${project.basedir}/src/test/java", required = true)
    private File testSourceDirectory;

    /**
     * Location of the production source directory.
     */
    @Parameter(defaultValue = "${project.basedir}/src/main/java", required = true)
    private File productionSourceDirectory;

    public void execute() throws MojoExecutionException {
        // Verificar que el directorio de pruebas existe
        if (!testSourceDirectory.exists() || !testSourceDirectory.isDirectory()) {
            throw new MojoExecutionException("El directorio de pruebas no existe: " + testSourceDirectory.getAbsolutePath());
        }

        // Listar todos los archivos de prueba
        File[] testFiles = testSourceDirectory.listFiles();
        if (testFiles != null) {
            for (File testFile : testFiles) {
                if (testFile.isFile() && testFile.getName().endsWith("Test.java")) {
                    String productionFileName = testFile.getName().replace("Test.java", ".java");
                    File productionFile = new File(productionSourceDirectory, productionFileName);
                    
                    if (productionFile.exists()) {
                        getLog().info("Archivo de prueba encontrado: " + testFile.getName() + " -> Archivo de producción correspondiente: " + productionFile.getName());
                    } else {
                        getLog().warn("Archivo de producción no encontrado para: " + testFile.getName());
                    }
                }
            }
        } else {
            getLog().warn("No se encontraron archivos de prueba en el directorio: " + testSourceDirectory.getAbsolutePath());
        }

        // Crear el archivo touch.txt
        File touch = new File(testSourceDirectory.getAbsolutePath(), "touch.txt");

        try (FileWriter writer = new FileWriter(touch)) {
            writer.write("Lista de archivos de prueba generada.");
            getLog().info("Archivo creado: " + touch.getAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException("Error creando el archivo " + touch, e);
        }
    }
}
