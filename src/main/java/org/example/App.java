package org.example;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {
    public final static int NUMBER_OF_ITERATIONS = 1_000_000;
    public final static int THREAD_POOL_SIZE = 5;


    public static Map<String, Integer> synchronizedMap = null;
    public static Map<String, Integer> concurrentHashMapObject = null;

    public static void main(String[] args) throws InterruptedException {

        //Тестирование synchronizedMap
        synchronizedMap = Collections.synchronizedMap(new HashMap<String, Integer>());
        performanceTest(synchronizedMap);

        //Тестирование ConcurrentHashMap
        concurrentHashMapObject = new ConcurrentHashMap<String, Integer>();
        performanceTest(concurrentHashMapObject);

    }

    public static void performanceTest(final Map<String, Integer> testingMap) throws InterruptedException {

        System.out.println("Тестируем: " + testingMap.getClass().getSimpleName());
        long averageTime = 0;
        for (int i = 0; i < 5; i++) {

            long startTime = System.nanoTime();
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            for (int j = 0; j < THREAD_POOL_SIZE; j++) {
                executor.execute(new Runnable() {

                    @Override
                    public void run() {

                        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
                            Integer randomNumber = (int) Math.ceil(Math.random() * 550000);

                            Integer value = testingMap.get(String.valueOf(randomNumber));

                            // добавляем в мап значение
                            testingMap.put(String.valueOf(randomNumber), randomNumber);
                        }
                    }
                });
            }
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

            long entTime = System.nanoTime();
            long totalTime = (entTime - startTime) / 1000000L;
            averageTime += totalTime;
            System.out.println(NUMBER_OF_ITERATIONS + " значений добавлено/извлечено " + totalTime + " мсек");
        }
        System.out.println(testingMap.getClass().getSimpleName() + ": среднее время: " + averageTime / THREAD_POOL_SIZE + " мсек\n");
    }
}