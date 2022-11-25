# Onkostar Pluginvorlage

Implementierung eines einfachen Plugins für Onkostar und Spielwiese.

## Voraussetzungen

Installation des JDK 11 oder neuer. Die Datei `onkostar-api-2.8.1.jar` (oder neuer) muss verfügbar sein, da sie hier nicht enthalten ist.

## Build

Kopieren der Datei `onkostar-api-2.8.1.jar` (oder neuer) in das Verzeichnis `libs`.

**_Hinweis_**: Die Maven-Konfiguration referenziert entsprechend dem Beispielplugin die Onkostar-API mit `${project.basedir}/libs/onkostar-api-2.8.1.jar`.
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

## Abweichungen zum Beispielplugin

Dieses Testplugin weicht vom Beispiel im [Kundenbereich ONKOSTAR Erweiterungen](https://confluence.it-choice.de/display/KBOSTARAPI/Entwicklungsumgebung) ab.

Angepasst wurden:

* Nutzung von Maven-Wrapper: Keine lokale Installation von Maven erforderlich.
* Nutzung von SLF4J: Keine Ausgaben mit System.out.println() in den Beispielen.
* Projekt nutzt ausschließlich Maven und keine IDE-spezifischen Konfigurationen - kein spezieller Eclipse-Workspace erforderlich, Nutzung anderer IDE möglich.
* Spring-Konfiguration `moduleContext.xml`: Zur verwendeten Version des Spring-Frameworks passende Schemata und Entfernen von ungenutzten Schemata.
