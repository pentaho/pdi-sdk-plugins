# PDI SDK Samples

### Pre-requisites for building the project:
* Maven, version 3+
* Java JDK 11
* This [settings.xml](https://github.com/pentaho/maven-parent-poms/blob/master/maven-support-files/settings.xml) 
in your <user-home>/.m2 directory

## Overview

This project contains a series of examples demonstrating various use-cases for extended and embedding PDI functionality.

* PDI Transformation Step
* PDI Job Entry
* PDI Database Dialect
* PDI Transformation Hop Partitioner
* Embedded PDI Features
  * Creating Jobs or Transformations in Java
  * Executing Jobs or Transformations in Java
  * Interacting with a Carte Web Server via APIs

## Building

From the main directory, or any of the module sub-directories, run `mvn clean package site`.

For the Step, Job Entry, Database Dialect, and Hop Partitioner samples, a plugin zip file will be generated that can be unzipped into a PDI Client for testing.  Reports are also generated, covering topics such as unit test results, code coverage statistics, and adherence to code style rules.

The examples provided in the embedded module can be run directly from a Java IDE or command-line.

The generating examples needs to have a `plugins` folder on the root folder, containing any plugin jars used, currently there is a need to add
the `pdi-core-plugins-impl.jar` 

```
pdi-sdk-plugins/
├── kettle-sdk-database-plugin
├── kettle-sdk-embedding-samples
├── kettle-sdk-jobentry-plugin
├── kettle-sdk-partitioner-plugin
├── kettle-sdk-plugin-assembly
├── kettle-sdk-step-plugin
└── plugins --> add any plugin's jars needed for embedding samples
```