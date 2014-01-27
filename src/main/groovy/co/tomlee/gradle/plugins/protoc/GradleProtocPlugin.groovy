package co.tomlee.gradle.plugins.protoc

import org.gradle.api.Plugin
import org.gradle.api.Project


class GradleProtocPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.extensions.create("protoc", GradleProtocPluginExtension, project)
    }
}
