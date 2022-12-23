package org.example.pluginvorlage.restexample;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyseTriggerEvent;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class RestExamples implements IProcedureAnalyzer {

    /**
     * Logger for this class.
     * Provides better log output than {@code System.out.println()}'
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestTemplate restTemplate;

    public RestExamples() {
        this.restTemplate = new RestTemplate();
    }

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
     * Returns param "testparam" making roundtrip to HTTPbin.
     * Usage in script:
     *
     * <pre>
     *      executePluginMethod(
     *          'ExampleMethods',
     *          'makeRestCall',
     *          { testparam: 'Testdata' },
     *          function (result) {console.log(result);},
     *          false
     *      );
     * </pre>
     *
     * @param input The input data Map
     * @return The response message - should be equal to given value of 'testparam'.
     */
    public String makeRestCall(Map<String, Object> input) throws IllegalArgumentException {
        var testparam = input.get("testparam");
        if (null == testparam) {
            throw new IllegalArgumentException("Kein Testparameter angegeben!");
        }

        // Erstelle Request-URI mit Query-Parameter "testparam" und Wert "testdata"
        var uri = UriComponentsBuilder
                .fromHttpUrl("https://httpbin.org/anything")
                .queryParam("testparam", "testdata")
                .build()
                .toUri();

        // Testanfrage an "httpbin.org" => Siehe hier: https://httpbin.org/#/Anything
        // Liefert neben anderen Werten auch die Query-Parameter in einem JSON-Dokument zurück
        var response = restTemplate.getForEntity(uri, ExampleResponse.class);

        // Behandlung erfolgreicher Anfrage
        // Hier wird der Wert von "Testparam" aus der Antwort wieder extrahiert und zurück gegeben.
        if (HttpStatus.OK == response.getStatusCode() && response.hasBody()) {
            var arguments = response.getBody().args;
            if (null != arguments) {
                return arguments.get("testparam");
            }
        }

        return "Keine Antwort";
    }

    // Ausschnitt aus der Beispiel-Antwort.
    private static class ExampleResponse {
        public Map<String, String> args;
    }

}
