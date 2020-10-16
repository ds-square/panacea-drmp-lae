package org.panacea.drmp.lae.domain.attackGraph.privLevel;

public enum HumanPrivLevel {
    OWN,
    USE,
    EXECUTE;

    public static boolean contains(String s) {
        for (HumanPrivLevel hpl : HumanPrivLevel.values()) {
            if (hpl.name().equals(s)) return true;
        }
        return false;
    }
}
