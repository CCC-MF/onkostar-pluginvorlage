package org.example.pluginvorlage.moreexamples;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class UnitTestExamplesTest {

    @Mock
    private IOnkostarApi onkostarApi;

    private UnitTestExamples unitTestExamples;

    @BeforeEach
    public void setup() {

        // Gebe vor, was bei Aufruf von Onkostar-API getPatient() passieren soll
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            var patientId = invocation.getArgument(0, Integer.class);

            if (patientId % 2 == 0) {
                // Patient mit gerader ID existiert ...
                var patient = new Patient(onkostarApi);
                patient.setId(patientId);
                return patient;
            } else {
                // ... andere Patienten nicht.
                return null;
            }
        }).when(this.onkostarApi).getPatient(anyInt());

        // Erstelle Objekt für zu testende Klasse
        this.unitTestExamples = new UnitTestExamples(onkostarApi);
    }

    @Test
    void testShouldGetIcd10CodesIfPatientDiseases() {
        // Gebe vor, was bei Aufruf von Onkostar-API getDiseasesByPatientId() passieren soll
        // Wird von Patient.getDiseases() aufgerufen.
        doAnswer(invocation -> {
            var disease1 = new Disease(onkostarApi);
            disease1.setIcd10Code("ICD1");

            var disease2 = new Disease(onkostarApi);
            disease2.setIcd10Code("ICD2");

            return List.of(disease1, disease2);
        }).when(this.onkostarApi).getDiseasesByPatientId(anyInt());

        // Aufruf der Methode 'methodUnderTest'
        var actual = this.unitTestExamples.methodUnderTest(Map.of("patientId", 2));

        // Stelle sicher, dass die korrekte Antwort zurück kommt
        assertThat(actual).contains("ICD1", "ICD2");
    }

    @Test
    void testShouldThrowExceptionForUnknownPatient() {
        // Erwarte eine Exception bei Aufruf ...
        var thrownException = assertThrows(Exception.class, () -> {
            this.unitTestExamples.methodUnderTest(Map.of("patientId", 4711));
        });

        // Stelle sicher, dass die erwartete Exception mit der korrekten Fehlermeldung geworfen wurde
        assertThat(thrownException)
                .isInstanceOf(UnitTestExamples.NoSuchPatientException.class)
                .hasMessage("Kein Patient mit ID 4711");
    }

}
