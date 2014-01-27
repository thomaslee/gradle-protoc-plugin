package co.tomlee.gradle.plugins.protoc

import org.gradle.api.Project;

class GradleProtocPluginExtension {
    String executable = "protoc"
    List<File> path = []
    final Project project
    
    public GradleProtocPluginExtension(final Project project) {
        this.project = project
    }
    
    def executable(String executable) {
        this.executable = executable
    }
    
    def path(String path) {
        final File file = new File(path)
        if (file.isAbsolute()) {
            this.path << file
        }
        else {
            this.path << project.file(path)
        }
    }
    
    def path(File path) {
        this.path << path
    }
}
