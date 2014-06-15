# gradle-protoc-plugin

## Status

Beta

## Overview

A Gradle plugin for protoc, the code generator behind Google's Protocol Buffers.

Most Gradle plugins out there don't seem to do a great job of external *protoc*
plugins (e.g. my own [rpckit](http://github.com/thomaslee/protoc-gen-rpckit)).
This  particular Gradle plugin tries to make the interface to third party
protoc language support & plugins just as easy as for built-in languages.

## Usage

### Minimal configuration

    apply plugin: 'java'
    apply plugin: 'protoc'

    buildscript {
        repositories {
            mavenCentral()
        }

        dependencies {
            classpath 'co.tomlee.gradle.plugins:gradle-protoc-plugin:0.0.3'
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        //
        // Choose whatever version is appropriate here
        //
        compile 'com.google.protobuf:protobuf-java:2.5.0'
    }

This will:

* Create a `compileProto` task and hook it up as a dependency of `compileJava`
* Configure `compileProto` to inspect `src/main/proto` and write out Java code
to `src/main/gen-java`.
* Add `src/main/gen-java` to `sourceSets.main.java`
* Create a `cleanProto` task and hook it up as a dependency of `clean`

If you don't use `apply plugin: "java"`, the `compileProto` task will be created
but will not be configured to generate Java code by default.

### If protoc is not on your PATH

This is only necessary if you want to use a `protoc` binary that is not on
your `PATH`:

    protoc {
        executable "/usr/bin/protoc"
    }

### Modifying the protoc include path

    protoc {
        path "path/to/protofiles"
        path "path/to/more/protofiles"
    }

### Using protoc plugins

protoc version 2.3.0+ supports plugins, which is neat:

    compileProto {
        plugins {
            //
            // External protoc plugins are supported too
            //
            some_external_plugin {
                //
                // Optional if the plugin is on your $PATH and looks like `protoc-gen-<name>`
                //
                executable "/usr/local/bin/some-external-protoc-plugin"

                out destinationDir

                //
                // Options will be passed through to the protoc plugin on the command line.
                // Option values will be coerced to strings for the command line.
                //
                option foo: true
                option bar: 123
                option baz: "boz"
            }
        }
    }

### Defining your own ProtobufCompile tasks

    ext.destination = file("build/gen-proto")

    sourceSets.main.java.srcDir destination.path

    task myProtoCompile(type: ProtobufCompile) {
        inputs.files fileTree("src/main/my-proto").include("**/*.proto")

        plugins {
            cpp {
                out destination
            }
            python {
                out destination
            }
        }
    }

## License

MIT

## Support

Please log defects and feature requests using the issue tracker on [github](http://github.com/thomaslee/gradle-protoc-plugin/issues).

## About

gradle-protoc-plugin was written by [Tom Lee](http://tomlee.co).

Follow me on [Twitter](http://www.twitter.com/tglee) or
[LinkedIn](http://au.linkedin.com/pub/thomas-lee/2/386/629).

