package me.connlost.allstackable.server.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.ItemsHelper;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.LinkedList;

public class StackSizeCommand {

    private static ItemsHelper itemsHelper = ItemsHelper.getItemsHelper();
    private static ConfigManager configManager = ConfigManager.getConfigManager();

    private static int showItem(ServerCommandSource source, Item item) throws CommandSyntaxException {
        source.sendFeedback(new TranslatableText("as.command.show_item",
                new TranslatableText(item.getTranslationKey()),
                itemsHelper.getCurrentCount(item),
                itemsHelper.getDefaultCount(item)), false);
        return 1;
    }

    private static int showAll(ServerCommandSource source) throws CommandSyntaxException {
        LinkedList<Item> list = itemsHelper.getAllModifiedItems();
        if (list.isEmpty()) {
            source.sendFeedback(new TranslatableText("as.command.show_none"), false);
        }
        for (Item item : list) {
            source.sendFeedback(new TranslatableText("as.command.show_item",
                    new TranslatableText(item.getTranslationKey()),
                    itemsHelper.getCurrentCount(item),
                    itemsHelper.getDefaultCount(item)), false);
        }
        return 1;
    }

    private static int showItemOnHand(ServerCommandSource source, ServerPlayerEntity serverPlayerEntity) throws CommandSyntaxException {
        Item item = null;
        if ((item = getMainHandItem(source, serverPlayerEntity)) == null) {
            return 0;
        }
        return showItem(source, item);
    }

    private static int setItem(ServerCommandSource source, Item item, int count) throws CommandSyntaxException {
//        if (count > 64) {
//            source.sendError(new TranslatableText("as.command.error_exceeded"));
//            return 0;
//        } else {
            itemsHelper.setSingle(item, count);
            configManager.syncConfig();
            source.sendFeedback(new TranslatableText("as.command.set_item",
                    new TranslatableText(item.getTranslationKey()),
                    count,
                    itemsHelper.getDefaultCount(item)), true);
//        }
        return 1;
    }

    private static int setItemOnHand(ServerCommandSource source, ServerPlayerEntity serverPlayerEntity, int count) throws CommandSyntaxException {
        Item item = null;
        if ((item = getMainHandItem(source, serverPlayerEntity)) == null) {
            return 0;
        }
        return setItem(source, item, count);
    }

    private static int setMatched(ServerCommandSource source, String type, int originalSize, int newSize){
        int count = itemsHelper.setMatchedItems(originalSize, newSize, type);
        configManager.syncConfig();
        source.sendFeedback(
                new TranslatableText(
                        "as.command.set_matched",
                        count,
                        newSize,
                        type.equals("vanilla") ? new TranslatableText("as.command.default") : new TranslatableText("as.command.previous"),
                        originalSize
                ),
                true
        );
        return 1;
    }

    private static int resetItem(ServerCommandSource source, Item item) throws CommandSyntaxException {
        itemsHelper.resetItem(item);
        configManager.syncConfig();
        source.sendFeedback(new TranslatableText("as.command.reset_item",
                new TranslatableText(item.getTranslationKey())), true);
        return 1;
    }

    private static int resetAllItems(ServerCommandSource source) {
        itemsHelper.resetAll(true);
        configManager.resetAllItems();
        source.sendFeedback(new TranslatableText("as.command.reset_all"), true);
        return 1;
    }

    private static int resetItemOnHand(ServerCommandSource source, ServerPlayerEntity serverPlayerEntity) throws CommandSyntaxException {
        Item item = null;
        if ((item = getMainHandItem(source, serverPlayerEntity)) == null) {
            return 0;
        }
        return resetItem(source, item);
    }

    private static Item getMainHandItem(ServerCommandSource source, ServerPlayerEntity serverPlayerEntity) throws CommandSyntaxException {
        ItemStack itemStack = serverPlayerEntity.getMainHandStack();
        if (itemStack.isEmpty()) {
            String u1 = serverPlayerEntity.getName().asString();
            String u2 = source.getEntity() instanceof ServerPlayerEntity ? source.getName() : "Server";

            source.sendError(new TranslatableText("as.command.error_empty_hand", u1.equals(u2) ? new TranslatableText("as.command.you") : serverPlayerEntity.getName()));
            return null;
        }
        return itemStack.getItem();
    }

