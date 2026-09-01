package com.stationdecor.block.signal;

/**
 * Ergebnis eines Blockabschnitt-Scans (z.B. eines Create-Gleissignals unter
 * dem Mehrabschnittssignal). Bewusst ein eigener, Create-unabhängiger Typ,
 * damit {@link KsMultiSectionSignalBlockEntity} niemals direkt eine
 * Create-Klasse referenzieren muss - das würde ohne installiertes Create
 * beim Laden der Klasse abstürzen. Die eigentliche Übersetzung von Creates
 * Signalzustand in diesen Typ passiert ausschließlich in
 * {@code com.stationdecor.compat.create}.
 */
public enum SectionState {
    HALT,
    HALT_ERWARTEN,
    FAHRT
}
