package online.connlost.allstackable.server.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.text.Text;
import online.connlost.allstackable.server.config.ConfigManager;
import online.connlost.allstackable.util.ItemsHelper;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.LinkedList;

public class StackSizeCommand {

    private static ItemsHelper itemsHelper = ItemsHelper.getItemsHelper();
    private static ConfigManager configManager = ConfigManager.getConfigManager();

    private static int showItem(ServerCommandSource source, Item item) throws CommandSyntaxException {
        source.sendFeedback(() -> Text.translatable("as.command.show_item",
                Text.translatable(item.getTranslationKey()),
                itemsHelper.getCurrentCount(item),
                itemsHelper.getDefaultCount(item)), false);
        return 1;
    }

    private static int showAll(ServerCommandSource source) throws CommandSyntaxException {
        LinkedList<Item> list = itemsHelper.getAllModifiedItems();
        if (list.isEmpty()) {
            source.sendFeedback(() -> Text.translatable("as.command.show_none"), false);
        }
        for (Item item : list) {
            source.sendFeedback(() -> Text.translatable("as.command.show_item",
                    Text.translatable(item.getTranslationKey()),
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
        itemsHelper.setSingle(item, count);
        configManager.syncConfig();
        source.sendFeedback(() -> Text.translatable("as.command.set_item",
                Text.translatable(item.getTranslationKey()),
                count,
                itemsHelper.getDefaultCount(item)), true);
        return 1;
    }

    private static int setItemOnHand(ServerCommandSource source, ServerPlayerEntity serverPlayerEntity, int count) throws CommandSyntaxException {
        Item item = null;
        if ((item = getMainHandItem(source, serverPlayerEntity)) == null) {
            return 0;
        }
        return setItem(source, item, count);
    }

    private static int setMatched(ServerCommandSource source, String type, int originalSize, int newSize) {
        int count = itemsHelper.setMatchedItems(originalSize, newSize, type);
        configManager.syncConfig();
        source.sendFeedback(() ->
                Text.translatable(
                        "as.command.set_matched",
                        count,
                        newSize,
                        type.equals("vanilla") ? Text.translatable("as.command.default") : Text.translatable("as.command.previous"),
                        originalSize
                ),
                true
        );
        return 1;
    }

    private static int resetItem(ServerCommandSource source, Item item) throws CommandSyntaxException {
        itemsHelper.resetItem(item);
        configManager.syncConfig();
        source.sendFeedback(() -> Text.translatable("as.command.reset_item",
                Text.translatable(item.getTranslationKey())), true);
        return 1;
    }

    private static int resetAllItems(ServerCommandSource source) {
        itemsHelper.resetAll(true);
        configManager.resetAllItems();
        source.sendFeedback(() -> Text.translatable("as.command.reset_all"), true);
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
            String u1 = serverPlayerEntity.getName().getContent().toString();
            String u2 = source.getEntity() instanceof ServerPlayerEntity ? source.getName() : "Server";

            source.sendError(Text.translatable("as.command.error_empty_hand", u1.equals(u2) ? Text.translatable("as.command.you") : serverPlayerEntity.getName()));
            return null;
        }
        return itemStack.getItem();
    }

    private static int reloadConfig(ServerCommandSource source) {
        configManager.setupConfig();
        source.sendFeedback(() -> {
            try {
                return Text.translatable("as.command.reloaded", source.getPlayerOrThrow().getName());
            } catch (CommandSyntaxException e) {
                return Text.of(e.getMessage());
            }
        }, true);
        return 1;
    }

    private static int updateGlobalConfig(ServerCommandSource source, boolean allowAutoApply, boolean updateStackableList) {
        configManager.updateGlobalConfig(updateStackableList, allowAutoApply);
        source.sendFeedback(() -> Text.translatable("as.command.updated_glob_conf"), true);
        return 1;
    }

    private static int fromGlobal(ServerCommandSource source) {
        configManager.applyGlobalToLocal();
        source.sendFeedback(() -> Text.translatable("as.command.from_global"), true);
        return 1;
    }

    private static int restore(ServerCommandSource source) {
        if (configManager.restoreBackup()) {
            source.sendFeedback(() -> Text.translatable("as.command.restored"), true);
            return 1;
        } else {
            source.sendError(Text.translatable("as.command.nobk"));
            return 0;
        }
    }

    private static int help(ServerCommandSource source) {
        source.sendFeedback(() -> Text.translatable("as.command.help"), false);
        return 1;
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("allstackable")
                    .requires(source -> source.hasPermissionLevel(configManager.getRuleSetting("permissionLevel")))
                    .then(literal("help").executes(ctx -> help(ctx.getSource())))
                    .then(literal("show")
                            .then(literal("hand")
                                    .then(argument("targets", EntityArgumentType.player())
                                            .executes(ctx -> showItemOnHand(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "targets"))))
                            )
                            .then(literal("all")
                                    .executes(ctx -> showAll(ctx.getSource()))
                            )
                            .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
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
                            .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                    .executes(ctx -> resetItem(ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem()))
                            )
                    )
                    .then(literal("set")
                            .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                    .then(argument("count", IntegerArgumentType.integer(1, 64))
                                            .executes(ctx -> setItem(
                                                    ctx.getSource(),
                                                    ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem(),
                                                    IntegerArgumentType.getInteger(ctx, "count"))))
                            )
                            .then(literal("hand")
                                    .then(argument("targets", EntityArgumentType.player())
                                            .then(argument("count", IntegerArgumentType.integer(1, 64))
                                                    .executes(ctx -> setItemOnHand(
                                                            ctx.getSource(),
                                                            EntityArgumentType.getPlayer(ctx, "targets"),
                                                            IntegerArgumentType.getInteger(ctx, "count"))))
                                    )
                            )
                            .then(literal("vanilla")
                                    .then(argument("vanillaSize", IntegerArgumentType.integer(1, 64))
                                            .then(argument("customSize", IntegerArgumentType.integer(1, 64))
                                                    .executes(ctx -> setMatched(
                                                            ctx.getSource(),
                                                            "vanilla",
                                                            IntegerArgumentType.getInteger(ctx, "vanillaSize"),
                                                            IntegerArgumentType.getInteger(ctx, "customSize")
                                                    ))
                                            )
                                    )
                            )
                            .then(literal("modified")
                                    .then(argument("previousSize", IntegerArgumentType.integer(1, 64))
                                            .then(argument("newSize", IntegerArgumentType.integer(1, 64))
                                                    .executes(ctx -> setMatched(
                                                            ctx.getSource(),
                                                            "modified",
                                                            IntegerArgumentType.getInteger(ctx, "previousSize"),
                                                            IntegerArgumentType.getInteger(ctx, "newSize")
                                                    ))
                                            )
                                    )
                            )
                            .then(literal("all")
                                    .then(argument("previousSize", IntegerArgumentType.integer(1, 64))
                                            .then(argument("newSize", IntegerArgumentType.integer(1, 64))
                                                    .executes(ctx -> setMatched(
                                                            ctx.getSource(),
                                                            "all",
                                                            IntegerArgumentType.getInteger(ctx, "previousSize"),
                                                            IntegerArgumentType.getInteger(ctx, "newSize")
                                                    ))
                                            )
                                    )
                            )
                    )
                    .then(literal("config")
                            .then(literal("reload")
                                    .executes(ctx -> reloadConfig(ctx.getSource()))
                            )
                            .then(literal("loadFromGlobal")
                                    .executes(ctx -> fromGlobal(ctx.getSource()))
                            )
                            .then(literal("saveToGlobal")
                                    .executes(ctx -> updateGlobalConfig(ctx.getSource(), false, true))
                            )
                            .then(literal("globalConfigAutoApply")
                                    .then(literal("true").executes(ctx -> updateGlobalConfig(ctx.getSource(), true, false)))
                                    .then(literal("false").executes(ctx -> updateGlobalConfig(ctx.getSource(), false, false)))
                            )
                            .then(literal("restore")
                                    .executes(ctx -> restore(ctx.getSource()))
                            )

                    );

            dispatcher.register(literalArgumentBuilder);

        });
    }


}
