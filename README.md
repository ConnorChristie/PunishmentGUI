# PunishmentGUI
Bukkit plugin originally created by: **Ethan160503**, modified by **TheChillerCraft**!

### About
This plugin was created to allow server admins to punish other players in their server.
The admins are allowed to warn players, temporarily mute and ban players and permanently mute and ban players!
This plugin uses a custom inventory GUI that contains the buttons to perform the above actions!

### How to Use
1. Put the PunishmentGUI.jar into your plugins directory
2. Restart or reload your server
3. Add the permissions to whomever is using it
4. Use the command: /punish <player> <reason> to open the GUI
5. Optionally:
    * Edit config to change temporary ban and mute expiration (seconds)
    * Edit all the messages to use your server's message format
    * Message arguments:
        * **{message_prefix}** - The prefix before the message
        * **{message_suffix}** - The suffix after the message
        * **{reason}** - The reason the player was punished
        * **{date}** - The date the punishment was given
        * **{expiration}** - The date the temporary punishment expires
        * **{punished}** - The punished player's name
        * **{punisher}** - The punisher's player's name
        * **{remover}** - The player's name who removed the punishment
        * **{remove_reason}** - The reason the punishment was removed
    * Optionally disable the MOTD status message

### Commands
- **/punish \<player\> \<reason\>** - Opens the punishment GUI for that player
  - Requires permission: punish.use
- **/p \<player\> \<reason\>** - Same as above

### Permissions
- **punish.use** - Gives the ability to use the punishment GUI
- **punish.temp_mute** - Gives the ability to temporarily mute a player
- **punish.temp_ban** - Gives the ability to temporarily ban a player
- **punish.perm_mute** - Gives the ability to permanently mute a player
- **punish.perm_ban** - Gives the ability to permanently ban a player
- **punish.protected** - Makes the player invulnerable to punishments

### Todo
- ~~Add motd status if banned or muted~~
- ~~Add expiration argument to messages~~