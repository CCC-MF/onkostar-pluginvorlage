package org.example.pluginvorlage;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
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
     * Provides better log output than 'System.out.println()'
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
        return AnalyzerRequirement.ENTRY;
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
