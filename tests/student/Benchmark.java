import java.util.*;

public class Benchmark {
    interface Search {
        Map<String, Object> run(Map<String, Object> input);
    }

    static class Entry {
        String name;
        Search search;

        Entry(String name, Search search) {
            this.name = name;
            this.search = search;
        }
    }

    static volatile long sink = 0;
    static final int REPS = 5000;

    static String randomString(int length, Random random) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append((char) ('a' + random.nextInt(26)));
        }
        return s.toString();
    }

    static void test(String type, int n, int m, String text, String pattern,
                     Entry[] algorithms) {

        Map<String, Object> input = new HashMap<>();
        input.put("text", text);
        input.put("pattern", pattern);

        List<Integer> expected =
            (List<Integer>) Algorithms.naive(input).get("positions");

        // Check correctness before measuring time
        for (Entry algorithm : algorithms) {
            List<Integer> actual =
                (List<Integer>) algorithm.search.run(input).get("positions");

            if (!expected.equals(actual)) {
                throw new RuntimeException(
                    algorithm.name + " returned incorrect results"
                );
            }
        }

        // JVM warm-up
        for (int w = 0; w < 1000; w++) {
            for (Entry algorithm : algorithms) {
                algorithm.search.run(input);
            }
        }

        // Three measured runs
        for (int run = 1; run <= 3; run++) {
            List<Entry> order = new ArrayList<>(Arrays.asList(algorithms));
            Collections.shuffle(order, new Random(1000L + n * 10L + run));

            for (Entry algorithm : order) {
                long start = System.nanoTime();

                for (int i = 0; i < REPS; i++) {
                    Map<String, Object> result =
                        algorithm.search.run(input);

                    sink += ((List<Integer>)
                        result.get("positions")).size();
                }

                long end = System.nanoTime();
                double milliseconds =
                    (end - start) / 1_000_000.0 / REPS;

                System.out.printf(
                    Locale.US,
                    "%s,%d,%d,%s,run%d,%.6f%n",
                    type, n, m, algorithm.name, run, milliseconds
                );
            }
        }
    }

    public static void main(String[] args) {
        Entry[] algorithms = {
            new Entry("Naive", Algorithms::naive),
            new Entry("KMP", Algorithms::kmp),
            new Entry("RK", Algorithms::rk)
        };

        System.out.println(
            "Case,n,m,Algorithm,Run,Time_ms"
        );

        int[][] sizes = {
            {500, 50},
            {1500, 150},
            {3000, 300}
        };

        for (int[] size : sizes) {
            int n = size[0];
            int m = size[1];

            Random random = new Random(20261108L + n);

            String randomText = randomString(n, random);
            int patternStart = random.nextInt(n - m + 1);
            String randomPattern = randomText.substring(patternStart, patternStart + m);

            test(
                "Random", n, m,
                randomText, randomPattern, algorithms
            );

            String repetitiveText = "a".repeat(n);
            String repetitivePattern = "a".repeat(m - 1) + "b";

            test(
                "Repetitive", n, m,
                repetitiveText, repetitivePattern, algorithms
            );
        }

        System.err.println("Checksum: " + sink);
    }
}
