package org.panacea.drmp.lae.domain.attackGraph.privLevel;

public enum NetworkPrivLevel {
    NONE,
    USER,
    ROOT;

    public static boolean contains(String s) {
        for (NetworkPrivLevel npl : NetworkPrivLevel.values()) {
            if (npl.name().equals(s)) return true;
        }
        return false;
    }
}
