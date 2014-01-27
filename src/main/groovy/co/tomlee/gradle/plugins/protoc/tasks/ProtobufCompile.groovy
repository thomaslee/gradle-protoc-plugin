package co.tomlee.gradle.plugins.protoc.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import co.tomlee.gradle.plugins.protoc.domain.ProtocPlugin

import org.gradle.api.GradleException

class ProtobufCompile extends DefaultTask {
    String protoc
    NamedDomainObjectContainer<ProtocPlugin> plugins
    List<File> path
    
    ProtobufCompile() {
        plugins = project.container(ProtocPlugin)
        path = []
        // inputs.files(project.fileTree("src/main/proto").include("**/*.proto"))
    }
    
    @TaskAction
    def invokeProtoc() {
        def command = buildCommand()
        println command
        ensureOutputDirectoriesExist()
        def p = command.execute()
        if (p.waitFor() != 0) {
            throw new GradleException("${protoc} command failed")
        }
    }

    @OutputDirectories
    def outputDirectories() {
        def result = []
        plugins.each { ProtocPlugin plugin ->
            result << pluginOutDir(plugin)
        }
        return result
    }
    
    private void ensureOutputDirectoriesExist() {
        outputDirectories().each { File dir ->
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new GradleException("Failed to create output directory: ${dir.absolutePath}")
                }
            }
            if (!dir.isDirectory()) {
                throw new GradleException("Specified output path is not a directory: ${dir.absolutePath}")
            }
        }
    }
    
    List<String> buildCommand() {
        def protoc = (this.protoc != null ? this.protoc : project.protoc.driver)
        def command = [protoc]
        project.protoc.path.each { File includePath ->
            command << "-I${includePath.absolutePath}"
        }
        path.each { File includePath ->
            command << "-I${includePath.absolutePath}"
        }
        def builtins = ["java", "cpp", "python"]
        plugins.each { ProtocPlugin plugin ->
            if (!builtins.contains(plugin.name)) {
                if (plugin.program) {
                    command << "--plugin=${plugin.name}=${plugin.program}".toString()
                }
                else {
                    command << "--plugin=${plugin.name}".toString()
                }
            }
            def optionsPrefix = ""
            if (plugin.options.size() > 0) {
                def options = plugin.options.collect { option -> "${option.key}=${option.value}" }.join(",")
                optionsPrefix = options + ":"
            }
            // FIXME code duplication: see pluginOutDir
            def outDir = plugin.outDir != null ? plugin.outDir : project.file("src/main/${plugin.name}")
            command << "--${plugin.name}_out=${optionsPrefix}${outDir.absolutePath}"
        }
        def sources = this.inputs.files
        if (!sources.any()) {
            throw new GradleException("No protobuf sources found in ")
        }
        sources.each { command << it.absolutePath }
        return command
    }
    
    private File pluginOutDir(ProtocPlugin plugin) {
        // FIXME code duplication: see buildCommand
        return plugin.outDir != null ? plugin.outDir : project.file("src/main/${plugin.name}")
    }
    
    def protoc(String protoc) {
        this.protoc = protoc
    }
    
    def plugins(Closure c) {
        plugins.configure(c)
    }
    
    def srcDir(File srcDir) {
        this.srcDir = srcDir
    }
    
    def path(String pathItem) {
        final File file = new File(pathItem);
        if (file.isAbsolute()) {
            this.path << file
        }
        else {
            this.path = project.file(pathItem)
        }
    }
    
    def path(File pathItem) {
        this.path << pathItem
    }
}
