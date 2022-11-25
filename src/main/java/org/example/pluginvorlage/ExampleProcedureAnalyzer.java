package org.example.pluginvorlage;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyseTriggerEvent;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExampleProcedureAnalyzer implements IProcedureAnalyzer {

    /**
     * Logger for this class.
     * Provides better log output than 'System.out.println()'
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IOnkostarApi onkostarApi;

    @Override
    public OnkostarPluginType getType() {
        return OnkostarPluginType.ANALYZER;
    }

    @Override
    public String getVersion() {
        return "0.0.1";
    }

    @Override
    public String getName() {
        return "Example Procedure Analyzer";
    }

    @Override
    public String getDescription() {
        return "A simple Example Procedure Analyzer";
    }

    @Override
    public boolean isRelevantForDeletedProcedure() {
        return false;
    }

    @Override
    public boolean isRelevantForAnalyzer(Procedure procedure, Disease disease) {
        return true;
    }

    @Override
    public void analyze(Procedure procedure, Disease disease) {
        logger.info("Run 'ExampleProcedureAnalyzer.analyze()'");

        var patient = procedure.getPatient();

        patient.getDiseases().forEach(patientDisease -> {
            var icd10code = patientDisease.getIcd10Code();
            // Example log! Do not use in production - personal information!
            logger.info("Found Disease {} for Patient {}", icd10code, patient.getId());

            // Do something with this data ...
        });
    }

    @Override
    public boolean isSynchronous() {
        return false;
    }

    @Override
    public AnalyzerRequirement getRequirement() {
        return AnalyzerRequirement.ENTRY;
    }

    @Override
    public Set<AnalyseTriggerEvent> getTriggerEvents() {
        return new HashSet<>(
                List.of(
                        AnalyseTriggerEvent.EDIT_SAVE
                )
        );
    }
}
