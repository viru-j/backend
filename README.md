# Local Java Code Assistant

This project is a minimal example of a local code assistant written in Java. It communicates with a local Spirare API model by sending prompts as strings and receiving string responses. The assistant includes a simple Swing-based GUI and a project structure analyzer.

## Features

- Send prompts to a local API and display responses.
- Scan a project directory and save a textual tree diagram to `project_structure.txt`.
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

This is a proof-of-concept and does not implement all features described in the requirements.
