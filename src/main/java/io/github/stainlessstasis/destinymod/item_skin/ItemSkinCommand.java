package io.github.stainlessstasis.destinymod.item_skin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.stainlessstasis.destinymod.data.DestinyModDataComponents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.ItemStack;

public class ItemSkinCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("itemskin")
                        .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .then(Commands.literal("add")
                                .then(Commands.argument("skin", IdentifierArgument.id())
                                        .suggests((ctx, builder) -> {
                                            ItemSkinIDs.getAll().forEach(id ->
                                                    builder.suggest(id.toString()));
                                            return builder.buildFuture();
                                        })
                                        .executes(ItemSkinCommand::applySkin)
                                )
                        )
                        .then(Commands.literal("remove")
                                .executes(ItemSkinCommand::removeSkin)
                        )
        );
    }

    private static int applySkin(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Identifier skinId = IdentifierArgument.getId(ctx, "skin");

        if (!ItemSkinIDs.has(skinId)) {
            ctx.getSource().sendFailure(Component.literal("Unknown skin: " + skinId));
            return 0;
        }

        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("You must be holding an item in your main hand."));
            return 0;
        }

        heldItem.set(DestinyModDataComponents.ITEM_SKIN.get(), new ItemSkinComponent(skinId));
        ctx.getSource().sendSuccess(() -> Component.literal("Applied skin " + skinId + " to held item."), false);
        return 1;
    }

    private static int removeSkin(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("You must be holding an item in your main hand."));
            return 0;
        }

        if (!heldItem.has(DestinyModDataComponents.ITEM_SKIN.get())) {
            ctx.getSource().sendFailure(Component.literal("Held item has no skin."));
            return 0;
        }

        heldItem.remove(DestinyModDataComponents.ITEM_SKIN.get());
        ctx.getSource().sendSuccess(() -> Component.literal("Removed skin from held item."), false);
        return 1;
    }
}
