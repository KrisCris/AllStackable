package me.connlost.allstackable.server.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.connlost.allstackable.server.Server;
import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.ItemsHelper;
import me.connlost.allstackable.util.NetworkHelper;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.LinkedList;

public class CommandSetMaxCount {

    private static ItemsHelper itemsHelper = ItemsHelper.getItemsHelper();
    private static ConfigManager configManager = ConfigManager.getConfigManager();



    private static int showItem(ServerCommandSource source, ItemStackArgument isa) throws CommandSyntaxException {
        Item item = isa.getItem();
        source.sendFeedback(new TranslatableText("as.command.show_item",
                new Object[] {
                        isa.createStack(1, false).toHoverableText(),
                        Integer.valueOf(itemsHelper.getCurrentCount(item)),
                        Integer.valueOf(itemsHelper.getDefaultCount(item))
                }),false);
        return 1;
    }

    private static int showAll(ServerCommandSource source) throws CommandSyntaxException {
        LinkedList<Item> list = itemsHelper.getAllModifiedItem();
        if (list.isEmpty()){
            source.sendFeedback(new TranslatableText("as.command.show_none"), false);
        }
        for (Item item : list) {
            ItemStackArgument isa = new ItemStackArgument(item, null);
            source.sendFeedback(new TranslatableText("as.command.show_item",
                    new Object[] {
                            isa.createStack(1, false).toHoverableText(),
                            Integer.valueOf(itemsHelper.getCurrentCount(item)),
                            Integer.valueOf(itemsHelper.getDefaultCount(item))
                    }),false);
        }
        return 1;
    }

    private static int setItem(ServerCommandSource source, ItemStackArgument isa, int count) throws CommandSyntaxException {
        if (count > 64) {
            source.sendError(new TranslatableText("as.command.error_exceeded"));
            return 0;
        } else {
            Item item = isa.getItem();
            itemsHelper.setSingle(item, count);
            configManager.syncConfig();
            NetworkHelper.sentConfigToAll(Server.minecraft_server.getPlayerManager().getPlayerList());
            source.sendFeedback(new TranslatableText("as.command.set_item",
                    new Object[] {
                            source.getName(),
                            isa.createStack(1, false).toHoverableText(),
                            Integer.valueOf(count),
                            itemsHelper.getDefaultCount(item)
                    }), true);
        }
        return 1;
    }

    private static int resetItem(ServerCommandSource source, ItemStackArgument isa) throws CommandSyntaxException {
        itemsHelper.resetItem(isa.getItem());
        configManager.syncConfig();
        NetworkHelper.sentConfigToAll(Server.minecraft_server.getPlayerManager().getPlayerList());
        source.sendFeedback(new TranslatableText("as.command.reset_item",
                new Object[] {
                        isa.createStack(1, false).toHoverableText(),
                        source.getName()
                }), true);
        return 1;
    }

    private static int resetAll(ServerCommandSource source){
        itemsHelper.resetAll();
        configManager.resetAll();
        NetworkHelper.sentConfigToAll(Server.minecraft_server.getPlayerManager().getPlayerList());
        source.sendFeedback(new TranslatableText("as.command.reset_all",
                new Object[] {
                        source.getName()
                }), true);
        return 1;
    }


    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>) CommandManager.literal("allstackable").requires(source -> source.hasPermissionLevel(4))

                    .then(CommandManager.literal("show")
                            .then(CommandManager.literal("all")
                                    .executes(ctx -> showAll((ServerCommandSource) ctx.getSource()))
                            )

                            .then((RequiredArgumentBuilder) CommandManager.argument("item", ItemStackArgumentType.itemStack())
                                    .executes(ctx -> showItem((ServerCommandSource)ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item")))
                            )

                    )

                    .then(CommandManager.literal("reset")
                            .then(CommandManager.literal("all")
                                    .executes(ctx -> resetAll(ctx.getSource()))
                            )

                            .then((RequiredArgumentBuilder) CommandManager.argument("item", ItemStackArgumentType.itemStack())
                                    .executes(ctx -> resetItem((ServerCommandSource) ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item")))
                            )
                    )

                    .then(CommandManager.literal("set")
                            .then((RequiredArgumentBuilder) CommandManager.argument("item", ItemStackArgumentType.itemStack())
                                    .then(CommandManager.argument("count", IntegerArgumentType.integer(1))
                                            .executes(ctx -> setItem(
                                                    (ServerCommandSource) ctx.getSource(),
                                                    ItemStackArgumentType.getItemStackArgument(ctx, "item"),
                                                    IntegerArgumentType.getInteger(ctx, "count"))))
                            )
                    )
            );
        });
    }

}
