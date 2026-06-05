package io.github.stainlessstasis.destinymod.tooltip.component;

import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DescriptionComponentParser {
    public static final Pattern PATTERN = Pattern.compile("<(\\w+):([^>]+)>");

    public static Component parseTranslatable(String translationKey) {
        if (Language.getInstance().has(translationKey)) {
            String raw = Language.getInstance().getOrDefault(translationKey);
            return parse(raw);
        }

        return parse(translationKey);
    }

    public static Component parse(String unparsed) {
        MutableComponent parsed = Component.empty();
        Matcher matcher = PATTERN.matcher(unparsed);
        int lastIndex = 0;

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                String normalText = unparsed.substring(lastIndex, matcher.start());
                parsed.append(Component.literal(normalText).withStyle(ChatFormatting.GRAY));
            }

            String tagType = matcher.group(1);
            String tagText = matcher.group(2);

            MutableComponent styledPart = Component.literal(tagText);
            switch (tagType.toLowerCase()) {
                case "keyword" -> styledPart.withStyle(ChatFormatting.GOLD);
                case "ability" -> styledPart.withStyle(ChatFormatting.WHITE);
                default -> styledPart.withStyle(ChatFormatting.GRAY);
            }

            parsed.append(styledPart);
            lastIndex = matcher.end();
        }

        if (lastIndex < unparsed.length()) {
            String trailingText = unparsed.substring(lastIndex);
            parsed.append(Component.literal(trailingText).withStyle(ChatFormatting.GRAY));
        }

        return parsed;
    }
}
