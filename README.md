# Preface
Since I started developing MineCraft server plugins and was exposed to the operation and maintenance work of MineCraft servers, I have found managing and defining commands in the server to be a very troublesome works. Sometimes I only need a command to send a sentence after entering the main command, so I need to specifically write a new plugin and implement a new command executor.  
I cannot find any plugins that meet the functional requirements (or they are difficult to use), making them unfriendly to some new server owners. Therefore, I have decided to write a simple command management plugin that comes with some common function functions and can be edited directly in YAML file. That is **LiteCommandEditor**.  
Although the functionality of such a "pseudo script" plugin is limited, the upper limit of the functionality depends on your level of thinking and logical thinking, and the lower limit is as low as you can use it as long as you can edit the configuration file.  

# What is LiteCommandEditor
LiteCommandEditor is a plugin used for managing commands, freely creating and editing commands. It can be easily used without requiring you to have a certain understanding of programming languages, and the editing difficulty is equivalent to editing a GUI for a menu plugin. You can freely create a simple command based on the comments in the plugin's configuration file or the help in the wiki.  
You can set the name, usage permissions, executor functions, TAB completion function, and other functions of this command in a command configuration file. Command executors support infinite nested functions, function triggering conditions, etc; The basic functions include: sending messages, broadcasting, executing other commands, playing sounds, playing title messages, giving items, etc.   
By the way, the plugin also presets some player functions and world functions through the Bukkit API for more advanced editing.  
In addition to the main function of "custom commands", plugin also include other command management functions. For example, disabled command (Delete a command from this server), redirect command (similar to EasyCommand, directing command input to another command), and so on.  

## Dependencies
- Runtime environment for Java8 or above.
- PlaceholderAPI (Optional)

---

### Create a new custom command: [**Click here to start**](https://github.com/TRCStudioDean/LiteCommandEditor/wiki/Basic-Settings-Document)

---

## Features

### Custom Command (Major Function)
**By using a simple YAML configuration file, the following functions can be achieved:**
- Basic command settings: Prefix, Name, Alias, Permission, Usage, etc.
- **Command Executor** (Functions can be infinitely nested)
  - **Functions**:
    - Send messages: `Messages`
    - Broadcast: `Broadcast`
    - Execute other commands: `Commands`
    - Reward items: `Reward-Items`
    - Take items: `Take-Items`
    - Server teleport: `Server-Teleport` (Need BungeCord or Velocity)
    - Send titles: `Titles` (Can only be used by players)
    - Send action-bars: `Action-Bars` (Can only be used by players)
    - Play sounds: `Sounds` (Can only be used by players)
    - Set placeholders: `Set-Placeholders` (Set or remove a placeholder that can be used anywhere)
    - Configurator: `Configurator` (Management of configuration tables, as well as saving to and loading from files)
    - Preset player functions: `Player-Functions` (More functions provided by Bukkit API with a specific player as an instance)
    - Preset world function: `World-Functions` (More functions provided by Bukkit API with a specific world as an instance)
    - Preset server function: `Server-Functions` (Some server functions provided by Bukkit API)
    - Compound functions: `Compound-Functions` (Put multiple **functions** into a list for execution)
    - Sub-functions: `Functions` (Functions can have sub functions and can be infinitely nested)
  - **Attributes**:
    - Break out of a Functions block: `Break`
    - Function permission: `Permission` 
    - Function priority: `Priority`
    - Function execution sequence: `Sequence`
    - Function conditions: `Conditions`
      - Comparison object: `Comparison`
      - Has item: `HasItem`
      - Has permission: `Permission`
      - Has placeholder: `HasPlaceholder`
      - Player attributes: `Player` (More player attributes provided by some BukkitAPI)
      - World attributes: `World` (More world attributes provided by some BukkitAPI)
- **TAB Completer** (Functions can be infinitely nested)
  - *After executing all functions, the merged results of all recipes will be returned as the automatic filling content for this time*
  - **Functions**:
    - Recipes of TAB result: `Recipes`
      - Text: `Text` (It can be a combination of placeholders and text)
      - Online players: `Players` (Can be a regular expression)
      - Item IDs: `Items` (Can be a regular expression)
      - Sound IDs: `Sounds` (Can be a regular expression)
      - TAB results of other commands:  `Commands` (Simulate TAB when entering specific commands)
    - Exceptions of Recipes: `Exceptions` (If the same content exists in the recipe, it will be removed)
    - Sub-functions: `Functions` (Functions can have sub functions and can be infinitely nested)
  - **Attributes**:
    - Break out of a Functions block: `Break`
    - Function permission: `Permission` 
    - Function conditions: `Conditions`
      - Comparison object: `Comparison`
      - Has item: `HasItem`
      - Has permission: `Permission`
      - Has placeholder: `HasPlaceholder`
      - Player attributes: `Player` (More player attributes provided by some BukkitAPI)
      - World attributes: `World` (More world attributes provided by some BukkitAPI)
- JSON Components (You can edit JSON component placeholders directly here to distinguish them from other commands.)
> JSON component is a functional text that acts on the chat bar, such as a window displayed when the mouse hovers over text, clicking on text to execute commands, and so on.
- Item Collection (You can use commands to add item in hand to collection, or simply customize an item to collection.)
> Item collection can be used in some settings related to item IDs based on the name you defined for them (not the name displayed on the item)

### Other Features
- Manage, list, view, and delete specified Command Executor (Other plugins or system commands such as/op, /plugins, and even /stop can be removed)
- Command alias/Easy command settings (Create a new command and direct it to an existing command or its subcommands)
- Command cooldown (can also take effect on commands from other plugins, support adding cooldown to sub commands)
- Support PlaceholderAPI
- Custom JSON Components (JSON component is a functional text that acts on the chat bar, such as a window displayed when the mouse hovers over text, clicking on text to execute commands, and so on)
- Debug mode (Track and record the running status of custom command functions, and provide feedback to the operator)
- Hot load and unload custom command configuration files
- Item Collection (Serialize and save the item in hand to the configuration file for easy use of NBT and other data of the item in the custom command configuration file)
- *And more...*
---

### Plugin Commands
|Command (Main is /litecommandeditor or /lce) |Feature 
|:- |:- 
|/litecommandeditor reload |Reload all settings
|/litecommandeditor info |View command's info
|/litecommandeditor load |Load a custom command configuration
|/litecommandeditor unload |Unload a command configuration
|/litecommandeditor delete |Delete a command
|/litecommandeditor itemcollection |Manage item collection for command configuration
|/litecommandeditor debug |Debugging mode comprehensive command 
|/litecommandeditor list |List all loaded custom command configurations
|/litecommandeditor listall |List all commands
|/litecommandeditor tools |Tools comprehensive command

---
[![BigImage](https://bstats.org/signatures/bukkit/LiteCommandEditor.svg)](https://bstats.org/plugin/bukkit/LiteCommandEditor/16521)