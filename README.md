# Onkostar Pluginvorlage

Implementierung eines einfachen Plugins für Onkostar und Spielwiese.

## Voraussetzungen

Installation des JDK 11 oder neuer. Die Datei `onkostar-api-2.11.1.1.jar` (oder neuer) muss verfügbar sein, da sie hier nicht enthalten ist.

## Build

Kopieren der Datei `onkostar-api-2.11.1.1.jar` (oder neuer) in das Verzeichnis `libs`.

**_Hinweis_**: Die Maven-Konfiguration referenziert entsprechend dem Beispielplugin die Onkostar-API mit `${project.basedir}/libs/onkostar-api-2.11.1.1.jar`.
Dies führt bei Ausführung des folgenden Befehls zu einer Warnung, ist aber mit der aktuellen Maven-Version 3.5 durchführbar.

Danach Ausführen des Befehls:

```shell
./mvnw package
```

Das Projekt verwendet Maven-Wrapper (hier: `./mvnw`, für Windows gilt `mvnw.cmd`) als Ergänzung zur Maven-Builddatei `pom.xml`.

## Funktion

Es sind zwei Klassen implementiert:

* `ExampleProcedureAnalyser` zeigt eine Implementierung von `IProcedureAnalyzer`, welche nach jedem Speichern eines Formulars ausgeführt wird.
* `ExampleMethods` zeigt eine Implementierung von `IProcedureAnalyzer`, mit einer Funktion zum Aufruf aus einem Script.

