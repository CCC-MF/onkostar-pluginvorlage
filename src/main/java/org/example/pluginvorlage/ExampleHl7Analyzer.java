package org.example.pluginvorlage;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.v26.segment.PID;
import ca.uhn.hl7v2.model.v26.segment.PV1;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;
import de.itc.onkostar.api.Hl7Message;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IHl7Analyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.Executors;

public class ExampleHl7Analyzer implements IHl7Analyzer {
    /**
     * Logger for this class.
     * Provides better log output than {@code System.out.println()}'
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String PERSONENSTAMM_ZUORDNUNG_KONFIGURATION = "Zuordnung_FA_Personenstamm";
    public static final String PERSONENSTAMM_HL7 = "PersonenstammHL7";

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
    public void analyze(final Hl7Message hl7) {

        try {
            Message geparsteNachricht = parseMessage(hl7);
            if (geparsteNachricht == null) {
                return;
            }

            HL7VersionEnum hl7VersionEnum = HL7VersionEnum.getHl7Version(hl7.getHl7Version());

            // PID-Segment aus der geparsten Nachricht ermitteln und daraus die PID
            PID patientendaten = getPatient(hl7VersionEnum, geparsteNachricht);
            if (patientendaten == null || patientendaten.getPatientIDInternalID().length == 0) {return;}

            String patientId = patientendaten.getPatientIDInternalID(0).getID().getValue();

            //Name des Personenstamms, in dem die HL7 Patienten aufgenommen werden und Personenstammzuordnung aus der Einstellung lesen
            String personenstammHL7 = onkostarApi.getGlobalSetting(PERSONENSTAMM_HL7);
            String settingPersZuordnung = onkostarApi.getGlobalSetting(PERSONENSTAMM_ZUORDNUNG_KONFIGURATION);

            if (settingPersZuordnung.isEmpty() || personenstammHL7.isEmpty()) {return;}

            JSONObject zuordnung = new JSONObject(settingPersZuordnung);

            //Patient aus dem Quellstamm, der in anderen Personenstamm kopiert werden soll
            Patient patient_kis = onkostarApi.getPatient(patientId, personenstammHL7);
            if (patient_kis == null) {return;}

            //Auslesen der Fachabteilung aus der HL7 Nachricht und auslesen des Zielstamms aus der Konfiguration
            String Fachabteilung = getFachabteilung(hl7VersionEnum,geparsteNachricht);
            String zielPersonenstamm = zuordnung.getString(Fachabteilung);
            if (StringUtils.isBlank(zielPersonenstamm)) {
                logger.info("Es ist kein Zielpersonenstamm für die Fachabteilung: '" + Fachabteilung + "' konfiguriert");
                return;
            }

            //Kopie des Patienten in den entsprechenden Zielpersonenstamm
            onkostarApi.addPatientToPool(patient_kis.getId(), zielPersonenstamm);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * Wandelt die String Darstellung der HL7 Nachricht in ein HL7 Message Objekt
     * um.
     *
     * @param hl7
     * @return
     */
    private Message parseMessage(final Hl7Message hl7) {

        HapiContext context = new DefaultHapiContext(Executors.newCachedThreadPool());
        CanonicalModelClassFactory mcf = new CanonicalModelClassFactory(hl7.getHl7Version());
        context.setModelClassFactory(mcf);
        context.setValidationContext(new MyValidationContext());
        PipeParser p = context.getPipeParser();
        context.getExecutorService();

        Message message = null;

        String hl7Message = hl7.getMessage().replace('\n', '\r');

        try {
            message = p.parse(hl7Message);
        } catch (HL7Exception e) {
            // TODO Auto-generated catch block
            logger.error("Fehler beim Parsen der Nachricht: " + e.getMessage());
        }

        try {
            context.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return message;
    }

    /**
     * Versucht aus dem HL7 Message Objekt das PID Segment zu lesen
     *
     * @param hl7Version
     *     Version
     * @param message
     *     Message
     * @return PID
     */

    private PID getPatient(final HL7VersionEnum hl7Version, final Message message) {
        if (message == null) {
            return null;
        }
        try {
            Structure pidStructure = message.get("PID");
            switch (hl7Version) {
                case V2_3:
                    return Wrapper2_3.wrap((ca.uhn.hl7v2.model.v23.segment.PID) pidStructure);
                case V2_4:
                    return Wrapper2_4.wrap((ca.uhn.hl7v2.model.v24.segment.PID) pidStructure);
                case V2_5:
                    return Wrapper2_5.wrap((ca.uhn.hl7v2.model.v25.segment.PID) pidStructure);
                case V2_6:
                    return Wrapper2_6.wrap((ca.uhn.hl7v2.model.v26.segment.PID) pidStructure);
            }
        } catch (HL7Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Versucht aus dem HL7 Message Objekt, speziell aus dem PV1 Segment, die Fachabteilung zu lesen
     *
     * @param hl7Version
     *     Version
     * @param message
     *     Message
     * @return Fachabteilung
     */
    private String getFachabteilung(final HL7VersionEnum hl7Version, final Message message) {
        if (message == null) {
            return null;
        }
        try {
            Structure pv1Structure = message.get("PV1");
            PV1 fa = null;
            switch (hl7Version) {
                case V2_3:
                    fa = Wrapper2_3.wrap((ca.uhn.hl7v2.model.v23.segment.PV1) pv1Structure);
                    break;
                case V2_4:
                    fa = Wrapper2_4.wrap((ca.uhn.hl7v2.model.v24.segment.PV1) pv1Structure);
                    break;
                case V2_5:
                    fa = Wrapper2_5.wrap((ca.uhn.hl7v2.model.v25.segment.PV1) pv1Structure);
                    break;
                case V2_6:
                    fa = Wrapper2_6.wrap((ca.uhn.hl7v2.model.v26.segment.PV1) pv1Structure);
                    break;
            }

            if (fa == null) {
                return "";
            }
            return fa.getAssignedPatientLocation().getFacility().getNamespaceID().getValue();
        } catch (HL7Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
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
