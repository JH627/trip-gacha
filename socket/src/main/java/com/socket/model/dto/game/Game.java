package com.socket.model.dto.game;

public enum Game {
    DEFAULT("DEFAULT"),
    FAST_CLICK("FAST_CLICK"),
    ROULETTE("ROULETTE"),
    COIN_TOSS("COIN_TOSS"),
    CROCODILIA("CROCODILIA");

    private final String value;

    Game(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 문자열로부터 enum 생성
    public static Game from(String value) {
        for (Game game : Game.values()) {
            if (game.value.equalsIgnoreCase(value)) {
                return game;
            }
        }
        throw new IllegalArgumentException("Unknown game type: " + value);
    }
}
