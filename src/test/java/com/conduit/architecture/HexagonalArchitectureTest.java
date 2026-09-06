package com.conduit.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

class HexagonalArchitectureTest {
  @Test
  void domain_does_not_depend_on_frameworks_explicitly() {
    JavaClasses classes = new ClassFileImporter().importPackages("com.conduit.domain");
    domain_does_not_depend_on_frameworks.check(classes);
  }

  static final ArchRule domain_does_not_depend_on_frameworks =
      noClasses()
          .that()
          .resideInAnyPackage("com.conduit.domain..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.springframework..", "jakarta.persistence..");

  @Test
  void inbound_adapters_reside_under_interfaces() {
    JavaClasses classes = new ClassFileImporter().importPackages("com.conduit");
    classes()
        .that()
        .haveSimpleNameEndingWith("Controller")
        .should()
        .resideInAnyPackage("com.conduit.interfaces..")
        .check(classes);
  }
}
