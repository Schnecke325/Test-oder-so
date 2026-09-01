# Station Decor

NeoForge-Mod für Minecraft **1.21.1**. Enthält alle drei geplanten Blöcke
sowie zwei Ks-Signalblöcke mit optionaler Create-Integration.

## Enthaltene Blöcke

### 1. `station_decor:obj_display` – Fahrkartenautomat (OBJ-Block)

- Wird über einen `BlockEntityRenderer` als **OBJ-Modell** gerendert (nicht
  über ein normales Blockmodell).
- Nutzt das gelieferte Modell `db_fahrkartenautomat.obj` (13 Cubes, 2 Blöcke
  breit × 3 Blöcke hoch × ~1 Block tief). Die Kollisionsbox deckt bewusst nur
  den Platzierungsblock ab – der optisch überstehende Teil ist begehbar
  (Mehrblock-Kollision wäre ein deutlich größeres Feature).
- **Aktuell bewusst ohne Rotation** (feste Ausrichtung) und mit einem stark
  vereinfachten GUI (nur ein grauer Kasten mit "Soon™", keine Knöpfe/Slots) –
  das ist ein Zwischenschritt, um das Laden des OBJ-Modells isoliert zu
  debuggen (siehe „Bekannte Einschränkungen"). Rotation und die richtige GUI
  (Zielknöpfe, Münzslot für eine spätere **Create: Numismatics**-Zahlung)
  kommen zurück, sobald das Modell zuverlässig rendert.

### 2. `station_decor:seat` – Sitzblock

- Genau wie der Anzeigeblock frei rotierbar, mit eigener konfigurierbarer
  Schrittzahl.
- Rechtsklick setzt den Spieler auf den Block (Stuhl-Verhalten: fixiert an
  der Position, freies Umsehen per Maus, aber keine Bewegung). Erneuter
  Rechtsklick lässt den Spieler wieder aufstehen.
- **Automatisches Ausrichten:** Wird ein neuer Sitzblock direkt neben einen
  bereits vorhandenen Sitzblock (Nord/Süd/Ost/West) platziert, übernimmt er
  automatisch dessen Rotation, statt sich an der Blickrichtung des Spielers
  zu orientieren. Abschaltbar per Config.

### 3. `station_decor:floor_marking` – Bodenmarkierung

- Ebenfalls frei rotierbar (eigene konfigurierbare Schrittzahl).
- **Vorschau-Outline beim Zielen:** Hält man das Item in der Hand und zielt
  auf einen Block, wird zusätzlich zur normalen Auswahlbox eine Outline
  gezeichnet, die die anvisierte Fläche entlang der (eingerasteten)
  Blickrichtung in 3 gleich große Zonen unterteilt. Bei z.B. einem
  45°-Rotationsschritt liegen diese Trennlinien entsprechend diagonal, da sie
  an derselben Rotation ausgerichtet sind, mit der der Block auch platziert wird.
- **Nah/Mitte/Fern-Versatz:** Je nachdem, in welcher der 3 Zonen man klickt,
  wird die Markierung beim Platzieren um 1/3 Block näher zum Spieler,
  zentriert oder um 1/3 Block weiter weg gesetzt – entlang ihrer eigenen
  (gedrehten) Vorwärtsachse. Das passiert in einem eigenen `BlockItem`
  (`FloorMarkingBlockItem`), da nur dort der exakte Klickpunkt bekannt ist.
- Technischer Hinweis: Das `RenderHighlightEvent.Block` von NeoForge ist in
  1.21.1 nicht abbrechbar – die 2 Trennlinien werden daher zusätzlich zur
  vanilla Auswahlbox gezeichnet, nicht anstelle davon.

### 4. `station_decor:ks_main_signal` – Ks-Hauptsignal & `station_decor:ks_distant_signal` – Ks-Vorsignal

- Der gezeigte Signalbegriff ist weiterhin eine normale BlockState-Property
  (`aspect`, dazu `facing`), wie bei einer Redstone-Lampe - Vanilla übernimmt
  die komplette Client-Synchronisation automatisch.
- Hauptsignal zeigt **Hp0** (Halt), **Hp1** (Fahrt), **Hp2** (Fahrt mit
  Geschwindigkeitsbeschränkung). Vorsignal zeigt **Vr0/Vr1/Vr2** analog.
- Rechtsklick schaltet manuell zum nächsten Signalbegriff (zum Testen ohne
  Create). Beim Platzieren richtet sich der Mast wie ein Ofen zur
  Blickrichtung des Spielers aus (4 Himmelsrichtungen, keine freie Rotation).
- **Ks-Hauptsignal scannt jetzt selbst lokal** (per Nutzervorgabe geändert):
  anders als das Vorsignal hat `ks_main_signal` jetzt eine eigene
  `KsMainSignalBlockEntity`, die alle 10 Ticks bis zu 10 Blöcke gerade nach
  unten nach einem Create-Gleissignal (`SignalBlockEntity`) sucht - exakt
  derselbe Scan wie beim Mehrabschnittssignal (siehe unten), inklusive
  Brass-Signalen: es wird nur per `instanceof SignalBlockEntity` geprüft,
  Tier/Variante spielt keine Rolle. RED → Hp0, GREEN → Hp1, YELLOW → Hp2.
  Wird **kein** Gleissignal in Reichweite gefunden (oder Create ist gar nicht
  installiert), bleibt der zuletzt gesetzte Begriff unverändert (manuell per
  Rechtsklick oder per Display Link gesetzt) - der Scan überschreibt also nur,
  wenn er tatsächlich ein Signal findet.
- **Ks-Vorsignal bleibt reiner Display-Link-Empfänger** (kein eigener lokaler
  Scan) - es wird ausschließlich per Create Display Link an ein Signal davor
  gebunden.
- **Create: Display Link-Kompatibilität** (wie bei Create's Nixie Tubes):
  Beide Blöcke registrieren sich zusätzlich als `DisplayTarget`
  (`com.simibubi.create.api.behaviour.display.DisplayTarget`, siehe
  `com.stationdecor.compat.create`). Ein Display Link kann Text wie `hp0`,
  `1` oder `Hp2` (bzw. `vr0`/`1`/`Vr2` beim Vorsignal) an das Signal senden,
  um den gezeigten Begriff zu setzen. Beim Hauptsignal gilt: der nächste
  lokale Scan-Durchlauf überschreibt einen per Display Link gesetzten Begriff
  wieder, sobald darunter ein Gleissignal gefunden wird - die beiden Quellen
  konkurrieren also nicht, der lokale Scan hat Vorrang, wenn er fündig wird.
- **Create ist nur eine optionale, compile-time-only Abhängigkeit**
  (`compileOnly` in `build.gradle`, siehe `create_version`/
  `registrate_version`/`ponder_version` in `gradle.properties` - Ponder wird
  transitiv gebraucht, weil Create's `SmartBlockEntity`, von der auch
  Gleissignale erben, Ponders `VirtualBlockEntity`-Interface implementiert).
  Ohne installiertes Create funktionieren alle drei Signale ganz normal (nur
  eben ohne Display-Link-Bindung) - die Compat-Klassen werden nur geladen,
  wenn `ModList.get().isLoaded("create")` beim Start `true` liefert.
- **Modell:** Alle drei Signaltypen nutzen jetzt ein echtes Mehrelement-
  Blockmodell (Mast + Signalkopf, reines Vanilla-Blockmodell-JSON mit
  `"elements"`, bewusst **kein** OBJ-Loader/BlockEntityRenderer - siehe
  „Bekannte Einschränkungen" zum Fahrkartenautomaten-Problem). Der
  Signalkopf trägt die jeweilige Lampen-Textur, der Mast eine neutrale
  Metalltextur (`textures/block/signal_mast.png`).

### 5. `station_decor:ks_multi_section_signal` – Ks-Mehrabschnittssignal

Kombiniert Haupt- und Vorsignalfunktion und zeigt **Fahrt**, **Halt** oder
**Halt erwarten**. Anders als Block 4 hat dieser Block eine BlockEntity, weil
er aktiv zwei Quellen kombiniert:

1. **Gleissignal-Scan:** Alle 10 Ticks wird bis zu 10 Blöcke gerade nach
   unten nach einem Create-Gleissignal (`SignalBlockEntity`) gesucht.
   RED → Halt, YELLOW → Halt erwarten, GREEN/kein Signal gefunden → Fahrt
   (aus Sicht dieser Quelle).
2. **Display Link zum Signal davor:** Das Mehrabschnittssignal ist selbst
   ein `DisplayTarget` - ein Create Display Link kann es an ein
   vorausliegendes `ks_main_signal` oder `ks_multi_section_signal` binden
   (beide sind zusätzlich als `DisplaySource` registriert, liefern also
   ihren aktuellen Begriff als Text). Zeigt das gebundene Signal Halt
   (`halt`/`hp0`/`vr0`/`0`/`red`/`stop`), merkt sich das Mehrabschnittssignal
   das als "Vorwarnung".

**Kombinationslogik** (siehe `KsMultiSectionSignalBlockEntity#recomputeAspect`,
nach Nutzer-Feedback angepasst): Gleissignal zeigt Halt → **Halt** (immer,
unabhängig vom Link). Sonst, wenn das gebundene Signal davor Halt zeigt →
**Halt erwarten**. Sonst → **Fahrt**. "Halt erwarten" tritt also **nur** ein,
wenn das verlinkte Signal Halt zeigt - ein lokales Gleissignal-YELLOW allein
löst es nicht mehr aus. Ein bereits "Halt erwarten" zeigendes Signal
propagiert das selbst nicht weiter als Vorwarnung an das Signal davor
(entspricht realer Signallogik - die Vorwarnung gilt nur für den unmittelbar
nächsten Halt-Begriff).

## Architektur-Änderung: Ks-Hauptsignal scannt jetzt selbst (statt nur Display Link)

Ursprünglich verließ sich `ks_main_signal` beim Aktualisieren komplett auf
manuelles Rechtsklicken oder einen gebundenen Create Display Link - ob
Display Links bei uns zuverlässig ankommen, blieb ungeklärt (das zuerst
geteilte `latest.log` deckte nur ~90 Sekunden ohne jeden Bindungsversuch ab).

Auf Nutzervorgabe hin funktioniert `ks_main_signal` jetzt **wie das
Mehrabschnittssignal**: eine eigene `KsMainSignalBlockEntity` scannt alle 10
Ticks bis zu 10 Blöcke unter sich nach einem Create-Gleissignal und übernimmt
dessen Zustand direkt (RED → Hp0, GREEN → Hp1, YELLOW → Hp2). Das ist
derselbe robuste, log-mäßig bereits als funktionierend bestätigte Mechanismus
wie beim Mehrabschnittssignal (`CreateCompat.readTrackSignalBelow`) - der
Display-Link-Pfad bleibt als Zweitquelle bestehen, wird aber vom nächsten
Scan-Durchlauf überschrieben, sobald ein physisches Gleissignal gefunden
wird. Das Ks-Vorsignal (`ks_distant_signal`) bleibt bewusst **ohne** eigenen
Scan - es hat ja kein "davor liegendes Gleissignal", sondern wird gezielt per
Display Link an ein Signal gebunden.

Die Diagnose-Logs in den `DisplayTarget`-Implementierungen
(`Ks-Hauptsignal bei BlockPos{...} hat "..." per Display Link empfangen -> ...`)
bleiben für `ks_distant_signal` weiterhin relevant, falls dessen
Display-Link-Bindung beim nächsten Test noch nicht greift - bitte in dem Fall
wieder ein frisches `logs/latest.log` teilen, das eine tatsächliche
Bindung+Auslösung eines Display Links an `ks_distant_signal` abdeckt.

## Root Cause gefunden: Warum Display Link nie funktioniert hat

Beim Untersuchen, warum das Vorsignal (rein Display-Link-gesteuert) nie
aktualisiert, während Haupt-/Mehrabschnittssignal nur wegen ihres **eigenen**
lokalen Gleissignal-Scans funktionieren (der komplett unabhängig vom Display
Link läuft), habe ich Creates Quellcode (`mc1.21.1/dev`-Branch) direkt
durchsucht. Ergebnis: Unsere `DisplaySource`-Implementierungen
(`KsMainSignalDisplaySource`, `KsMultiSectionSignalDisplaySource`) wurden nur
über `DisplaySource.BY_BLOCK.add(block, instanz)` einem Block zugeordnet -
sie wurden aber **nie** als echter Eintrag in Creates eigener Registry
(`CreateBuiltInRegistries.DISPLAY_SOURCE`, eine normale NeoForge-Registry)
registriert. Ohne diese Registrierung liefert `getId()` auf unseren Quellen
`null`, wodurch `getName()` - beim Öffnen des Display-Link-Bildschirms zum
Aufbau der Quellen-Auswahlliste aufgerufen - mit einer
NullPointerException fehlschlägt bzw. unsere Quelle dort erst gar nicht
sauber auswählbar ist. Damit ließ sich "Ks-Hauptsignal Begriff" nie
tatsächlich als aktive Quelle für einen Display Link auswählen - der Link
hatte also schlicht nie eine gültige Datenquelle, unabhängig davon, wie
richtig Ziel-Seite (`acceptLine`) implementiert war.

**Fix:** Neue Klasse `CreateDisplayRegistry` registriert unsere
`DisplaySource`-/`DisplayTarget`-Instanzen jetzt zusätzlich als echte
Registry-Einträge (`DeferredRegister<DisplaySource>`/`DeferredRegister<DisplayTarget>`
gegen `CreateRegistries.DISPLAY_SOURCE`/`DISPLAY_TARGET`), bereits im
Mod-Konstruktor (muss vor `FMLCommonSetupEvent` passieren, da NeoForge
Registry-Einträge über das reguläre `RegisterEvent` sammelt). `CreateCompat.register()`
ordnet diese jetzt registrierten Instanzen weiterhin den Blöcken zu. Fehlende
Übersetzungen für die Anzeigenamen (`station_decor.display_source.ks_main_signal_aspect`
usw.) wurden ebenfalls ergänzt. Das sollte das Vorsignal-Problem beheben -
bitte im Display-Link-Bildschirm am Hauptsignal/Mehrabschnittssignal jetzt
gezielt "Ks-Hauptsignal Begriff" bzw. "Ks-Mehrabschnittssignal Begriff" als
Quelle auswählen (falls dort noch ein anderer Eintrag wie "Redstonestärke"
aktiv war, wurde dieser genutzt statt unserer eigenen Quelle).

## Nur die Lampe leuchtet, nicht der ganze Signalmast

Bisher machte `lightLevel(state -> 10)` den **gesamten Block** zur
Lichtquelle - Vanilla-Blocklicht lässt sich nicht auf einzelne
Modell-Elemente/Flächen beschränken, wodurch Mast und Signalkopf gleich hell
wirkten. Jetzt zeichnet ein zusätzlicher `BlockEntityRenderer`
(`SignalAspectLampRenderer`/`SignalLampRenderHelper`) nur die Lampenfläche
(Nord-/Südseite des `head`-Elements) mit fest voller Helligkeit,
unabhängig vom Umgebungslicht - der Mast bleibt normal vom Umgebungslicht
abhängig (dunkel bei Nacht). Alle drei Signalblöcke haben dafür jetzt eine
BlockEntity, auch `ks_distant_signal` (vorher keine) - rein technisch, ohne
eigene Scan-Logik. `lightLevel` wurde entfernt (kein tatsächlicher
Licht-Emitter mehr, rein optischer Effekt).

## Fahrkartenautomat: Form stimmt, Textur sieht verzerrt aus

Kein Rendering-Bug: Das gelieferte Modell (`db_fahrkartenautomat.obj`) nutzt
UV-Koordinaten, die für die ursprüngliche, nie hochgeladene
`ticket_machine_db_new.png` (vermutlich ein größeres Textur-Atlas, das
mehrere Bereiche für unterschiedliche Teile des Automaten enthält) gedacht
waren. Unser 32×32-Platzhalter deckt nur einen kleinen Ausschnitt davon ab
(UV-Bereich u: 0–0,53, v: 0,40–1,0) und wird auf diesen Ausschnitt gequetscht
- das sieht dadurch verzerrt/wie eine einfarbige Fläche aus, nicht wie ein
Rendering-Fehler. Um das richtig zu fixen, bräuchte ich die echte
`ticket_machine_db_new.png` (falls noch vorhanden) - einfach unter
`textures/block/ticket_machine.png` ablegen, dann passt die Zuordnung
automatisch.

## Konfiguration

Nach dem ersten Start liegt die Common-Config unter
`config/station_decor-common.toml`:

| Key                       | Standard | Bedeutung                                                                 |
|----------------------------|----------|----------------------------------------------------------------------------|
| `objBlockRotationSteps`   | `8`      | Rotationsschritte des Anzeigeterminals (4 = 90°, 8 = 45°, 16 = 22,5°, ...) |
| `seatBlockRotationSteps`  | `8`      | Rotationsschritte des Sitzblocks                                          |
| `seatBlockAutoAlign`      | `true`   | Automatisches Ausrichten an einem angrenzenden Sitzblock beim Platzieren  |
| `floorMarkingRotationSteps` | `8`    | Rotationsschritte der Bodenmarkierung                                     |

Die Schrittzahl ist frei zwischen 2 und 64 wählbar (keine feste Beschränkung
auf 4/8/16 – "8" ergibt z.B. 45°-Schritte, weil `360° / 8 = 45°`).

## Architektur (kurz)

- Die Rotation wird **nicht** als BlockState-Property abgebildet, weil deren
  Anzahl von der Config abhängt (und BlockStates zur Registrierungszeit fest
  stehen müssen). Stattdessen hält jede `AbstractRotatableBlockEntity`
  (`block/rotation/AbstractRotatableBlockEntity.java`) einen Rotationsindex +
  die Schrittzahl, mit der er erzeugt wurde, persistiert das selbst und
  synchronisiert es zum Client.
- Gerendert wird über `RotatedObjRenderHelper`, der ein per
  `neoforge:obj`-Loader geladenes Standalone-Modell
  (`ModelEvent.RegisterAdditional`) um einen beliebigen Winkel dreht und
  zeichnet – das ist der Mechanismus, der Zwischenwinkel überhaupt erst
  ermöglicht.
- Der Sitzblock nutzt dafür zusätzlich eine unsichtbare, rein technische
  `SeatEntity`, auf die der Spieler per `startRiding` gesetzt wird. Sie
  entfernt sich automatisch wieder, sobald niemand mehr sitzt oder der Block
  entfernt wird.

## Bekannte Einschränkungen / Platzhalter

Damit das Grundgerüst sofort baut und im Spiel testbar ist, wurden
Platzhalter-Assets erzeugt, die ihr nach Bedarf ersetzen könnt:

- **3D-Modelle:** `seat.obj`/`.mtl` und `floor_marking.obj`/`.mtl` sind
  einfache Platzhaltergeometrien, keine fertigen Möbelstücke. Der
  Fahrkartenautomat (`db_fahrkartenautomat.obj`/`.mtl`) ist dagegen bereits
  das gelieferte, echte Modell. Ersetzen läuft über
  `models/block/<name>_render.json` (Pfad zum `.obj`, Textur-Zuordnung).
- **Textur des Fahrkartenautomaten fehlt noch:** Die `.mtl`-Datei referenzierte
  ursprünglich `ticket_machine_db_new.png`, die nicht mit hochgeladen wurde.
  Ich habe einen simplen 32×32-Platzhalter unter
  `textures/block/ticket_machine.png` erzeugt. Die echte Textur einfach unter
  gleichem Pfad/Namen ablegen, sobald verfügbar.
- **Root Cause gefunden und behoben (dank `logs/latest.log`):** Der Grund
  für den magenta/schwarzen "Missing Model"-Würfel war die
  `map_Kd`-Textur-Referenz gar nicht, sondern der **Dateiname** des Modells.
  Minecraft-`ResourceLocation`-Pfade erlauben nur `[a-z0-9/._-]` -
  `DB_Fahrkartenautomat.obj`/`.mtl` enthielt Großbuchstaben und wurde daher
  mit `Invalid path in pack ... ignoring` komplett verworfen, bevor der
  OBJ-Loader überhaupt zum Zug kam. Dateien in `db_fahrkartenautomat.obj`/
  `.mtl` umbenannt (inkl. der internen `mtllib`-Referenz und des Verweises
  in `obj_display_render.json`) - das war der eigentliche Bug, nicht das
  `#texture0`-Token-Problem von vorher (das war trotzdem eine reale,
  separate Verbesserung).
- **Aktuell weiterhin bewusst reduziert:** Rotation (feste 0°-Ausrichtung),
  Inventar-Slot und die echte GUI (Zielknöpfe, Münzslot) wurden für das
  Debugging entfernt und sind noch nicht zurückgebaut - sag Bescheid, wenn
  das wieder rein soll, jetzt wo die eigentliche Ursache behoben ist.
- **Weitere Texturen:** `textures/block/seat.png` und `floor_marking.png`
  sind simple 16×16-Platzhalter.
- **Kollisionsbox:** Aus Einfachheitsgründen ist die Hitbox beider Blöcke
  rotationsunabhängig (ein fester, leicht verkleinerter Würfel). Eine exakt
  der 22,5°-Rotation folgende Box wäre nicht mehr achsenparallel und wurde
  bewusst nicht umgesetzt.

## Build & Testen

Voraussetzung: JDK 21.

```bash
./gradlew build          # baut den Mod-JAR unter build/libs/
./gradlew runClient      # startet einen Testclient mit der Mod
./gradlew runServer      # startet einen Testserver
```

Falls der Build eine neuere NeoForge-Version verlangt: aktuelle Version unter
<https://projects.neoforged.net/neoforged/neoforge> nachsehen und in
`gradle.properties` (`neo_version`) eintragen.

**Wichtig:** `./gradlew build`/`compileJava` prüft nur den Java-Code gegen die
echte NeoForge-API (das wurde in dieser Session mehrfach erfolgreich
verifiziert). Blockstates, Modell-JSONs, `.obj`/`.mtl`-Dateien und Texturen
werden dabei **nicht** validiert – das passiert erst beim tatsächlichen
Laden im Client (`runClient`). Insbesondere das Laden des echten
`db_fahrkartenautomat.obj`-Modells und der Y-Achsen-Ausrichtungskorrektur in
`ObjDisplayBlockEntityRenderer` wurden nur rechnerisch (Bounding-Box-Analyse),
nicht visuell in einem laufenden Client geprüft, da diese Sandbox kein
Display hat.

## Projektstruktur

```
src/main/java/com/stationdecor/
  StationDecorMod.java          Einstiegspunkt
  config/                       Common-Config
  registry/                     DeferredRegister (Blocks, Items, BlockEntities, Entities, Menus, CreativeTab)
  block/rotation/                Gemeinsame Rotationslogik (Util + Basis-BlockEntity)
  block/obj/                     Anzeigeterminal (Block, BlockEntity)
  block/seat/                    Sitzblock (Block, BlockEntity, SeatEntity)
  menu/                          Container-Menü des Terminals
  client/                        Rendering (BER, OBJ-Modell-Rotation), Screen, Registrierung
src/main/resources/
  assets/station_decor/          Blockstates, Modelle, Texturen, Lang
  data/station_decor/            Loot Tables
  data/minecraft/tags/block/...  Werkzeug-Tags (pickaxe/axe)
```
