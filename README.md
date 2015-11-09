# ShefRobot
Wrapper classes for Java code to interact with Lego EV3 robots.

Developed at the University of Sheffield, for use within the [COM1003: Java Programming](http://www.dcs.shef.ac.uk/intranet/teaching/public/modules/level1/com1003.html) module.

##Dependencies
Being a Java project, it is necessary to have the [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) setup on the machines wishing to build and use the code within this repository. The code been developed under Java version 8, however it is likely to work under any modern version.

In order to compile and run the code within this repository it is necessary to have the follow libraries on the classpath:

* [bluecove-2.1.0.jar](http://sourceforge.net/projects/bluecove/files/BlueCove/2.1.0/)
* [ev3classes.jar](http://sourceforge.net/projects/ev3.lejos.p/files/0.9.0-beta/) (Available within the lib dir of the beta_win32.zip or beta.tar.gz downloads)
* [dbusJava](http://www.freedesktop.org/wiki/Software/DBusBindings/#java) (Prebuilt version available with ev3classes.jar download)

The implementation has only been tested with the above specified versions.

##Build
The below sections explain how to build a jar and the documentation for this project, due to the cross-platform nature of Java these commands should be identical between operating systems.

###Library
To build the library, execute the below two commands from this directory.

```bat
mkdir build
javac -d build ShefRobot/*.java
```

This will build the library to the new `build` directory, the contents of this directory can be copied to your working directory to use the API. Alternatively you can follow the below section to create a .jar to add to your classpath.

If this has failed to run, you need to add the above dependencies to your classpath. This can be done temporarily by adding the parameter `-cp ".;[path to bluecove.jar];[path to ev3classes.jar];[path to dbusJava.jar];"` to the build command and replacing the square brackets with the correct paths.

###Jar
If you have built the library using the above command, so that it resides within the `build` directory, the below command will package it into ShefRobot.jar, so that you can add it to your classpath.

To build the .jar, execute the below command from this directory.

```bat
jar -cf ShefRobot.jar -C build ShefRobot
```

###Docs

Documentation for the library can be found on the [repositories GitHub pages](http://ramsay-t.github.io/ShefRobot/).

If you would prefer to generate your own local copy, call the below command from this directory.

```bat
javadoc -d docs -public -link http://docs.oracle.com/javase/8/docs/api/ -link http://www.lejos.org/ev3/docs/ -subpackages ShefRobot -windowtitle "ShefRobot API" -overview "ShefRobot/overview.html"
```

##License
TODO
