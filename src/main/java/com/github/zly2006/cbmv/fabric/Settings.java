package com.github.zly2006.cbmv.fabric;

public class Settings {
    public boolean villagerOldOffers = false;
    public boolean villagerOldCure = true;
    public boolean oldRaid = true;
    public boolean oldWitchDropIfRaider = true;

    @Override
    public String toString() {
        return "Settings{" +
                "villagerOldOffers=" + villagerOldOffers +
                ", villagerOldCure=" + villagerOldCure +
                ", oldRaid=" + oldRaid +
                ", oldWitchDropIfRaider=" + oldWitchDropIfRaider +
                '}';
    }
}
