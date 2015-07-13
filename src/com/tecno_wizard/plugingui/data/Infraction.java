package com.tecno_wizard.plugingui.data;

import java.io.Serializable;

/**
 * Created by Ethan Zeigler on 7/7/2015 for PunismentGUI.
 */
public class Infraction implements Serializable{
    private PunishType type;
    private String reason;
    private long date;
    private String givenBy;

    public Infraction(PunishType type, String reason, long date, String givenBy) {
        this.type = type;
        this.reason = reason;
        this.date = date;
        this.givenBy = givenBy;
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
