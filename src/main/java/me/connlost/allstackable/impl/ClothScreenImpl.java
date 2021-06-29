//package me.connlost.allstackable.impl;
//
//import me.shedaniel.clothconfig2.api.ConfigBuilder;
//import me.shedaniel.clothconfig2.api.ConfigCategory;
//import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.text.LiteralText;
//import net.minecraft.text.TranslatableText;
//
//public class ClothScreenImpl {
//    static String currentValue = "currentValue";
//
//    public static Screen getConfigScreen(Screen parent){
//        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(new TranslatableText("as.name"));
//        // way to save
//        builder.setSavingRunnable(()->{
//
//        });
//        //cate
//        ConfigCategory stackableItems = builder.getOrCreateCategory(new LiteralText("stackableItems"));
//        //option
//        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
//
//        stackableItems.addEntry(entryBuilder.startStrField(new LiteralText("test1"), ClothScreenImpl.currentValue)
//                .setDefaultValue("This is the default value") // Recommended: Used when user click "Reset"
//                .setTooltip(new TranslatableText("This option is awesome!")) // Optional: Shown when the user hover over this option
//                .setSaveConsumer(newValue -> ClothScreenImpl.currentValue = newValue) // Recommended: Called when user save the config
//                .build()); // Builds the option entry for cloth config
//        return builder.build();
//    }
//}
