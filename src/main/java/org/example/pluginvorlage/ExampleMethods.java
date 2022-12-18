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

import java.util.Map;

public class ExampleMethods implements IProcedureAnalyzer {

    /**
     * Logger for this class.
     * Provides better log output than {@code System.out.println()}'
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
     * In this example {@link Procedure} always false - {@link #analyze(Procedure, Disease)} will never be run.
     *
     * @param procedure The procedure the plugin might analyze. Can be {@code null}.
     * @param disease   The disease thie plugin might analyze. Can be {@code null}.
     * @return True if plugin handles a procedure.
     */
    @Override
    public boolean isRelevantForAnalyzer(Procedure procedure, Disease disease) {
        return false;
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
     * In this example, this method will never be called since this class should only provide a method to be called
     * from frontend.
     */
    @Override
    public void analyze(Procedure procedure, Disease disease) {
        // Nothing to do - should never be called
    }

    /**
     * Return Hello Message for given name and log method call.
     * Usage in script:
     *
     * <pre>
     *      executePluginMethod(
     *          'ExampleMethods',
     *          'getHelloMessage',
     *          { name: 'World' },
     *          function (result) {console.log(result);},
     *          false
     *      );
     * </pre>
     *
     * @param data The data Map
     * @return The hello message
     */
    public String getHelloMessage(Map<String, Object> data) {
        var name = data.get("name");
        logger.info("Called 'getHelloMessage' with name '{}'", name);
        return String.format("Hello %s", name);
    }
}
