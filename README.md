# Onkostar Pluginvorlage

Implementierung eines einfachen Plugins für Onkostar und Spielwiese.

## Voraussetzungen

Installation des JDK 11 oder neuer. Die Datei `onkostar-api-2.11.1.3.jar` (oder neuer) muss verfügbar sein, da sie hier nicht enthalten ist.

## Build

Kopieren der Datei `onkostar-api-2.11.1.3.jar` (oder neuer) in das Verzeichnis `libs`.

**_Hinweis_**: Die Maven-Konfiguration referenziert entsprechend dem Beispielplugin die Onkostar-API mit `${project.basedir}/libs/onkostar-api-2.11.1.3.jar`.
Dies führt bei Ausführung des folgenden Befehls zu einer Warnung, ist aber mit der aktuellen Maven-Version 3.5 durchführbar.

Danach Ausführen des Befehls:

```shell
./mvnw package
```

Das Projekt verwendet Maven-Wrapper (hier: `./mvnw`, für Windows gilt `mvnw.cmd`) als Ergänzung zur Maven-Builddatei `pom.xml`.

## Funktion

Es sind zwei Klassen implementiert:

* `ExampleProcedureAnalyser` zeigt eine Implementierung von `IProcedureAnalyzer`, welche nach jedem Speichern eines Formulars ausgeführt wird.
* `ExampleMethods` zeigt eine Implementierung, basierend auf `AbstractBackendService`, mit einer Funktion zum Aufruf aus einem Script.

Die abstrakte Klasse `AbstractBackendService` implementiert Methoden aus `IProcedureAnalyzer` für den Plugintyp `OnkostarPluginType.BACKEND_SERVICE`.

## Abweichungen zum Beispielplugin

Dieses Testplugin weicht vom Beispiel im [Kundenbereich ONKOSTAR Erweiterungen](https://confluence.it-choice.de/display/KBOSTARAPI/Entwicklungsumgebung) ab.

Angepasst wurden:

* Nutzung von Maven-Wrapper - Keine lokale Installation von Maven erforderlich.
* Projekt nutzt ausschließlich Maven und keine IDE-spezifischen Konfigurationen - Kein spezieller Eclipse-Workspace erforderlich, Nutzung anderer IDE möglich.
* Anpassung und entfernen von ungenutzten Schemata in der Spring-Konfiguration `moduleContext.xml`.
* Nutzung der aktuellsten Version der Onkostar-API.