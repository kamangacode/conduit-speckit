package com.conduit.architecture;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexagonalArchitectureTest {
    @Test
    void domain_does_not_depend_on_frameworks_explicitly() {
        JavaClasses classes = new ClassFileImporter().importPackages("com.conduit.domain");
        domain_does_not_depend_on_frameworks.check(classes);
    }

    static final ArchRule domain_does_not_depend_on_frameworks = noClasses()
            .that().resideInAnyPackage("com.conduit.domain..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta.persistence..");
}