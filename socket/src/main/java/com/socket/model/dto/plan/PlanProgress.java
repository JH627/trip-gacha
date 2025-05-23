package com.socket.model.dto.plan;

public enum PlanProgress {
    SELECT_ACCOMMODATION(20),
    SELECT_TOURIST_SPOTS(40),
    FINALIZE_DESTINATIONS(60),
    REVIEW_AND_EDIT(80),
    COMPLETE(100);

    private final int progress;

    PlanProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress * 20;
    }

    public PlanProgress next() {
        int ordinal = this.ordinal();
        PlanProgress[] values = PlanProgress.values();
        if (ordinal < values.length - 1) {
            return values[ordinal + 1];
        } else {
            return this; // 마지막 단계면 그대로 반환
        }
    }

    public PlanProgress prev() {
        int ordinal = this.ordinal();
        PlanProgress[] values = PlanProgress.values();
        if (ordinal > 0) {
            return values[ordinal - 1];
        } else {
            return this; // 첫 단계면 그대로 반환
        }
    }
}
