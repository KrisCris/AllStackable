package me.connlost.allstackable.server.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.IItemMaxCount;
import me.connlost.allstackable.util.ItemsHelper;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public class CommandSetMaxCount {

    private static ItemsHelper itemsHelper = ItemsHelper.getItemsHelper();
    private static ConfigManager configManager = ConfigManager.getConfigManager();
    //TODO re-design commands
    //allstackable
    //->list
    //  ->modified
    //      ->[modifiedCount]
    //  ->vanilla [maxCount]
    //->set
    //  ->[item] [count]
    //  ->group
    //      ->vanilla [vanillaMaxCount] [newCount]
    //      ->modified [oldCount] [newCount]
    //      ->all [newCount]
    //->show [item]
    //->revert
    //  ->[item]
    //  ->group
    //      ->[count]
    //->revertall
    //
    //

//    public static void nr(){
//        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
//            dispatcher.register(
//                    (LiteralArgumentBuilder<ServerCommandSource>) CommandManager.literal("allstack")
//                    .requires(source -> source.hasPermissionLevel(4))
//                    .then(CommandManager.literal("list")
//                        .then(CommandManager.literal("modified")
//                            .executes(ctx -> listAllModifiedItems(ctx.getSource()))
//                            .then(CommandManager.argument("count", IntegerArgumentType.integer(1)))
//                                .executes(ctx -> listModifiedSameCount(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "count"))))
//                        .then(CommandManager.literal("vanilla")
//                            .then(CommandManager.argument("count", IntegerArgumentType.integer(1)))
//                                .executes(ctx -> listVanillaSameCount(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "count")))))
//                    .then(CommandManager.literal("set")
//                        .then((RequiredArgumentBuilder) CommandManager.argument("item", ItemStackArgumentType.itemStack())
//                            .then((RequiredArgumentBuilder) CommandManager.argument("count", IntegerArgumentType.integer(1))
//                                .executes(ctx -> setMaxCount(
//                                        ctx.getSource(),
//                                        ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem(),
//                                        IntegerArgumentType.getInteger(ctx, "count")))))
//                            .then(CommandManager.literal("group")
//                                .then(CommandManager.literal("vanilla")
//                                    .then(CommandManager.argument("defaultCount", IntegerArgumentType.integer(1))
//                                        .then(CommandManager.argument("newCount", IntegerArgumentType.integer(1))
//                                            .executes()))))
//                    )
//            );
//        });
//    }

    private static int listAllModifiedItems(ServerCommandSource source) {
        for (Map.Entry<String, Integer> entry : ConfigManager.getConfigManager().getConfigEntry()) {
            source.sendFeedback(new LiteralText(info(Registry.ITEM.get(new Identifier(entry.getKey())))), false);
        }
        return 1;
    }

    private static int listModifiedSameCount(ServerCommandSource source, int count){
        //TODO
        return 1;
    }

    private static int listVanillaSameCount(ServerCommandSource source, int count){
        //TODO
        return 1;
    }

    private static int setMaxCount(ServerCommandSource source, Item item, int count) {
        if (count > 64) {
            source.sendFeedback(new LiteralText("You could only set it up to 64!"), false);
            return 0;
        } else {
            itemsHelper.setSingle(item, count);
            configManager.addConfig(item.toString(), count);
            source.sendFeedback(new LiteralText(source.getName() + " has changed the max stack size of " + info(item)), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>) CommandManager.literal("allstackable").requires(source -> source.hasPermissionLevel(4))

                    .then(CommandManager.literal("revertall")
                            .executes(ctx -> revertAll(ctx.getSource()))
                    )

                    .then(CommandManager.literal("list")
                            .executes(ctx -> listAllModifiedItems((ServerCommandSource) ctx.getSource()))
                    )

                    .then(((RequiredArgumentBuilder) CommandManager.argument("item", ItemStackArgumentType.itemStack())
                            .executes(ctx -> getMaxCount(
                                    (ServerCommandSource) ctx.getSource(),
                                    ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem()
                            )))

                            .then(CommandManager.literal("revert")
                                    .executes(ctx -> revert(
                                            (ServerCommandSource) ctx.getSource(),
                                            ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem()
                                    ))
                            )

                            .then(CommandManager.argument("count", IntegerArgumentType.integer(1))
                                    .executes(ctx -> setMaxCount(
                                            (ServerCommandSource) ctx.getSource(),
                                            ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem(),
                                            IntegerArgumentType.getInteger(ctx, "count"))
                                    )
                            )
                    )
            );
        });
    }

    //TODO Refactor and put some methods to ItemsHelper
    private static int revertAll(ServerCommandSource source){
        for (Map.Entry<String, Integer> entry : ConfigManager.getConfigManager().getConfigEntry()) {
            resetItem(Registry.ITEM.get(new Identifier(entry.getKey())));
        }
        ConfigManager.getConfigManager().removeAllConfig();
        source.sendFeedback(new LiteralText("ALL maxCount settings are reverted to vanilla"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int revert(ServerCommandSource source, Item item){
        resetItem(item);
        ConfigManager.getConfigManager().removeConfig(item.toString());
        source.sendFeedback(new LiteralText("The maxCount of " + item.toString() + " has been reset to " + item.getMaxCount()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static void resetItem(Item item){
        ((IItemMaxCount)item).revert();
    }

    private static String info(Item item){
        int defaultCount = itemsHelper.getDefaultCount(item);
        int currentCount = itemsHelper.getCurrentCount(item);
        StringBuilder sb = new StringBuilder();
        sb.append(item.toString())
                .append(" Current: ")
                .append(currentCount)
                .append(" Default: ")
                .append(defaultCount);
        return sb.toString();
    }




    private static int getMaxCount(ServerCommandSource source, Item item) {
        source.sendFeedback(new LiteralText(info(item)), false);
        return Command.SINGLE_SUCCESS;
    }




}
