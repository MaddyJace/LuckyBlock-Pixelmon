package net.luckyblockpixelmon.maddyjace.util;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class WeightedRandom<T> {
    private final NavigableMap<Double, T> map = new TreeMap<>();
    private final Random random = new Random();
    private double total = 0;

    public void add(double weight, T item) {
        if (weight <= 0) return;
        total += weight;
        map.put(total, item);
    }

    public T next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }

    /**
     * 通过传入传说史诗普通概率抽奖，并反馈抽中的(legendary, epic, normal)字符串！
     *
     * @param legendary 接收 legendary 的 probability 配置值！
     * @param epic 接收 epic 的 probability 配置值！
     * @param normal 接收 normal 的 probability 配置值！
     */
    public static String startPrizeLottery(double legendary, double epic, double normal) {
        WeightedRandom<String> weightedRandom = new WeightedRandom<>();
        weightedRandom.add(legendary, "LEGENDARY");
        weightedRandom.add(epic, "EPIC");
        weightedRandom.add(normal, "NORMAL");
        return weightedRandom.next();
    }
    public static String noLegendaryLottery(double epic, double normal) {
        WeightedRandom<String> weightedRandom = new WeightedRandom<>();
        weightedRandom.add(epic, "EPIC");
        weightedRandom.add(normal, "NORMAL");
        return weightedRandom.next();
    }
    public static String noEpicLottery(double legendary, double normal) {
        WeightedRandom<String> weightedRandom = new WeightedRandom<>();
        weightedRandom.add(legendary, "LEGENDARY");
        weightedRandom.add(normal, "NORMAL");
        return weightedRandom.next();
    }

}


