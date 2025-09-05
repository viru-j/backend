package com.ub19.mcp.server.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.yaml.snakeyaml.Yaml;
import org.springframework.stereotype.Service;

import com.ub19.shared.model.dto.GenerateApiResponse;
import com.ub19.shared.model.dto.OpenApiSkeletonResponse;

/**
 * Parses OpenAPI definitions and generates API skeleton projects.
 */
@Service
public class OpenApiService {

    public OpenApiSkeletonResponse toSkeleton(String openapiYaml) {
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(openapiYaml);
        Map<String, Object> info = (Map<String, Object>) root.getOrDefault("info", Map.of());
        String title = Objects.toString(info.getOrDefault("title", "api"));
        String base = sanitize(title);
        Map<String, Object> paths = (Map<String, Object>) root.get("paths");
        List<String> checklist = new ArrayList<>();
        if (paths == null || paths.isEmpty()) {
            checklist.add("paths: missing");
        } else {
            checklist.add("paths: ok");
        }
        Map<String, String> packages = Map.of(
                "controller", base + ".controller",
                "service", base + ".service",
                "repository", base + ".repo",
                "dto", base + ".dto");
        return new OpenApiSkeletonResponse(checklist, packages);
    }

    public GenerateApiResponse generate(String openapiYaml, String storyMd, String packageBase) {
        try {
            Path workspace = Files.createTempDirectory("api-gen-");
            if (openapiYaml != null && !openapiYaml.isBlank()) {
                generateFromOpenApi(openapiYaml, packageBase, workspace);
            } else {
                Files.createDirectories(workspace);
            }
            List<String> files = Files.walk(workspace)
                    .filter(Files::isRegularFile)
                    .map(workspace::relativize)
                    .map(Path::toString)
                    .toList();
            return new GenerateApiResponse(workspace.toString(), files);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate API", e);
        }
    }

    private void generateFromOpenApi(String openapiYaml, String packageBase, Path workspace) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(openapiYaml);
        Map<String, Object> paths = (Map<String, Object>) root.get("paths");
        Map.Entry<String, Object> entry = paths.entrySet().iterator().next();
        String path = entry.getKey();
        Map<String, Object> methodNode = (Map<String, Object>) entry.getValue();
        Map.Entry<String, Object> opEntry = methodNode.entrySet().iterator().next();
        String http = opEntry.getKey();
        Map<String, Object> opNode = (Map<String, Object>) opEntry.getValue();
        String operationId = Objects.toString(opNode.getOrDefault("operationId", http + path));
        String resource = resourceName(path);
        String classBase = capitalize(resource);

        createPom(workspace);
        Path srcMain = workspace.resolve("src/main/java/")
                .resolve(packageBase.replace('.', '/'));
        Path srcTest = workspace.resolve("src/test/java/")
                .resolve(packageBase.replace('.', '/'));
        Files.createDirectories(srcMain.resolve("controller"));
        Files.createDirectories(srcMain.resolve("service"));
        Files.createDirectories(srcMain.resolve("repo"));
        Files.createDirectories(srcMain.resolve("dto"));
        Files.createDirectories(srcMain.resolve("mapper"));
        Files.createDirectories(srcMain.resolve("model"));
        Files.createDirectories(srcMain.resolve("advice"));
        Files.createDirectories(srcTest.resolve("controller"));

