package co.tomlee.gradle.plugins.protoc.tasks;

import static org.junit.Assert.*;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import co.tomlee.gradle.plugins.protoc.GradleProtocPlugin;
import co.tomlee.gradle.plugins.protoc.domain.ProtocPlugin;
import static org.junit.Assume.*;

class ProtobufCompileTest {

    @Test
    public void protobufCompileTest() {
        final Project project = ProjectBuilder.builder().build();
        project.apply plugin: GradleProtocPlugin
        final ProtobufCompile protoc = project.task('protoc', type: ProtobufCompile);
        
        project.file('src/main/proto').mkdirs()
        final FileOutputStream fileOutputStream = new FileOutputStream(project.file('src/main/proto/example.proto'))
        final PrintWriter pw = new PrintWriter(fileOutputStream)
        try {
            pw.println("""
package example;

option java_package = "co.tomlee.gradle.plugins.protoc.example";

message ExampleMessage {
    optional string name = 10;
}
""")
        }
        finally {
            pw.close()
        }
        
        protoc.path(project.file("src/main/proto"))
        protoc.inputs.dir(project.file("src/main/proto"))
        protoc.plugins {
            java
            cpp
            rpckit {
                out project.file('src/main/java')
                option java_stubs: true
                option java_endpoints_httpservlet: true
                option java_proxies_httpclient: true
            }
        }
        assertEquals(3, protoc.plugins.size())
        
        final ProtocPlugin javaPlugin = protoc.plugins.java
        assertNotNull(javaPlugin)
        assertNull(javaPlugin.out)
        assertEquals(0, javaPlugin.options.size())
        
        final ProtocPlugin cppPlugin = protoc.plugins.cpp
        assertNotNull(cppPlugin)
        assertNull(cppPlugin.out)
        assertEquals(0, cppPlugin.options.size())
        
        final ProtocPlugin rpckitPlugin = protoc.plugins.rpckit
        assertNotNull(rpckitPlugin)
        assertEquals(project.file('src/main/java'), rpckitPlugin.out)
        assertEquals(3, rpckitPlugin.options.size())
        
        List<String> command = protoc.buildCommand()
        assertEquals("protoc", command[0])
        assertEquals("-I${project.file('src/main/proto').absolutePath}", command[1])
        assertEquals("--cpp_out=${project.file('src/main/cpp').absolutePath}", command[2])
        assertEquals("--java_out=${project.file('src/main/java').absolutePath}", command[3])
        assertEquals("--plugin=rpckit", command[4])
        assertEquals("--rpckit_out=java_stubs=true,java_endpoints_httpservlet=true,java_proxies_httpclient=true:${project.file('src/main/java').absolutePath}", command[5])
        assertEquals(project.file("src/main/proto/example.proto").absolutePath, command[6])
    }
}
