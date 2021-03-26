package io.penguinstats.util.strategy;

public interface DecryptStrategy {

    String decrypt(String cipherText);

    DecryptStrategyName getStrategyName();

}
