package dev.pulse.client.module;

public enum Category {
    HUD("HUD"), COMBAT("Combat"), UTILITY("Utility"), PERFORMANCE("Performance"), COSMETICS("Cosmetics"), MUSIC("Music");

    public final String label;

    Category(String label) {
        this.label = label;
    }
}
