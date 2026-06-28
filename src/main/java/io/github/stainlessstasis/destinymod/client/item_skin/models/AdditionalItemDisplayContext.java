package io.github.stainlessstasis.destinymod.client.item_skin.models;

import net.minecraft.world.item.ItemDisplayContext;

public record AdditionalItemDisplayContext(
        boolean isFirstPerson, boolean isThirdPerson, boolean isLeftHand, boolean isRightHand, boolean isInHand
) {
    public static AdditionalItemDisplayContext create(ItemDisplayContext displayMode) {
        boolean isFirstPerson = displayMode.firstPerson();
        boolean isThirdPerson = displayMode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || displayMode == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        boolean isLeftHand = displayMode.leftHand();
        boolean isRightHand = displayMode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || displayMode == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        boolean isInHand = isLeftHand || isRightHand;
        return new AdditionalItemDisplayContext(isFirstPerson, isThirdPerson, isLeftHand, isRightHand, isInHand);
    }
}