        writeApplication(srcMain, packageBase, classBase);
        writeGlobalExceptionHandler(srcMain.resolve("advice"), packageBase);
        writeEntity(srcMain.resolve("model"), packageBase, classBase);
        writeDto(srcMain.resolve("dto"), packageBase, classBase);
        writeMapper(srcMain.resolve("mapper"), packageBase, classBase);
        writeRepository(srcMain.resolve("repo"), packageBase, classBase);
        writeService(srcMain.resolve("service"), packageBase, classBase);
        writeController(srcMain.resolve("controller"), packageBase, classBase, http, path, operationId);
        writeTest(srcTest.resolve("controller"), packageBase, classBase);
    }

    private void writeApplication(Path src, String pkg, String classBase) throws IOException {
        String content = "package " + pkg + ";\n\n" +
                "import org.springframework.boot.SpringApplication;\n" +
                "import org.springframework.boot.autoconfigure.SpringBootApplication;\n\n" +
                "@SpringBootApplication\n" +
                "public class " + classBase + "Application {\n" +
                "    public static void main(String[] args) {\n" +
                "        SpringApplication.run(" + classBase + "Application.class, args);\n" +
                "    }\n" +
                "}\n";
        Files.writeString(src.resolve(classBase + "Application.java"), content);
    }

    private void writeGlobalExceptionHandler(Path src, String pkg) throws IOException {
        String content = "package " + pkg + ".advice;\n\n" +
                "import org.springframework.http.ResponseEntity;\n" +
                "import org.springframework.web.bind.MethodArgumentNotValidException;\n" +
                "import org.springframework.web.bind.annotation.ExceptionHandler;\n" +
                "import org.springframework.web.bind.annotation.RestControllerAdvice;\n\n" +
                "@RestControllerAdvice\n" +
                "public class GlobalExceptionHandler {\n" +
                "    @ExceptionHandler(MethodArgumentNotValidException.class)\n" +
                "    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {\n" +
                "        return ResponseEntity.badRequest().body(\"validation error\");\n" +
                "    }\n" +
                "}\n";
        Files.writeString(src.resolve("GlobalExceptionHandler.java"), content);
    }

    private void writeEntity(Path src, String pkg, String classBase) throws IOException {
        String content = "package " + pkg + ".model;\n\n" +
                "public class " + classBase + "Entity {\n" +
                "    private int value;\n\n" +
                "    public int getValue() { return value; }\n" +
                "    public void setValue(int value) { this.value = value; }\n" +
                "}\n";
        Files.writeString(src.resolve(classBase + "Entity.java"), content);
    }

    private void writeDto(Path src, String pkg, String classBase) throws IOException {
        String content = "package " + pkg + ".dto;\n\n" +
                "import jakarta.validation.constraints.NotNull;\n\n" +
                "public record " + classBase + "Dto(@NotNull int value) { }\n";
        Files.writeString(src.resolve(classBase + "Dto.java"), content);
    }

    private void writeMapper(Path src, String pkg, String classBase) throws IOException {
        String content = "package " + pkg + ".mapper;\n\n" +
                "import org.mapstruct.Mapper;\n" +
                "import " + pkg + ".dto." + classBase + "Dto;\n" +
                "import " + pkg + ".model." + classBase + "Entity;\n\n" +
                "@Mapper(componentModel = \"spring\")\n" +
                "public interface " + classBase + "Mapper {\n" +
                "    " + classBase + "Dto toDto(" + classBase + "Entity entity);\n" +
                "}\n";
        Files.writeString(src.resolve(classBase + "Mapper.java"), content);
    }

    private void writeRepository(Path src, String pkg, String classBase) throws IOException {
        String content = "package " + pkg + ".repo;\n\n" +
                "import org.springframework.stereotype.Repository;\n" +
                "import " + pkg + ".model." + classBase + "Entity;\n\n" +
                "@Repository\n" +
                "public class " + classBase + "Repository {\n" +
                "    public " + classBase + "Entity find() {\n" +
                "        " + classBase + "Entity e = new " + classBase + "Entity();\n" +
                "        e.setValue(0);\n" +
                "        return e;\n" +
                "    }\n" +
                "}\n";
        Files.writeString(src.resolve(classBase + "Repository.java"), content);
    }

    private void writeService(Path src, String pkg, String classBase) throws IOException {
        String content = "package " + pkg + ".service;\n\n" +
                "import org.springframework.stereotype.Service;\n" +
                "import " + pkg + ".dto." + classBase + "Dto;\n" +
                "import " + pkg + ".mapper." + classBase + "Mapper;\n" +
                "import " + pkg + ".repo." + classBase + "Repository;\n\n" +
                "@Service\n" +
                "public class " + classBase + "Service {\n" +
                "    private final " + classBase + "Repository repo;\n" +
                "    private final " + classBase + "Mapper mapper;\n\n" +
                "    public " + classBase + "Service(" + classBase + "Repository repo, " + classBase + "Mapper mapper) {\n" +
                "        this.repo = repo;\n" +
                "        this.mapper = mapper;\n" +
                "    }\n\n" +
                "    public " + classBase + "Dto " + decapitalize(classBase) + "() {\n" +
                "        return mapper.toDto(repo.find());\n" +
                "    }\n" +
                "}\n";
        Files.writeString(src.resolve(classBase + "Service.java"), content);
    }

    private void writeController(Path src, String pkg, String classBase, String http, String path, String opId) throws IOException {
        String content = "package " + pkg + ".controller;\n\n" +
                "import org.springframework.web.bind.annotation." + http.toUpperCase(Locale.ROOT) + "Mapping;\n" +
                "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.RestController;\n" +
                "import " + pkg + ".dto." + classBase + "Dto;\n" +
                "import " + pkg + ".service." + classBase + "Service;\n\n" +
                "@RestController\n" +
                "@RequestMapping(\"" + path + "\")\n" +
                "public class " + classBase + "Controller {\n" +
                "    private final " + classBase + "Service service;\n\n" +
                "    public " + classBase + "Controller(" + classBase + "Service service) {\n" +
                "        this.service = service;\n" +
                "    }\n\n" +
                "    @" + http.toUpperCase(Locale.ROOT) + "Mapping\n" +
                "    public " + classBase + "Dto " + opId + "() {\n" +
                "        return service." + decapitalize(classBase) + "();\n" +
                "    }\n" +
                "}\n";
        Files.writeString(src.resolve(classBase + "Controller.java"), content);
    }

    private void writeTest(Path src, String pkg, String classBase) throws IOException {
        String content = "package " + pkg + ".controller;\n\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;\n" +
                "import org.springframework.boot.test.mock.mockito.MockBean;\n" +
                "import org.springframework.test.web.servlet.MockMvc;\n" +
                "import " + pkg + ".service." + classBase + "Service;\n" +
                "import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;\n" +
                "import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;\n\n" +
                "@WebMvcTest(" + classBase + "Controller.class)\n" +
                "class " + classBase + "ControllerTest {\n" +
                "    @Autowired MockMvc mvc;\n" +
                "    @MockBean " + classBase + "Service service;\n" +
                "    @Test void ok() throws Exception { mvc.perform(get(\"/limits\")).andExpect(status().isOk()); }\n" +
                "}\n";
        Files.writeString(src.resolve(classBase + "ControllerTest.java"), content);
    }

    private void createPom(Path workspace) throws IOException {
        String pom = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"" +
                " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                " xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">" +
                "<modelVersion>4.0.0</modelVersion>" +
                "<groupId>temp</groupId><artifactId>generated-api</artifactId><version>1.0-SNAPSHOT</version>" +
                "<parent><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-parent</artifactId><version>3.3.2</version></parent>" +
                "<properties><java.version>17</java.version><mapstruct.version>1.5.5.Final</mapstruct.version></properties>" +
                "<dependencies>" +
                "<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>" +
                "<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-validation</artifactId></dependency>" +
                "<dependency><groupId>org.mapstruct</groupId><artifactId>mapstruct</artifactId><version>${mapstruct.version}</version></dependency>" +
                "<dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-test</artifactId><scope>test</scope></dependency>" +
                "</dependencies>" +
                "<build><plugins><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-compiler-plugin</artifactId>" +
                "<configuration><annotationProcessorPaths><path><groupId>org.mapstruct</groupId><artifactId>mapstruct-processor</artifactId><version>${mapstruct.version}</version></path></annotationProcessorPaths></configuration>" +
                "</plugin></plugins></build></project>";
        Files.writeString(workspace.resolve("pom.xml"), pom);
    }

    private String sanitize(String title) {
        String cleaned = title.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
        if (cleaned.isEmpty()) {
            return "api";
        }
        return "com.ub19." + cleaned;
    }

    private String resourceName(String path) {
        String r = path.replaceAll("/", "");
        return r.isEmpty() ? "root" : r;
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }

    private String decapitalize(String s) {
        return s.substring(0, 1).toLowerCase(Locale.ROOT) + s.substring(1);
    }
}

