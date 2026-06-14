package io.github.stainlessstasis.destinymod.client.keyword;

import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import io.github.stainlessstasis.destinymod.registry.datapack.Aspects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DestinyKeywords {
    private static final Map<String, List<DestinyKeyword>> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put(Abilities.THROWING_HAMMER.getName(), List.of(DestinyKeyword.SCORCH, DestinyKeyword.IGNITION));
        KEYWORDS.put(Abilities.THERMITE_GRENADE.getName(), List.of(DestinyKeyword.SCORCH, DestinyKeyword.IGNITION));
        KEYWORDS.put(Abilities.SOL_INVICTUS.getName(), List.of(DestinyKeyword.SCORCH, DestinyKeyword.IGNITION));
        KEYWORDS.put(Aspects.MELTING_POINT.getName(), List.of(DestinyKeyword.IGNITION));
        KEYWORDS.put(Aspects.HEATSEEKER.getName(), List.of(DestinyKeyword.SCORCH, DestinyKeyword.IGNITION));
    }

    public static List<DestinyKeyword> getKeywordsFor(String name) {
        return KEYWORDS.getOrDefault(name, List.of());
    }
}
