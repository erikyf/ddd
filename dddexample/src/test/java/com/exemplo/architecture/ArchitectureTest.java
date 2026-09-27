package com.exemplo.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * ArchUnit tests enforcing Clean Architecture and Domain-Driven Design boundaries.
 */
@AnalyzeClasses(packages = "com.exemplo", importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTest {

    @ArchTest
    public static final ArchRule domainMustNotDependOnApplicationOrInfrastructure =
            classes()
                    .that().resideInAPackage("..domain..")
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage("..domain..", "java..");

    @ArchTest
    public static final ArchRule applicationMustNotDependOnInfrastructure =
            classes()
                    .that().resideInAPackage("..application..")
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage("..application..", "..domain..", "java..");

    @ArchTest
    public static final ArchRule verifyLayeredArchitecture =
            layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("Domain").definedBy("..domain..")
                    .layer("Application").definedBy("..application..")
                    .layer("Infrastructure").definedBy("..infrastructure..")
                    .layer("Boot").definedBy("com.exemplo")
                    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Boot")
                    .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure", "Boot")
                    .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Boot");
}
