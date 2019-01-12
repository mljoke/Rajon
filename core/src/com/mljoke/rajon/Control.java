package com.mljoke.rajon;

public abstract class Control {

    private static final float lifeTime = 1f, isRun = 0.8f;
    private static float progress, elapsed;

    public static void run(float delta) {
        elapsed += delta;
        if (elapsed > lifeTime) elapsed = lifeTime;
        else if (elapsed < isRun) elapsed = isRun;
        progress = (elapsed + (lifeTime - elapsed) * 0.01f) * 80;
    }
    public static float getFoV() {
        return progress;
    }
}
