package fr.dojo.virements.exposition.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import fr.dojo.virements.application.transfer.port.in.TransferUseCase;
import fr.dojo.virements.application.transfer.port.out.TransferRepository;
import fr.dojo.virements.exposition.api.TransferController;
import fr.dojo.virements.infra.jpa.transfer.JpaTransferRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferLookupArchitectureTest {
    private static final String BASE = "fr.dojo.virements";
    private static final JavaClasses CODE = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE);

    @Test
    void controllerDelegatesLookupToTheInputPort() {
        noClasses().that().resideInAPackage(BASE + ".exposition.api..")
                .should().dependOnClassesThat().resideInAnyPackage(BASE + ".infra..", "org.springframework.data..")
                .check(CODE);
        assertTrue(CODE.get(TransferController.class).getMethod("getTransfer", UUID.class)
                .getMethodCallsFromSelf().stream()
                .anyMatch(call -> call.getTargetOwner().isEquivalentTo(TransferUseCase.class)
                        && call.getTarget().getName().equals("getTransfer")));
    }

    @Test
    void applicationLooksUpTheTransferThroughTheOutputPort() {
        noClasses().that().resideInAPackage(BASE + ".application..")
                .should().dependOnClassesThat().resideInAnyPackage(BASE + ".infra..", BASE + ".exposition..")
                .check(CODE);
    }

    @Test
    void domainIsIndependentOfFrameworksAndOuterModules() {
        noClasses().that().resideInAPackage(BASE + ".domaine..").should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "jakarta.servlet..",
                        BASE + ".application..", BASE + ".infra..", BASE + ".exposition..")
                .check(CODE);
    }

    @Test
    void jpaAdapterImplementsTheOutputPort() {
        classes().that().haveFullyQualifiedName(JpaTransferRepository.class.getName())
                .should().implement(TransferRepository.class).check(CODE);
    }
}
