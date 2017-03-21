package com.lanmo.canary.hash;

/**
 * Created by bowen on 2017/3/17.
 */
public abstract class ShardInfo<T> {
    private int weight;

    public ShardInfo() {
    }

    public ShardInfo(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }

    protected abstract T createResource();

    public abstract String getName();
}
