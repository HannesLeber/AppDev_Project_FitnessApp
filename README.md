# Fitness App
In unserem Projekt geht es darum, eine Fitness-App zu entwickeln, mit der Kraft- und Ausdauertrainingseinheiten sowie die täglich zurückgelegten Schritte getrackt werden können. 
Die App teilt sich ab dem Home-Bildschirm in diese drei Teile auf.

## Krafttraining
Beim Krafttraining können Trainingseinheiten und Übungen angelegt werden. 
Eine Trainingseinheit besteht aus mehreren Übungen, die absolviert werden. 
Eine Übung widerum beinhaltet mehrere Sätze, wobei Aufwärmsätze per Click grün markiert werden können.
Trainingseinheiten mit bestimmten Übungen können außerdem als Templates gespeichert werden.
Wenn man bei der Erstellung einer neuen Trainingseinheit ein Template auswählt, werden die zugehörigen Übungen automatisch hinzugefügt. So kann man bekannte Trainingsroutinen einfach wiederholen.
Bei jeder Übung wird außerdem die PR ("Persönliche Bestleistung" / "Personal Best") angezeigt, gemessen am höchsten Gewicht, das bei der Übung geschafft wurde.

## Ausdauertraining

## Schritte
Die Schritte werden über den im Smartphone verbauten Schrittzähler-Sensor erfasst. Ein Foreground-Service läuft im Hintergrund, zählt die Schritte laufend mit und zeigt den aktuellen Stand über eine dauerhafte Benachrichtigung an. Da der Sensor die Schritte seit dem letzten Geräteneustart aufsummiert, merkt sich die App täglich einen Startwert, um daraus die Schritte des jeweiligen Tages zu berechnen; ein Neustart des Geräts wird dabei ebenfalls korrekt abgefangen.
Aus der Schrittzahl werden zusätzlich die zurückgelegte Strecke (in km) und der ungefähre Kalorienverbrauch geschätzt. Alle Werte werden pro Tag in der Datenbank gespeichert, wodurch neben dem heutigen Fortschritt auch der Verlauf der letzten 7 Tage angezeigt werden kann.
Auf dem Schrittzähler-Screen sieht man den heutigen Fortschritt als animierten Fortschrittsring im Vergleich zum Tagesziel, darunter die geschätzte Strecke und die verbrannten Kalorien sowie eine Liste der letzten 7 Tage inklusive Zielerreichung in Prozent.
Über ein Einstellungs-Menü können ein individuelles Tagesziel (Standard: 10.000 Schritte) sowie eine tägliche Erinnerung zu einer frei wählbaren Uhrzeit festgelegt werden. Wurde das Tagesziel zur eingestellten Zeit noch nicht erreicht, erhält man eine Benachrichtigung mit der Anzahl der noch fehlenden Schritte.


## Benachrichtigungen

Der User hat die Möglichkeit, eigene Benachrichtigungen zu erstellen und zu verwalten. Dabei stehen die Kategorien Wasser, Supplements und Training zur Verfügung, die für eine Fitness-App besonders relevant sind. Benachrichtigungen können entweder zu einer bestimmten Uhrzeit an einem oder mehreren ausgewählten Tagen oder in einem frei wählbaren Intervall innerhalb eines bestimmten Zeitraums (von-bis) eingerichtet werden. Bereits erstellte Benachrichtigungen können jederzeit bearbeitet oder gelöscht werden. Bereits vorhandene „Muster“-Optionen können ganz einfach aktiviert werden, ohne die selber erstellen zu müssen.

## Gewichtskontrolle

Bei der Gewichtskontrolle kann der User sein aktuelles Gewicht einfach eintragen und speichern. Auf Basis der eingegebenen Körpergröße und des Gewichts wird automatisch der BMI berechnet und angezeigt. Die gespeicherten Gewichtseinträge werden  in einem Graphen dargestellt, sodass die Entwicklung des Gewichts über einen längeren Zeitraum verfolgt und der persönliche Fortschritt leicht nachvollzogen werden kann.
