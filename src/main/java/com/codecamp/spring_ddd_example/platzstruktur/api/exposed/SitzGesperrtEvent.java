package com.codecamp.spring_ddd_example.platzstruktur.api.exposed;

import java.util.Set;
import java.util.UUID;

public record SitzGesperrtEvent(Set<UUID> sitzplatzIds) {
}
