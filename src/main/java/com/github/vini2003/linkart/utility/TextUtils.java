package com.github.vini2003.linkart.utility;

import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.io.Serializable;

public class TextUtils {
    public static LiteralText literal(Serializable value, Formatting formatting) {
        return literal(value.toString(), formatting);
    }

    public static LiteralText literal(String value, Formatting formatting) {
        return (LiteralText) new LiteralText(value).formatted(formatting);
    }

    public static LiteralText literal(Serializable value) {
        return new LiteralText(value.toString());
    }
}
