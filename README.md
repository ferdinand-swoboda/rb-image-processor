# rb-image-processor
The application is a batch image processor that takes a work image API URL and an output directory path, retrieves the image data and transforms it in a structured set of HTML pages that allows to browse the works' images by camera make and camera model. The set of HTML pages is written to the specified output directory.

The application is written in Java as a Gradle (dependency manager similar to Maven) project. Therefore, at least JDK 1.8 is required. 

## Usage
The project comes with a build/install/bin subfolder that contains ready-to-use start scripts for both Windows and Unix systems.
To execute the application on e.g. Windows run the following command in the aforementioned subfolder:

`rb-image-processor http://take-home-test.herokuapp.com/api/ C:\Users\<username>\Desktop\htmlPages`

## Development
As this is a Gradle project, it can be easily imported as such in the IDE of your choice, preferably IntelliJ IDEA.
The Gradle configuration supports multiple tasks and wraps a self-contained Gradle installation meaning a system installation of Gradle is not required. This means, you can run any Gradle task in your project using the gradlew shell script or bat file located in the root directory.
However a Gradle plugin for your IDE will be needed to execute the Gradle tasks from within the IDE.

From within the project's root directory, you can execute any Gradle task by running

 - `./gradlew <task>` (on Unix-like platforms such as Linux and Mac OS X)
 - `gradlew <task>` (on Windows using the gradlew.bat batch file)

where <task> is replaced with test/build/installDist/clean etc.
Arguments have to be changed accordingly, if necessary.

## Architecture
TODO Things like why you made certain decisions, how you architected your code

## Comments
Java, Gradle and the Retrofit as well as Thymeleaf library were chosen due to familiarity and because they need minimum configuration to solve their tasks at hand.

TODO shortcomings with your code, etc. We generally read this first, and this gives us a good impression of what you were thinking, and helps us gauge your code. 
