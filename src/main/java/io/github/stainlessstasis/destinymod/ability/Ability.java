package io.github.stainlessstasis.destinymod.ability;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum Ability implements StringRepresentable {
    MELEE("melee");

    public static final Codec<Ability> CODEC = StringRepresentable.fromEnum(Ability::values);

    private final String name;
    Ability(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
