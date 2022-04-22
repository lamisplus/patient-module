# Patient Module
* Patient module is one of the core module on lamisplus is basically the entry point to the application

## Build

### Prerequisites

#### Java
 
####  Lamisplus is a Java application that is why you need to install Java JDK, the required version is 1.8.

#### Maven
#### Install the build tool [Maven](https://maven.apache.org/install.html)
#### You need to ensure that Maven uses the Java JDK needed for the branch you want to build.

To do so execute

```bash
 mvn -version
```

##### which will tell you what version Maven is using. Refer to the Maven docs if you need to configure Maven.

#### Git
##### Install the version control tool git and clone this repository with

```bash
https://github.com/lamisplus/patient-module.git
```
### Build Command
```bash
cd patient-module
mvn clean package
```

## Deploy
#### Copy the jar file generated in the target folder and install it on the LAMISPLUS base.
###  Congratulation
