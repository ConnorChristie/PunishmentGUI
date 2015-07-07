package com.tecno_wizard.plugingui.core;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.UUID;

/**
 * Created by Ethan Zeigler on 7/7/2015 for PunismentGUI.
 */
public class PlayerFile {
    YamlConfiguration file;

    public PlayerFile(UUID id) {
        file = Resources.getPlayerFile(id);
    }

    public 
}
