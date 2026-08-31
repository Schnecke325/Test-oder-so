# Station Decor

NeoForge-Mod für Minecraft **1.21.1**. Enthält aktuell zwei der drei geplanten
Blöcke; der dritte folgt, sobald er beschrieben wird.

## Enthaltene Blöcke

### 1. `station_decor:obj_display` – Anzeigeterminal (OBJ-Block)

- Wird als frei rotierbares **OBJ-Modell** gerendert (über einen
  `BlockEntityRenderer`, nicht über ein normales Blockmodell – dadurch sind
  auch Zwischenwinkel wie 45° oder 22,5° möglich, nicht nur 90°-Schritte).
- Die Rotation wird beim Platzieren automatisch aus der Blickrichtung des
  Spielers berechnet und auf den nächsten konfigurierten Schritt gerundet.
- Rechtsklick öffnet ein Container-GUI (aktuell ein generisches 3×3-Raster
  plus Spielerinventar als Platzhalter/Grundgerüst – siehe unten).

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

### 3. (folgt)

## Konfiguration

Nach dem ersten Start liegt die Common-Config unter
`config/station_decor-common.toml`:

| Key                       | Standard | Bedeutung                                                                 |
|----------------------------|----------|----------------------------------------------------------------------------|
| `objBlockRotationSteps`   | `8`      | Rotationsschritte des Anzeigeterminals (4 = 90°, 8 = 45°, 16 = 22,5°, ...) |
| `seatBlockRotationSteps`  | `8`      | Rotationsschritte des Sitzblocks                                          |
| `seatBlockAutoAlign`      | `true`   | Automatisches Ausrichten an einem angrenzenden Sitzblock beim Platzieren  |

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

- **3D-Modelle:** `src/main/resources/assets/station_decor/models/obj/*.obj`
  (+ `.mtl`) sind einfache Platzhaltergeometrien (Terminal-Sockel mit
  "Bildschirm", Sitzfläche mit Rückenlehne), keine fertigen Möbelstücke.
  Einfach durch eigene `.obj`/`.mtl`-Dateien mit gleichem Dateinamen
  ersetzen; die Zuordnung läuft über
  `models/block/obj_display_render.json` bzw. `seat_render.json`.
- **Texturen:** `textures/block/obj_display.png` und `seat.png` sind simple
  16×16-Platzhaltertexturen.
- **GUI-Inhalt:** Das Terminal-GUI ist aktuell ein generisches 3×3-Raster +
  Spielerinventar (Klasse `menu/ObjDisplayMenu.java`, Screen nutzt die
  vanilla Werfer-Textur als Hintergrund). Sag einfach, was konkret im GUI
  passieren soll (Infoanzeige, Filter, Rezepte, etc.), dann bauen wir es
  entsprechend aus.
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
