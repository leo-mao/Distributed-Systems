# Übung 2
## Programmieraufgabe: Marshalling
### 1.1 Flache Struktur

Erstellen Sie eine Klasse (in Java, gern auch in einer anderen Sprache) mit diesen Attributen:
* Personenname
* Geburtsdatum
* 32-Bit Ganzzahl ohne Vorzeichen
Setzen Sie die Attribute auf sinnvolleWerte. Erstellen Sie nun eine Methode **toByteArray** (externe Darstellung), welche die Attribute in einen binaren Byte-Strom serialisiert. Erstellen Sie eine korrespondierende Methode **fromByteArray** und testen Sie auf fehlerfreie Übertragung von einem Objekt zu einem anderen mittels eines Byte Arrays. Verwenden Sie nicht die Java- Serialisierungs-mechanismen,sondern programmieren Sie es "zu Fuß". Verwenden Sie zur Binärdarstellung des Personennamens einen sinnvollen Zeichensatz und big endian Byte-Reihenfolge fur den Integer-Wert. Vergessen Sie die Zeitzone nicht und wahlen Sie eine gängige eindeutige Darstellung fur das Datumsfeld.

### 1.2 Transiente AttributeErweitern Sie die Klasse um eine transientes Attribut (zum Beispiel ein View-Objekt oder ein Logger-Objekt). Sorgen Sie fur die korrekte Behandlung dieses Attributs beim Marshalling bzw. beim Demarshalling.

###1.3 All input is evilPrüfen Sie eine eingehende Ausprägung der externen Darstellung vor dem Interpretieren beimUnmashalling auf Konsistenz. Schauen Sie sich dazu [OWASP Data Validation](https://www.owasp.org/index.php/Data_Validation) und [WikipediaData Validation](http://en.wikipedia.org/wiki/Data_validation) an. Diskutieren Sie vor allem notwendige Manahmen, wenn es sich um einTextformat statt eines Binärformats handelte.

##2 Server-Ressourcen-ZuordnungDie Kapazität eines Server-Systems soll erhöht werden, indem eingehende Anfragen auf verschiedene Server-Instanzen verteilt werden. Das Verteilungskriterium fur die Anfragen ist eine Ressourcen-ID (ein String); es gibt drei Server mit den Namen ***s1.example.com*** ... ***s3.example.com***.

###2.1 Modulus-AnsatzLassen Sie ihr Programm die unten angegebenen Ressourcen-IDs auf die Server verteilen, indemSie einen den Modulus-Ansatz verwenden:

Resource-ID  | Zuständiger Server
------------- | -------------
intro.pdf  | Content Cell
index.html	|d41d8cd98f00b204e9800998ecf8427e |0cc175b9c0f1b6a831c399e269772661 |900150983cd24fb0d6963f7d28e17f72 |f96b697d7cb7938d525a2f31aaf161d0 |c3fcd3d76192e4007dfb496cca67e13b |c0008dfc-b5c6-4ac8-9b96-c1780084109b |fde20f54-6d1f-45a8-baf0-82d20b6a0c62 |10af8b96-bf4a-4567-9cf4-56646cfefb23 |cb4c2299-be2b-4673-ae76-0e98d1539833 |c01c429b-5a55-4930-be48-8b0e2ae4db5d | 

Frage: Was passiert, wenn Sie nun ***s3.example.com*** aus der Liste entfernen?

##2.2 HRW-AnsatzImplementieren Sie Highest Random Weight (nehmen Sie eine bereits existierende Hash-Funktion)und lassen die Ressourcen-IDs auf ***s1.example.com ... s3.example.com*** verteilen.
Frage: Was passiert, wenn Sie nun ***s3.example.com*** aus der Liste entfernen?
Lassen Sie nun einige Hundert verschiedene Dokumenten-IDs von HRW auf die Server verteilen.
Frage: Ergibt sich eine gleichmaßige Verteilung?
Frage: Was passiert, wenn Sie nun ***s4.example.com*** zu der Serverliste hinzufugen? Wie groß istder Anteil der Ressourcen-IDs, die umverteilt werden?