    private static int reloadConfig(ServerCommandSource source) throws CommandSyntaxException {
        configManager.setupConfig();
        source.sendFeedback(new TranslatableText("as.command.reloaded", source.getPlayer().getName()), true);
        return 1;
    }


    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("allstackable").requires(source -> source.hasPermissionLevel(4))

                    .then(literal("show")
                            .then(literal("hand")
                                    .then(argument("targets", EntityArgumentType.player())
                                            .executes(ctx -> showItemOnHand(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "targets"))))
                            )
                            .then(literal("all")
                                    .executes(ctx -> showAll(ctx.getSource()))
                            )
                            .then(argument("item", ItemStackArgumentType.itemStack())
                                    .executes(ctx -> showItem(ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem()))
                            )

                    )
                    .then(literal("reset")
                            .then(literal("hand")
                                    .then(argument("targets", EntityArgumentType.player())
                                            .executes(ctx -> resetItemOnHand(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "targets"))))
                            )
                            .then(literal("all")
                                    .executes(ctx -> resetAllItems(ctx.getSource()))
                            )
                            .then(argument("item", ItemStackArgumentType.itemStack())
                                    .executes(ctx -> resetItem(ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem()))
                            )
                    )
                    .then(literal("set")
                            .then(argument("item", ItemStackArgumentType.itemStack())
                                    .then(argument("count", IntegerArgumentType.integer(1))
                                            .executes(ctx -> setItem(
                                                    ctx.getSource(),
                                                    ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem(),
                                                    IntegerArgumentType.getInteger(ctx, "count"))))
                            )
                            .then(literal("hand")
                                    .then(argument("targets", EntityArgumentType.player())
                                            .then(argument("count", IntegerArgumentType.integer(1))
                                                    .executes(ctx -> setItemOnHand(
                                                            ctx.getSource(),
                                                            EntityArgumentType.getPlayer(ctx, "targets"),
                                                            IntegerArgumentType.getInteger(ctx, "count"))))
                                    )
                            )
                            .then(literal("vanilla")
                                    .then(argument("vanillaSize", IntegerArgumentType.integer(1))
                                            .then(argument("customSize", IntegerArgumentType.integer(1))
                                                    .executes(ctx -> setMatched(
                                                            ctx.getSource(),
                                                            "vanilla",
                                                            IntegerArgumentType.getInteger(ctx,"vanillaSize"),
                                                            IntegerArgumentType.getInteger(ctx, "customSize")
                                                    ))
                                            )
                                    )
                            )
                            .then(literal("modified")
                                    .then(argument("previousSize", IntegerArgumentType.integer(1))
                                            .then(argument("newSize", IntegerArgumentType.integer(1))
                                                    .executes(ctx -> setMatched(
                                                            ctx.getSource(),
                                                            "modified",
                                                            IntegerArgumentType.getInteger(ctx,"previousSize"),
                                                            IntegerArgumentType.getInteger(ctx, "newSize")
                                                    ))
                                            )
                                    )
                            )
                            .then(literal("all")
                                    .then(argument("previousSize", IntegerArgumentType.integer(1))
                                            .then(argument("newSize", IntegerArgumentType.integer(1))
                                                    .executes(ctx -> setMatched(
                                                            ctx.getSource(),
                                                            "all",
                                                            IntegerArgumentType.getInteger(ctx,"previousSize"),
                                                            IntegerArgumentType.getInteger(ctx, "newSize")
                                                    ))
                                            )
                                    )
                            )
                    )
                    .then(literal("reload")
                            .executes(ctx -> reloadConfig(ctx.getSource()))
                    );

            dispatcher.register(literalArgumentBuilder);
        });
    }

}
