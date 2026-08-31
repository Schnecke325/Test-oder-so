# Station Decor

NeoForge-Mod für Minecraft **1.21.1**. Enthält alle drei geplanten Blöcke.

## Enthaltene Blöcke

### 1. `station_decor:obj_display` – Fahrkartenautomat (OBJ-Block)

- Wird als frei rotierbares **OBJ-Modell** gerendert (über einen
  `BlockEntityRenderer`, nicht über ein normales Blockmodell – dadurch sind
  auch Zwischenwinkel wie 45° oder 22,5° möglich, nicht nur 90°-Schritte).
- Nutzt das gelieferte Modell `DB_Fahrkartenautomat.obj` (13 Cubes, 2 Blöcke
  breit × 3 Blöcke hoch × ~1 Block tief). Die Kollisionsbox deckt bewusst nur
  den Platzierungsblock ab – der optisch überstehende Teil ist begehbar
  (Mehrblock-Kollision wäre ein deutlich größeres Feature).
- Die Rotation wird beim Platzieren automatisch aus der Blickrichtung des
  Spielers berechnet und auf den nächsten konfigurierten Schritt gerundet.
- Rechtsklick öffnet die **Fahrkartenautomat-GUI**: aktuell 6 Platzhalter-
  Zielknöpfe (zeigen nur eine "noch nicht verfügbar"-Meldung) sowie ein
  einzelner **Münzslot**. Dieser Slot ist der vorgesehene Anknüpfungspunkt für
  eine spätere **Create: Numismatics**-Zahlungsintegration (Fahrkarte gegen
  Münzen kaufen) – aktuell nimmt er noch jedes Item an, ohne Wirkung.

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
  Fahrkartenautomat (`DB_Fahrkartenautomat.obj`/`.mtl`) ist dagegen bereits
  das gelieferte, echte Modell. Ersetzen läuft über
  `models/block/<name>_render.json` (Pfad zum `.obj`, Textur-Zuordnung).
- **Textur des Fahrkartenautomaten fehlt noch:** Die `.mtl`-Datei referenzierte
  ursprünglich `ticket_machine_db_new.png`, die nicht mit hochgeladen wurde.
  Ich habe einen simplen 32×32-Platzhalter unter
  `textures/block/ticket_machine.png` erzeugt. Die echte Textur einfach unter
  gleichem Pfad/Namen ablegen, sobald verfügbar.
- **Fix nach erstem In-Game-Test:** Alle drei `.mtl`-Dateien referenzieren die
  Textur jetzt direkt per Namespace (`map_Kd station_decor:block/<name>`)
  statt über das `#texture0`-Token aus dem model-json. Das Token-Verfahren
  ist laut einem bekannten NeoForge-Issue in dieser Version nicht zuverlässig
  und führte dazu, dass das OBJ-Modell nicht buk und stattdessen der
  magenta/schwarze "Missing Model"-Würfel gerendert wurde.
- **Weitere Texturen:** `textures/block/seat.png` und `floor_marking.png`
  sind simple 16×16-Platzhalter.
- **GUI-Inhalt Fahrkartenautomat:** aktuell 6 Platzhalter-Zielknöpfe (keine
  Funktion) + 1 Münzslot für eine spätere Create: Numismatics-Zahlung. Sag
  Bescheid, wenn die eigentliche Fahrkartenlogik (Zielauswahl, Preis, Abzug
  der Münzen, Ausgabe eines Ticket-Items) drankommen soll.
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
`DB_Fahrkartenautomat.obj`-Modells und der Y-Achsen-Ausrichtungskorrektur in
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
