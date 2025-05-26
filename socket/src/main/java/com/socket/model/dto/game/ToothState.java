package com.socket.model.dto.game;

public class ToothState {
    private boolean pressed;

    public ToothState(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
}