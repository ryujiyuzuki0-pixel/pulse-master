package dev.pulse.client.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public final class SliderSetting extends Setting {
    public final double min, max, step;
    public final String suffix;
    public double value;

    public SliderSetting(String name, double min, double max, double step, double value, String suffix) {
        super(name);
        this.min = min;
        this.max = max;
        this.step = step;
        this.suffix = suffix;
        set(value);
    }

    public void set(double v) {
        v = Math.max(min, Math.min(max, v));
        v = Math.round((v - min) / step) * step + min;
        value = Math.round(v * 10000.0) / 10000.0;
    }

    public double normalized() {
        return (value - min) / (max - min);
    }

    public void setNormalized(double t) {
        set(min + Math.max(0, Math.min(1, t)) * (max - min));
    }

    public String format() {
        if (step >= 1) return (int) Math.round(value) + suffix;
        return String.format("%.2f", value) + suffix;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromJson(JsonElement e) {
        set(e.getAsDouble());
    }
}
