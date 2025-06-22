# Local Java Code Assistant


This project is a minimal example of a local code assistant written in Java. It communicates with a local Spirare API model by sending prompts as strings and receiving string responses. The assistant now includes a simple JavaFX based GUI and analyzes Java source using the Spoon framework.


## Features

- Send prompts to a local API and display responses.
- Scan a project directory and save a textual tree diagram to `project_structure.txt`.

- Analyze a Java source file with Spoon to list method signatures.
- Display the last scanned project structure in the UI.
- Generate JUnit test skeletons for a selected class using Spoon.
- Search Java files within a directory for a given keyword.
- Split Java files into individual method chunks using Spoon.

- Built with Java 21 and Maven.

## Building

Run the following command to build the project:

```bash
mvn package
```

## Running

Run the GUI, passing the API endpoint as the first argument (defaults to `http://localhost:8080/api`):

```bash
java -jar target/local-assistant-0.1.0.jar http://localhost:8080/api
```

This is a proof-of-concept and only covers a subset of the desired features.
