package com.examp.airhome.gesturelock;

/**
 * Created by airhome on 2015/11/30.
 */
public class Point {
    public enum State {
        NORMAL, SELECTED, ERROR
    }

    public float x;
    public float y;
    public State state = State.NORMAL;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
