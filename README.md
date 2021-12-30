# All Stackable

**A highly customizable Fabric Mod that makes items stackable and change the stack size of them.**

***Ever wanted to stack potions, totems, or Shulker Boxes in one slot? Or perhaps hope to sort totems produced by the raid farm using your Redstone machines? This is the mod for you!***

![](https://raw.githubusercontent.com/DawnTeamMC/DawnTeamMC/master/fabric_api/required.png)

![](https://i.imgur.com/31Q4pb2.png "")

![](https://img.shields.io/github/workflow/status/KrisCris/AllStackable/Java%20CI%20with%20Gradle/master)
![](https://img.shields.io/github/license/KrisCris/AllStackable)
[![](http://cf.way2muchnoise.eu/versions/404312.svg)](https://www.curseforge.com/minecraft/mc-mods/all-stackable/files)
[![](https://cf.way2muchnoise.eu/full_404312_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/all-stackable/files)

Video: https://streamable.com/7htqbn ( Issues in the video has already been fixed! )


## Usage

---

**English**

- `/allstackable show all`: list all modified items.
- `/allstackable show [item]`: show information of a specified item.
- `/allstackable show hand [player]`: show information about an item on a player's hand.
- `/allstackable reset all`: revert all changes.
- `/allstackable reset [item]`: reset a specified item.
- `/allstackable reset hand [player]`: reset an item on a player's hand.
- `/allstackable set [itemID] [count]`: set the max stackable count of a specified item.
- `/allstackable set hand [player]`: set the max stackable count of an item on a player's hand.
- `/allstackable set vanilla [default value] [new value]`: Apply **new value** to the stackable size of items whose default size is **default value**. (Only works for items that haven't been modified.)
- `/allstackable set modified [current value] [new value]`: Apply **new value** to the stackable size of items whose current size is **current value**. (Only works for items that have been modified.)
- `/allstackable set all [current value] [new value]`: Apply **new value** to the stackable size of items whose current size is **current value**. (Applies to all items.)
- `/allstackable config reload`: Reload config from file.
- `/allstackable config loadFromGlobal`: load the global config (stored in minecraft's config folder) to your game.
- `/allstackable config saveToGlobal`: Let your current config to be the global config.
- `/allstackable config globalConfigAutoApply [true/false]`: whether the global config will be automatically applied to every new game.
- `/allstackable config restore`: restore to last change

- **If you want to use this mod without OP/cheat, you can manually set the "permissionLevel" from 4 to 0 in the config file (in your save folder).**
- **If you want only stack empty shulkerbox, change the setting manually in the config files (0 -> 1)**

---

**Chinese**

- `/allstackable show all`: 列出所有修改过的物品.
- `/allstackable show [item]`: 显示这个物品的信息.
- `/allstackable show hand [player]`: 显示玩家手上的物品信息.
- `/allstackable reset all`: 撤销所有更改.
- `/allstackable reset [item]`: 撤销对这个物品的更改.
- `/allstackable reset hand [player]`: 撤销玩家手上的物品的更改.
- `/allstackable set [itemID] [count]`: 更改指定物品的堆叠数量.
- `/allstackable set hand [player]`: 更改玩家手上物品的堆叠数量.
- `/allstackable set vanilla [default value] [new value]`: 将所有默认堆叠上限为【default value】的物品的堆叠上限设置为【new value】（只对未被修改过的物品生效）
- `/allstackable set modified [current value] [new value]`: 将所有被修改过堆叠上限，且为【current value】的物品的堆叠上限设置为【new value】（只对被修改过的物品生效）
- `/allstackable set all [current value] [new value]`: 将所有当前堆叠上限为【current value】的物品的堆叠上限设置为【new value】
- `/allstackable config reload`: 重新从文件载入配置文件.
- `/allstackable config loadFromGlobal`: 为你当前游戏载入全局配置（保存在config文件夹里）.
- `/allstackable config saveToGlobal`: 让你当前游戏配置成为全局配置.
- `/allstackable config globalConfigAutoApply [true/false]`: 是否让新游戏自动使用全局配置，true代表是.
- `/allstackable config restore`: 恢复到上次的更变

- **如果你想在非op或者作弊的情况下使用，你可以把存档文件夹中配置文件里的 "permissionLevel" 从4 改到 0.**
- **如果你想只堆叠空的潜影盒，你可以把存档文件夹中配置文件里的 "stackEmptyShulkerBoxOnly" 从0 改到 1.**

### FAQ

[1]. Where is the config file stored?

**They are stored in the folder of your world. (There is also a global config, which would apply its settings to all new games. Only works after you turned it on.)**

[2]. Can I migrate the config from one world to another?

**Just copy and paste the config file to your new world, then use `/allstackable reload` if you're already in the world.**

[3]. What will happen to my world if I delete this mod?

**Don't worry, this mod updates items' stack size dynamically and changes NOTHING to the data (Though you have to manually split those items that are already stacked.). And if you install this mod back, all features come back as long as you didn't remove the config file.**

[4]. Server?

**You have to install this mod on both sides. Server will automatically let clients know if any item needs to be modified and synced.**

**Also, use [LoganDark's mod, fabric-languagehack](https://github.com/LoganDark/fabric-languagehack/releases), or something similar for enabling server-side (server cli) text translation if you want to see feedbacks in the console.**

[5]. Sorting non-stackable items that made stackable by this mod.

**Identical to sorting normal items. https://streamable.com/11hm1a**

[6]. I find a bug! / This mod doesn't work! / Any other Issues

**[Report!](https://github.com/KrisCris/AllStackable/issues/)**

## Notice:

(Some issues that related to how Minecraft's code works)

**[1].** Have a test before using/consuming certain types of stacked items (especially those having special functionalities), since you may lose them all or encounter unexpected behaviors. (Even though most issues are fixed in previous versions, please let me know if you found new bugs.)

## Check out my other mods if you like! :)

 - [TotemPlus - Save you from falling out of the world!](https://www.curseforge.com/minecraft/mc-mods/totem-plus)

---

#### ***Sorry, No plan for Forge.***
