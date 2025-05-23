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
        return progress;
    }
}
