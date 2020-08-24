package me.connlost.allstackable.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.connlost.allstackable.config.ConfigManager;
import me.connlost.allstackable.util.IItemMaxCount;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;

final public class CommandSetMaxCount {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandManager.literal("allstackable").requires(source -> source.hasPermissionLevel(4))

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
                )
            );
        });
    }

    private static int revertAll(ServerCommandSource source){
        for (Map.Entry<String, Integer> entry : ConfigManager.getConfigManager().getEntry()) {
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
        int maxCount = item.getMaxCount();
        StringBuilder sb = new StringBuilder();
        sb.append(item.toString())
                .append(" has maximum stackable count ")
                .append(item.getMaxCount());
        return sb.toString();
    }

    private static int listAllModifiedItems(ServerCommandSource source) {
        for (Map.Entry<String, Integer> entry : ConfigManager.getConfigManager().getEntry()) {
            source.sendFeedback(new LiteralText(info(Registry.ITEM.get(new Identifier(entry.getKey())))), false);
        }
        return Command.SINGLE_SUCCESS;
    }


    private static int getMaxCount(ServerCommandSource source, Item item) {
        source.sendFeedback(new LiteralText(info(item)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setMaxCount(ServerCommandSource source, Item item, int count) {
        int originCount = item.getMaxCount();
        if (count == originCount) {
            source.sendFeedback(new LiteralText(item.toString() + " unchanged: " + originCount), false);
        } else if (count > 64) {
            source.sendFeedback(new LiteralText("You could only set it up to 64!"), false);
        } else {

            ((IItemMaxCount) item).setMaxCount(count);
            source.sendFeedback(new LiteralText(source.getName() + " has set stackable count of " + item.toString() + " to " + count), true);
            ConfigManager.getConfigManager().addConfig(item.toString(), count);
        }
        return Command.SINGLE_SUCCESS;
    }


}
