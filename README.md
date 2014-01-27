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

    //
    // This bit is completely optional
    //
    protoc {
        executable "/usr/bin/protoc"
    }

    task compileProto(type: ProtobufCompile) {
        inputs.files fileTree("src/main/proto").include("**/*.proto")

        //
        // You can meddle with the proto_path like so
        //
        path file('src/main/proto')

        plugins {
            //
            // Specify protoc plugins here, including builtins like java, cpp & python.
            //
            java {
                //
                // Output directory can be specified on a per-plugin basis.
                // Default output directory is src/main/${plugin.name}
                //
                out file("src/main/java")
            }

            //
            // External protoc plugins are supported too
            //
            some_external_plugin {
                executable "/usr/local/bin/some-external-protoc-plugin"

                out file("src/main/java")

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

    compileJava.dependsOn compileProto
    compileJava.source compileProto.outputs.dir

## License

MIT

## Support

Please log defects and feature requests using the issue tracker on [github](http://github.com/thomaslee/gradle-protoc-plugin/issues).

## About

gradle-protoc-plugin was written by [Tom Lee](http://tomlee.co).

Follow me on [Twitter](http://www.twitter.com/tglee) or
[LinkedIn](http://au.linkedin.com/pub/thomas-lee/2/386/629).

