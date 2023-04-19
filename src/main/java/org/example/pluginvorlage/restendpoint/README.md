# Beispiel zur Implementierung eines REST-Endpunkts in einem Onkostar-Plugin

**Achtung!** Diese Vorgehensweise ist nicht offiziell für Onkostar dokumentiert.

## (Optionale) Konfiguration von HTTP-Basic zur Authentisierung

Damit Sie den REST-Endpunkt ohne Login über das Onkostar-Login-Formular verwenden können, ist es möglich,
Benutzername und Passwort mit der Anfrage (Request) in einem HTTP-Header mitzusenden. Siehe auch: https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication#basic_authentication_scheme

Auch das Loginformular überträgt dabei Benutzername und Passwort, allerdings im Inhalt der Anfrage und nicht im HTTP-Header. 

Damit HTTP-Basic für einen Endpunkt nutzbar ist, muss dies in der Datei `moduleContext.xml` konfiguriert werden.

```
<security:http pattern="/restendpoint/**">
    <security:csrf disabled="true" />
    <security:http-basic />
    <security:intercept-url pattern="/**" access="isAuthenticated()" />
</decurity:http>
```

Hierbei wird zunächst festgelegt, dass diese Konfiguration immer auf den (Teil-)Pfad `/restendpoint` und darunter liegende Abschnitte anzuwenden ist.

Sollten Sie Onkostar lokal installiert haben, wäre dies zum Beispiel `http://localhost:8080/onkostar/restendpoint` und alle dahinter liegenden Pfadabschnitte.

Die automatische Verwendung von CSRF wird abgeschaltet. Damit würde ansonsten für jede Anfrage ein einmaliges Token generiert werden,
welches in der nächsten Anfrage zurückgesendet werden muss. Dies ist in Onkostar nicht der Fall.

Die nächste Zeile aktiviert die Nutzung von HTTP-Basic - hier nur für den (Teil-)Pfad `/restendpoint` (und folgend).

Die letzte Zeile gibt an, dass alle Zugriffe abgefangen werden und der Benutzer einen gültigen Benutzernamen und Passwort mit senden muss.

Ohne diese Konfiguration kann der zu erstellende REST-Endpunkt nur im gleichen Browser nach Login über das Login-Formular verwendet werden.

## Anlegen eines einfachen REST-Endpunkts

Verwenden Sie folgende Konfiguration in der Datei `moduleContext.xml`, um die Spring-Komponenten in diesem Paket automatisch zu ermitteln.
```
<context:component-scan base-package="org.example.pluginvorlage.restendpoint" />
```

Im Anschluss werden alle Klasse, welche mit der Annotation `@RestController` versehen sind, automatisch beim Start von Onkostar konfiguriert
und als Spring-Bean instanziiert. Sie sind also verfügbar und reagieren auf Anfragen.

Sie können dabei sowohl `GET`, `POST`, `PUT` als auch `DELETE`-Anfragen entgegen nehmen.

Zum Festlegen, welche Methode auf welche Adresse und auf welche Art von Anfrage reagieren soll, können Sie die Annotation
`@RequestMapping` beziehungsweise `@GetMapping` usw. verwenden. Hierbei können Sie jeweils den Pfad, als auch den Inhaltstyp der Anfrage und Antwort festlegen. Im Pfad sind auch "Platzhalter" möglich, um sich ändernde IDs abbilden zu können.

Eine kurze Einführung dazu gibt es auch hier: https://spring.io/guides/gs/rest-service/

Das zugehörige Beispiel in der Klasse `DemoRestController` zeigt eine Methode, die z.B. auf eine Anfrage an `http://localhost:8080/onkostar/restendpoint/procedures/1234/formname` reagiert, die Prozedur lädt und, wenn eine prozedur mit ID 1234 existiert,
den Formularnamen der Prozedur zurückgibt.