Beachten Sie hierzu auch den Beitrag [Wie kann ich festlegen, ob und wann ein Plugin ausgeführt werden soll?](https://confluence.it-choice.de/pages/viewpage.action?pageId=127832868). 

## Abweichungen zum Beispielplugin

Dieses Testplugin weicht vom Beispiel im [Kundenbereich ONKOSTAR Erweiterungen](https://confluence.it-choice.de/display/KBOSTARAPI/Entwicklungsumgebung) ab.

Angepasst wurden:

* Nutzung von Maven-Wrapper: Keine lokale Installation von Maven erforderlich.
* Nutzung von SLF4J: Keine Ausgaben mit System.out.println() in den Beispielen.
* Projekt nutzt ausschließlich Maven und keine IDE-spezifischen Konfigurationen - kein spezieller Eclipse-Workspace erforderlich, Nutzung anderer IDE möglich.
* Spring-Konfiguration `moduleContext.xml`: Zur verwendeten Version des Spring-Frameworks passende Schemata und Entfernen von ungenutzten Schemata.

## Logging

Die Beispiele zeigen Logging mit SLF4J. Eine Logger-Instanz kann folgendermaßen erstellt werden:

```java
class MeinTestplugin implements IProcedureAnalyzer {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Beispiel zur Verwendung
    public void demo() {
        String logDetails = "Das sind weitere Details";
        logger.info("Das ist ein Logeintrag: {}", logDetails);
    }

    // ... restlliche Implementierung
}
```

Mehr zum Logging im Beitrag [Logging in Onkostar](https://confluence.it-choice.de/display/KBOSTARAPI/Logging+in+Onkostar).

## REST- oder sonstige Web-Anfragen in einem Plugin

Die Klasse `RestExamples` zeigt eine Anfrage an einen externen Dienst. Hier wird [**httpbin.org**](https://httpbin.org/) zum Darstellen einer Antwort verwendet.
Es handelt sich hierbei um einen einfachen HTTP-Anfrage- und Antwort-Dienst.

Dort wird [Anything](https://httpbin.org/#/Anything) als Beispielantwort verwendet. Hierbei werden neben anderen Werten auch die Query-Parameter der Anfrage zurück gegeben.

Für die Verwendung der Klasse `RestTemplate` ist folgende Abhängigkeit - hier passend für Version 2.11.1.x von Onkostar - in der Datei `pom.xml` eines eigenen Plugins einzufügen, um die in Onkostar verwendeten Klassen für Webanfragen bekannt zu machen.

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>4.3.8.RELEASE</version>
    <scope>provided</scope>
</dependency>
```

Mehr zu diesem Thema im Beitrag [REST- oder sonstige Web-Anfragen in einem Plugin](https://confluence.it-choice.de/display/KBOSTARAPI/REST-+oder+sonstige+Webanfragen+in+einem+Plugin)

## Statische Webressourcen

Durch Konfiguration in der Datei `moduleContext.xml` ist es möglich, statische Webressourcen wie JavaScript-Dateien,
aber auch Grafiken und andere Inhalte, in ein Plugin einzubinden.

Folgendes Beispiel zeigt die Konfiguration für den Fall, dass alle Inhalte des Verzeichnisses [`src/main/resources/examples`](`src/main/resources/examples`)
innerhalb des Webpfades `.../app/examples` abrufbar sind. Der Pfad ist dabei relativ zum Contextpfad von Onkostar, also in der Regel
`/onkostar/app/examples`. Eine Datei `Examples.js` kann entsprechend über `/onkostar/app/examples/Example.js` abgerufen werden.

```xml
<mvc:resources mapping="/app/examples/**" location="classpath:/examples/" />
```

Dies ermöglicht, das Einbinden weiterer JavaScript-Funktionalität in Onkostar über ein Plugin.
Hierzu muss in einem Formular zunächst das Script abgerufen werden und im Anschluss die zugehörige _ExtJS_-Klasse verwendet werden.

```javascript
// Laden über den Pfad der Datei (ohne '/onkostar'), mit Punkten anstelle '/' und ohne abschließendes '.js'.
Ext.syncRequire('app.examples.Example', () => {
    // Verweis auf die ExtJS-Klasse
    let Example = Ext.ClassManager.get('Example');

    // Wenn `Example === null`, dann Abbruch
    if (Example === null) {
        return;
    }
    
    // Gibt "Hallo, Welt!" auf der Webkonsole aus.
    Example.exampleFunc();
});
```

## Beispiel mit Spring ComponentScan

Onkostar verwendet das Spring Framework. Es ist dadurch möglich, einen ComponentScan auszuführen.
Damit entfällt die Angabe jedes zu erstellenden Beans mit Klasse und ID in der Datei `moduleContext.xml`, 
erfordert aber die Angabe, welches Java-Paket nach Componenten durchsucht werden soll.

```xml
<context:component-scan base-package="org.example.pluginvorlage.componentscan" />
```

Es werden nun alle Klasse, die mit der Java-Annotation `@Component` ausgezeichnet sind (oder davon abgeleiteten Annotationen wie `@Service` etc.) 
als zu initialisierendes Bean erkannt und entsprechende Beans auch ohne explizite Angabe in der XML-Datei erstellt.

Als ID des Beans wird dabei der Name der Klasse mit kleinem ersten Buchstaben verwendet.

Eine Angabe von zu referenzierenden Beans um diese über den Konstruktor (Vergleiche Beispiel mit UnitTest) einzufügen ist
dabei nicht erforderlich, da diese automatisch ermittelt und eingefügt werden.

## Beispiel mit UnitTests 

Nicht immer ist es einfach, das Verhalten von Plugins sicherzustellen. Hierbei können UnitTests helfen.
Hierbei werden einzelne Einheiten (Klassen und Methoden) eines Plugins getrennt von anderen Teilen der Software (hier: Onkostar) getestet. 

Ein einfaches Beispiel zur Verwendung von UnitTests mit JUnit5 und Mockito befindet sich in [`src/main/java/testexamples/UnitTestExamples.java`](src/main/java/testexamples/UnitTestExamples.java) 
mit entsprechenden Tests in [`src/test/java/testexamples/UnitTestExamplesTest.java`](src/test/java/testexamples/UnitTestExamplesTest.java).

Dieses Beispiel zeigt auch, wie das Verhalten der Onkostar-API für einen Test vorgegeben werden kann und nur die Beispiel-Methode getestet wird. 
