package org.example.pluginvorlage;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ExampleMethods implements IProcedureAnalyzer {

    @Autowired
    private IOnkostarApi onkostarApi;

    @Override
    public OnkostarPluginType getType() {
        return OnkostarPluginType.BACKEND_SERVICE;
    }

    @Override
    public String getVersion() {
        return "0.0.1";
    }

    @Override
    public String getName() {
        return "Example Methods";
    }

    @Override
    public String getDescription() {
        return "Simple Example Methods";
    }

    @Override
    public boolean isSynchronous() {
        return false;
    }

    @Override
    public AnalyzerRequirement getRequirement() {
        return AnalyzerRequirement.PROCEDURE;
    }

    @Override
    public boolean isRelevantForDeletedProcedure() {
        return false;
    }

    @Override
    public boolean isRelevantForAnalyzer(Procedure procedure, Disease disease) {
        return false;
    }

    @Override
    public void analyze(Procedure procedure, Disease disease) {
        // Nothing to do
    }

    /**
     * Get list with all ICD-10 codes for patient with given ID.
     * Usage in script:
     *
     * <pre>
     *      executePluginMethod(
     *          'ExampleMethods',
     *          'getPatientIcd10Codes',
     *          'Dummy1234',
     *          function (result) {console.log(result);},
     *          false
     *      );
     * </pre>
     *
     * @param patientId The ID of the patient
     * @return List with ICD-10 codes
     */
    public List<String> getPatientIcd10Codes(String patientId) {
        var result = new ArrayList<String>();
        var patient = onkostarApi.getPatient(patientId);
        if (null == patient) {
            return new ArrayList<>();
        }
        patient.getDiseases().forEach(disease -> result.add(disease.getIcd10Code()));
        return result;
    }

}
