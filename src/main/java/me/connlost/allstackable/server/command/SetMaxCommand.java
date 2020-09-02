package me.connlost.allstackable.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.connlost.allstackable.server.Server;
import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.ItemsHelper;
import me.connlost.allstackable.util.NetworkHelper;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.util.LinkedList;

public class SetMaxCommand {

    private static ItemsHelper itemsHelper = ItemsHelper.getItemsHelper();
    private static ConfigManager configManager = ConfigManager.getConfigManager();


    private static int showItem(ServerCommandSource source, Item item) throws CommandSyntaxException {
        source.sendFeedback(new TranslatableText("as.command.show_item",
                new TranslatableText(item.getTranslationKey()),
                itemsHelper.getCurrentCount(item),
                itemsHelper.getDefaultCount(item)),false);
        return 1;
    }

    private static int showAll(ServerCommandSource source) throws CommandSyntaxException {
        LinkedList<Item> list = itemsHelper.getAllModifiedItem();
        if (list.isEmpty()){
            source.sendFeedback(new TranslatableText("as.command.show_none"), false);
        }
        for (Item item : list) {
            source.sendFeedback(new TranslatableText("as.command.show_item",
                    new TranslatableText(item.getTranslationKey()),
                    itemsHelper.getCurrentCount(item),
                    itemsHelper.getDefaultCount(item)),false);
        }
        return 1;
    }

    private static int setItem(ServerCommandSource source, Item item, int count) throws CommandSyntaxException {
        if (count > 64) {
            source.sendError(new TranslatableText("as.command.error_exceeded"));
            return 0;
        } else {
            itemsHelper.setSingle(item, count);
            configManager.syncConfig();
            NetworkHelper.sentConfigToAll(Server.minecraft_server.getPlayerManager().getPlayerList());
            source.sendFeedback(new TranslatableText("as.command.set_item",
                    source.getName(),
                    new TranslatableText(item.getTranslationKey()),
                    count,
                    itemsHelper.getDefaultCount(item)), true);
        }
        return 1;
    }

    private static int setItemOnHand(ServerCommandSource source, ServerPlayerEntity serverPlayerEntity, int count) throws CommandSyntaxException {
        ItemStack itemStack = serverPlayerEntity.getMainHandStack();
        if (itemStack.isEmpty()){
            source.sendError(new TranslatableText("as.command.error_empty_hand", serverPlayerEntity.getName().getString()==source.getName()?"You":serverPlayerEntity.getName()));
            return 0;
        }

        return setItem(source, itemStack.getItem(), count);
    }

    private static int resetItem(ServerCommandSource source, Item item) throws CommandSyntaxException {
        itemsHelper.resetItem(item);
        configManager.syncConfig();
        NetworkHelper.sentConfigToAll(Server.minecraft_server.getPlayerManager().getPlayerList());
        source.sendFeedback(new TranslatableText("as.command.reset_item",
                new TranslatableText(item.getTranslationKey()),
                source.getName()), true);
        return 1;
    }

    private static int resetAll(ServerCommandSource source){
        itemsHelper.resetAll();
        configManager.resetAll();
        NetworkHelper.sentConfigToAll(Server.minecraft_server.getPlayerManager().getPlayerList());
        source.sendFeedback(new TranslatableText("as.command.reset_all",
                source.getName()), true);
        return 1;
    }


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("allstackable").requires(source -> source.hasPermissionLevel(4))

                .then(literal("show")
                        .then(literal("all")
                                .executes(ctx -> showAll((ServerCommandSource) ctx.getSource()))
                        )
                        .then(argument("item", ItemStackArgumentType.itemStack())
                                .executes(ctx -> showItem((ServerCommandSource)ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem()))
                        )

                )
                .then(literal("reset")
                        .then(literal("all")
                                .executes(ctx -> resetAll(ctx.getSource()))
                        )
                        .then(argument("item", ItemStackArgumentType.itemStack())
                                .executes(ctx -> resetItem((ServerCommandSource) ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem()))
                        )
                )
                .then(literal("set")
                        .then(argument("item", ItemStackArgumentType.itemStack())
                                .then(argument("count", IntegerArgumentType.integer(1))
                                        .executes(ctx -> setItem(
                                                (ServerCommandSource) ctx.getSource(),
                                                ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem(),
                                                IntegerArgumentType.getInteger(ctx, "count"))))
                        )
                        .then(literal("hand")
                                .then(argument("targets", EntityArgumentType.player())
                                        .then(argument("count", IntegerArgumentType.integer(1))
                                                .executes(ctx -> setItemOnHand(
                                                        (ServerCommandSource) ctx.getSource(),
                                                        EntityArgumentType.getPlayer(ctx,"targets"),
                                                        IntegerArgumentType.getInteger(ctx, "count"))))
                                )
                        )
                );

        dispatcher.register(literalArgumentBuilder);

    }

}
