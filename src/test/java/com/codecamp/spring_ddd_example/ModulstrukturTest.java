package com.codecamp.spring_ddd_example;

import com.codecamp.spring_ddd_example.platzstruktur.api.exposed.SitzGesperrtEvent;
import net.sourceforge.plantuml.SourceStringReader;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.docs.Documenter.DiagramOptions;
import org.springframework.modulith.docs.Documenter.DiagramOptions.DiagramStyle;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ModulstrukturTest {

    @Test
    void verifiesModulStruktur() {
        ApplicationModules modules = ApplicationModules.of(SpringDddExampleApp.class);
        System.out.println(modules);
        modules.verify();
    }

    @Test
    void erstelltModulDiagramme() throws IOException {
        ApplicationModules modules = ApplicationModules.of(SpringDddExampleApp.class);
        DiagramOptions options = DiagramOptions.defaults().withStyle(DiagramStyle.UML);
        new Documenter(modules)
                .writeModulesAsPlantUml(options)
                .writeIndividualModulesAsPlantUml(options);

        Path docsDir = Path.of("target/spring-modulith-docs");
        try (Stream<Path> pumlFiles = Files.find(docsDir, 1, (path, attr) -> path.toString().endsWith(".puml"))) {
            pumlFiles.forEach(pumlFile -> renderToPng(pumlFile));
        }
    }

    private void renderToPng(Path pumlFile) {
        Path pngFile = pumlFile.resolveSibling(pumlFile.getFileName().toString().replace(".puml", ".png"));
        try {
            String content = Files.readString(pumlFile);
            String contentWithSmetana = content.replace("@startuml", "@startuml\n!pragma layout smetana");
            try (OutputStream os = Files.newOutputStream(pngFile)) {
                new SourceStringReader(contentWithSmetana).outputImage(os);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void sitzGesperrtEventIstAusPlatzstrukturExposed() {
        ApplicationModules modules = ApplicationModules.of(SpringDddExampleApp.class);

        ApplicationModule platzstruktur = modules.getModuleByName("platzstruktur").orElseThrow();
        ApplicationModule eventModule = modules.getModuleByType(SitzGesperrtEvent.class).orElseThrow();

        assertThat(eventModule.getIdentifier()).isEqualTo(platzstruktur.getIdentifier());
        assertThat(platzstruktur.isExposed(SitzGesperrtEvent.class)).isTrue();
    }

}
