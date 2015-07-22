package com.tecno_wizard.plugingui.data;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Ethan Zeigler on 7/7/2015 for PunismentGUI.
 */
public class Infraction{
    private PunishType type;
    private String reason;
    private Long date;
    private String givenBy;

    public Infraction(PunishType type, String reason, long date, String givenBy) {
        this.type = type;
        this.reason = reason;
        this.date = date;
        this.givenBy = givenBy;
    }

    @Override
    public String toString() {
        return type.toString() + "#" + reason.toString() + "#" + date.toString() + "#" + givenBy.toString();
    }

    public static Infraction fromString(String input) {
        String[] args = input.split("#");
        PunishType type = PunishType.valueOf(args[0]);
        String reason = args[1];
        Long date = Long.parseLong(args[2]);
        String givenBy = args[3];
        return new Infraction(type, reason, date, givenBy);
    }

    public long getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }

    public PunishType getType() {
        return type;
    }

    public String getGivenBy() {
        return givenBy;
    }

}
