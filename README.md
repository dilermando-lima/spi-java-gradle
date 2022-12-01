# SPI-JAVA-GRADLE

  * [About this source](#about-this-source)
  * [Understanding SPI](#understanding-spi)
  * [Source's Architecture](#sources-architecture)
  * [Build a fatJar in gradle with all implementation providers](#build-a-fatjar-in-gradle-with-all-implementation-providers)
  * [Build a fatJar in gradle adding only one implementation provider](#build-a-fatjar-in-gradle-adding-only-one-implementation-provider)
  

## About this source
This project is a simple example for SPI ( Java Service Provider Interface ) using implementations as pluginn to be identified on source soon as implemented as dependencie into a specific project

## Understanding SPI

SPI Component | Description
--- | ---
Service Provider Interface | An interface or abstract class to be implmented on providers
Service Provider | A specific implementation of the SPI. The Service Provider contains one or more concrete classes that implement or extend the service type.
Service Loader | Aplication will discovery and load all provider implementations from classpath as they are implemented as dependencies

```bash

  ┌╌ Service Provider 1 <───┐
  ┆                         │
  ├╌ Service Provider 2 <───┼─── Service Provider Interface
  ┆                         │               │
  ├╌ Service Provider 3 <───┘               │
  ┆                                         │
  ┆                                         v 
  └╌╌╌╌╌╌╌ Add as library ╌╌╌╌╌╌╌╌╌╌╌> Service Loader

```

## Source's Architecture

```bash
.
├── settings.gradle # define all gradle project modules
├── build.gradle # config all building and depedencies modules
│
├── implementation-1 # module 1 implements :interface-aggreement
│   ├── resources/META-INF/services/MsgInterface
│   └── src/MsgPlugin1.java
│
├── implementation-2 # module 2 implements :interface-aggreement
│   ├── resources/META-INF/services/MsgInterface
│   └── src/MsgPlugin2.java
│
├── implementation-3 # module 3 implements :interface-aggreement
│   ├── resources/META-INF/services/MsgInterface
│   └── src/MsgPlugin3.java
│
├── interface-aggreement # interface to be implmented
│   └── src/MsgInterface.java
│
└── running-interface # runnable project receives all implementation
    │
    └── src/Run.java # with main method to be run

```

> Note: all source module has `src/main/java` replaced to `src` and all configuration modules are placed in root [./build.gradle](./build.gradle) 

## Build a fatJar in gradle with all implementation providers
If you need to create a jar that contains all providers implementations as dependencies, we need to say to `gradle` for merging all `META-INF/services`

```groovy

jar {
        // merging all /META-INF/services
        doFirst {
            def serviceDir = file("$buildDir/META-INF/services")
            serviceDir.deleteDir()
            serviceDir.mkdirs()
            for(eachFile in configurations.compileClasspath) {
                zipTree(eachFile).matching{ include 'META-INF/services/*' }.each { f ->
                    new File(serviceDir, f.name) << f.getText("UTF-8") + "\n"
                }
            }
        }
        manifest {
            attributes "Main-Class": "Run" // main class name
        }
        // exclude current /META-INF/services
        from(configurations.compileClasspath.collect{ it.isDirectory() ? it : zipTree(it) }) {
            exclude 'META-INF/**'
        }
        // use merged /META-INF/services
        from fileTree(buildDir).matching{ include 'META-INF/services/*' }
    }

```
>  All configuration modules are placed in root [./build.gradle](./build.gradle) 

## Build a fatJar in gradle adding only one implementation provider

If you need to build a jar to add only one service into classpath removing duplicated services use `DuplicatesStrategy.EXCLUDE`

```groovy
jar {
    manifest {
        attributes "Main-Class": "${mainClassName}"
    }
    // zip64=true
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE 
    from { configurations.compileClasspath.collect { it.isDirectory() ? it : zipTree(it) } }
}
```
