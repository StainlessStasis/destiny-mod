package io.github.stainlessstasis.destinymod.client.keyword;

import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import io.github.stainlessstasis.destinymod.registry.datapack.Aspects;
import io.github.stainlessstasis.destinymod.util.AbilityUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DestinyKeywords {
    private static final Map<String, List<DestinyKeyword>> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put(AbilityUtil.getAbilityName(Abilities.THROWING_HAMMER), List.of(DestinyKeyword.SCORCH, DestinyKeyword.IGNITION));
        KEYWORDS.put(AbilityUtil.getAbilityName(Abilities.THERMITE_GRENADE), List.of(DestinyKeyword.SCORCH, DestinyKeyword.IGNITION));
        KEYWORDS.put(AbilityUtil.getAspectName(Aspects.MELTING_POINT), List.of(DestinyKeyword.IGNITION));
        KEYWORDS.put(AbilityUtil.getAspectName(Aspects.HEATSEEKER), List.of(DestinyKeyword.SCORCH, DestinyKeyword.IGNITION));
    }

    public static List<DestinyKeyword> getKeywordsFor(String name) {
        return KEYWORDS.getOrDefault(name, List.of());
    }
}
