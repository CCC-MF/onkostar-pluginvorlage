package org.example.pluginvorlage;

import de.itc.onkostar.api.Hl7Message;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IHl7Analyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ExampleHl7Analyzer implements IHl7Analyzer {
    /**
     * Logger for this class.
     * Provides better log output than {@code System.out.println()}'
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
        return "Example HL7 Analyzer";
    }

    @Override
    public String getDescription() {
        return "A simple Example HL7 Analyzer";
    }

    /**
     * This method implements a check if HL7 message is relevant for running {@link #analyze(Hl7Message)}.
     *
     * @param hl7Message The HL7 Message the plugin might analyze. Can be {@code null}.
     * @return True if HL7 message is not null.
     */
    @Override
    public boolean isRelevantForAnalyzer(Hl7Message hl7Message) {
        return null != hl7Message;
    }

    /**
     * This method gets executed each time requirements are met.
     * <ul>
     *     <li>
     *         {@link #isRelevantForAnalyzer(Hl7Message)} must return 'true'.
     *     </li>
     * </ul>
     * <p>
     */
    @Override
    public void analyze(Hl7Message hl7Message) {
        // .. handle HL7 message ...
    }

    @Override
    public boolean isSynchronous() {
        return false;
    }

    @Override
    public AnalyzerRequirement getRequirement() {
        return AnalyzerRequirement.HL7;
    }
}
