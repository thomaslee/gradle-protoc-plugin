package co.tomlee.gradle.plugins.protoc

import java.util.Set;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlugin

import co.tomlee.gradle.plugins.protoc.tasks.ProtobufCompile;


class GradleProtocPlugin implements Plugin<Project> {
    public static final String COMPILE_PROTO_TASK = "compileProto"
    public static final String CLEAN_PROTO_TASK = "cleanProto"
    private static final String PROTOC_EXTENSION = "protoc"
    private static final String PROTO_SOURCE = "src/main/proto"
    private static final String GEN_JAVA_SOURCE = "src/main/gen-java"

    @Override
    public void apply(Project project) {
        project.extensions.create(PROTOC_EXTENSION, GradleProtocPluginExtension, project)
        project.ext.ProtobufCompile = ProtobufCompile
        project.tasks.create(COMPILE_PROTO_TASK, ProtobufCompile) {
            inputs.files project.fileTree(PROTO_SOURCE).include("**/*.proto")

            path project.file(PROTO_SOURCE)
        }

        def javaPlugin = project.plugins.findPlugin("java")
        if (javaPlugin) {
            project.getTasksByName(COMPILE_PROTO_TASK, false).each { Task compileProto ->
                installTaskDependencies(project, compileProto, JavaPlugin.COMPILE_JAVA_TASK_NAME)
            }
        }
        else {
            project.plugins.whenPluginAdded { plugin ->
                if (plugin.id.equals("java")) {
                    project.getTasksByName(COMPILE_PROTO_TASK, false).each { Task compileProto ->
                        installTaskDependencies(project, compileProto, JavaPlugin.COMPILE_JAVA_TASK_NAME)
                    }
                }
            }
        }
    }

    private void installTaskDependencies(Project project,
                                         Task compileProto,
                                         String taskName)
    {
        def destination = project.file(GEN_JAVA_SOURCE)
        project.getTasksByName(taskName, false).each { Task task ->
            project.sourceSets.main.java.srcDir destination.path
            compileProto.plugins.create("java").out destination
            task.dependsOn compileProto

            project.getTasksByName(BasePlugin.CLEAN_TASK_NAME, false).each { Task cleanTask ->
                def cleanProto = project.tasks.maybeCreate(CLEAN_PROTO_TASK) << {
                    project.delete compileProto.plugins["java"].out
                }
                cleanTask.dependsOn cleanProto
            }
        }
    }
}
