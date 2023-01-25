package org.example.pluginvorlage.componentscan;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyseTriggerEvent;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ComponentScanExample implements IProcedureAnalyzer {

    /**
     * Logger for this class.
     * Provides better log output than {@code System.out.println()}'
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IOnkostarApi onkostarApi;

    public ComponentScanExample(IOnkostarApi onkostarApi) {
        this.onkostarApi = onkostarApi;
    }

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
        return AnalyzerRequirement.ENTRY;
    }

    /**
     * This method implements a check if {@link #analyze(Procedure, Disease)} should be run for
     * a deleted {@link Procedure}
     * <p>
     * This method is deprecated in {@link IProcedureAnalyzer} and should be removed later.
     *
     * @return In this example always false
     */
    @Override
    public boolean isRelevantForDeletedProcedure() {
        return false;
    }


    /**
     * This method implements a check if procedure or disease is relevant for running {@link #analyze(Procedure, Disease)}.
     *
     * @param procedure The procedure the plugin might analyze. Can be {@code null}.
     * @param disease   The disease thie plugin might analyze. Can be {@code null}.
     * @return True if plugin handles a procedure.
     */
    @Override
    public boolean isRelevantForAnalyzer(Procedure procedure, Disease disease) {
        return true;
    }

    /**
     * This method gets executed each time requirements are met.
     * <ul>
     *     <li>
     *         {@link #isRelevantForDeletedProcedure()} must return 'true'.
     *     </li>
     *     <li>
     *         {@link #isRelevantForAnalyzer(Procedure, Disease)} must return 'true'.
     *     </li>
     *     <li>
     *         {@link #getTriggerEvents()} must contain matching {@link AnalyseTriggerEvent}.
     *         This method - if not implemented - defaults to all {@link AnalyseTriggerEvent}s.
     *     </li>
     * </ul>
     * <p>
     */
    @Override
    public void analyze(Procedure procedure, Disease disease) {
        // ... do something with procedure ...
        try {
            onkostarApi.saveProcedure(procedure, true);
        } catch (Exception e) {
            throw new RuntimeException("Prozedur konnte nicht gespeichert werden.'");
        }
    }

}
