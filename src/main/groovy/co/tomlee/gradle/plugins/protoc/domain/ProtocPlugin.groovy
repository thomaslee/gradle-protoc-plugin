package co.tomlee.gradle.plugins.protoc.domain

import org.gradle.api.Named
import org.gradle.api.GradleException

class ProtocPlugin implements Named {
    final String name
    String program
    File outDir
    LinkedHashMap<String, Object> options = new LinkedHashMap<>()
    
    public ProtocPlugin(String name) {
        this.name = name
    }
    
    def outDir(File outDir) {
        this.outDir = outDir
    }
    
    def program(String program) {
        this.program = program
    }
    
    def option(Map<String, Object> opt) {
        if (opt.size() > 1) {
            throw new GradleException("Too many arguments for protoc plugin option")
        }
        options.putAll(opt)
    }
    
    def options(Map<String, Object> opts) {
        options.putAll(opts)
    }
}
