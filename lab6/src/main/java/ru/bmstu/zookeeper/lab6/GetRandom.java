package ru.bmstu.zookeeper.lab6;

import java.util.Random;

public class GetRandom {
    private Random random;

    public GetRandom(Random random) {
        this.random = random;
    }

    public Random getRandom() {
        return random;
    }
}
