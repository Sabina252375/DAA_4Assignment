import java.util.*;
/** Implement these algorithms. docs/guide/CONTRACTS.md defines each field and limit. */
public final class Algorithms {
    /** Compare two versions of a sequence while preserving the order of shared characters. Implement lcs according to docs/guide/CONTRACTS.md. */

static Map<String,Object> lcs(Map<String,Object> input) {
    String a = (String) input.get("a");
    String b = (String) input.get("b");
    int n = a.length();
    int m = b.length();
    int[][] dp = new int[n + 1][m + 1];
    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1] + 1;
            } else {
                dp[i][j] = Math.max(
                    dp[i - 1][j],
                    dp[i][j - 1]
                );
            }
        }
    }
    StringBuilder result = new StringBuilder();
    int i = n;
    int j = m;
    while (i > 0 && j > 0) {
        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            result.append(a.charAt(i - 1));
            i--;
            j--;
        } else if (dp[i - 1][j] >= dp[i][j - 1]) {
            i--;
        } else {
            j--;
        }
    }
    result.reverse();
    Map<String,Object> output = new LinkedHashMap<>();
    output.put("length", dp[n][m]);
    output.put("sequence", result.toString());
    return output;
}

    /** Plan the cheapest sequence of adjacent block merges when the original order must be preserved. Implement merge according to docs/guide/CONTRACTS.md. */

static Map<String,Object> merge(Map<String,Object> input) {
    List<?> sizes = (List<?>) input.get("sizes");
    int n = sizes.size();
    long[] prefix = new long[n + 1];
    for (int i = 0; i < n; i++) {
        prefix[i + 1] = prefix[i]
            + ((Number) sizes.get(i)).longValue();
    }
    long[][] dp = new long[n][n];
    int[][] split = new int[n][n];
    for (int len = 2; len <= n; len++) {
        for (int l = 0; l + len <= n; l++) {
            int r = l + len - 1;
            dp[l][r] = Long.MAX_VALUE;
            long sum = prefix[r + 1] - prefix[l];
            for (int k = l; k < r; k++) {
                long cost = dp[l][k]
                          + dp[k + 1][r]
                          + sum;
                if (cost < dp[l][r]) {
                    dp[l][r] = cost;
                    split[l][r] = k;
                }
            }
        }
    }
    List<List<Integer>> plan = new ArrayList<>();
    if (n > 1) {
        buildMergePlan(0, n - 1, split, plan);
    }
    Map<String,Object> output = new LinkedHashMap<>();
    output.put("cost", n == 0 ? 0L : dp[0][n - 1]);
    output.put("plan", plan);
    return output;
}

static void buildMergePlan(
    int l,
    int r,
    int[][] split,
    List<List<Integer>> plan){
    if (l >= r) {
        return;
    }
    int k = split[l][r];
    buildMergePlan(l, k, split, plan);
    buildMergePlan(k + 1, r, split, plan);
    plan.add(Arrays.asList(l, k, r));
}


    /** Choose a maximum-value set when neighbouring vertices are incompatible and conflicts form a tree. Implement tree according to docs/guide/CONTRACTS.md. */

static Map<String,Object> tree(Map<String,Object> input) {
    int n = ((Number) input.get("n")).intValue();
    List<?> weightsInput = (List<?>) input.get("weights");
    List<?> edges = (List<?>) input.get("edges");
    long[] weights = new long[n];
    for (int i = 0; i < n; i++) {
        weights[i] =
            ((Number) weightsInput.get(i)).longValue();
    }
    List<List<Integer>> graph = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        graph.add(new ArrayList<>());
    }
    for (Object edgeObject : edges) {
        List<?> edge = (List<?>) edgeObject;
        int u = ((Number) edge.get(0)).intValue();
        int v = ((Number) edge.get(1)).intValue();
        graph.get(u).add(v);
        graph.get(v).add(u);
    }
    Map<String,Object> output = new LinkedHashMap<>();
    if (n == 0) {
        output.put("weight", 0L);
        output.put("selected", new ArrayList<Integer>());
        return output;
    }
    int[] parent = new int[n];
    Arrays.fill(parent, -1);
    int[] order = new int[n];
    int count = 0;
    int[] stack = new int[n];
    int top = 0;
    stack[top++] = 0;
    parent[0] = 0;
    while (top > 0) {
        int u = stack[--top];
        order[count++] = u;
        for (int v : graph.get(u)) {
            if (parent[v] == -1) {
                parent[v] = u;
                stack[top++] = v;
            }
        }
    }
    long[] take = new long[n];
    long[] skip = new long[n];
    for (int i = count - 1; i >= 0; i--) {
        int u = order[i];
        take[u] = weights[u];
        skip[u] = 0;
        for (int v : graph.get(u)) {
            if (parent[v] == u) {
                take[u] += skip[v];
                skip[u] += Math.max(
                    take[v],
                    skip[v]
                );
            }
        }
    }
    List<Integer> selected = new ArrayList<>();
    int[] nodes = new int[n];
    boolean[] parentTaken = new boolean[n];
    top = 0;
    nodes[top] = 0;
    parentTaken[top++] = false;
    while (top > 0) {
        top--;
        int u = nodes[top];
        boolean blocked = parentTaken[top];
        boolean choose = !blocked && take[u] > skip[u];
        if (choose) {
            selected.add(u);
        }
        for (int v : graph.get(u)) {
            if (parent[v] == u) {
                nodes[top] = v;
                parentTaken[top++] = choose;
            }
        }
    }
    Map<String,Object> result = new LinkedHashMap<>();
    result.put("weight", Math.max(take[0], skip[0]));
    result.put("selected", selected);
    return result;
}

    /** Establish a simple exact-search baseline against which the other two searches can be checked. Implement naive according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> naive(Map<String,Object> input) {
    String text = (String) input.get("text");
    String pattern = (String) input.get("pattern");
    List<Integer> positions = new ArrayList<>();
    int n = text.length();
    int m = pattern.length();
    if (m == 0) {
        for (int i = 0; i <= n; i++) {
            positions.add(i);
        }
    } else {
        for (int i = 0; i <= n - m; i++) {
            boolean match = true;
            for (int j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                positions.add(i);
            }
        }
    }
    Map<String,Object> output = new LinkedHashMap<>();
    output.put("positions", positions);
    output.put("count", positions.size());
    return output;
}
    /** Reuse information about matched prefixes to avoid restarting the search after every mismatch. Implement kmp according to docs/guide/CONTRACTS.md. */

static Map<String,Object> kmp(Map<String,Object> input) {
    String text = (String) input.get("text");
    String pattern = (String) input.get("pattern");
    List<Integer> positions = new ArrayList<>();
    int n = text.length();
    int m = pattern.length();
    if (m == 0) {
        for (int i = 0; i <= n; i++) {
            positions.add(i);
        }
    } else {
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        int textIndex = 0;
        int patternIndex = 0;
        while (textIndex < n) {
            if (text.charAt(textIndex) ==
                pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;
            }
            if (patternIndex == m) {
                positions.add(textIndex - patternIndex);
                patternIndex = lps[patternIndex - 1];
            } else if (textIndex < n &&
                       text.charAt(textIndex) !=
                       pattern.charAt(patternIndex)) {
                if (patternIndex != 0) {
                    patternIndex = lps[patternIndex - 1];
                } else {
                    textIndex++;
                }
            }
        }
    }
    Map<String,Object> output = new LinkedHashMap<>();
    output.put("positions", positions);
    output.put("count", positions.size());
    return output;
}
    /** Reject windows with unequal fingerprints and verify every equal-hash candidate. Implement rk according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> rk(Map<String,Object> input) {
    String text = (String) input.get("text");
    String pattern = (String) input.get("pattern");
    List<Integer> positions = new ArrayList<>();
    int n = text.length();
    int m = pattern.length();
    int base = 31;
    int mod = 101;
    if (m == 0) {
        for (int i = 0; i <= n; i++) {
            positions.add(i);
        }
    } else if (m <= n) {
        int patternHash = 0;
        int windowHash = 0;
        int power = 1;
        for (int i = 0; i < m - 1; i++) {
            power = (power * base) % mod;
        }
        for (int i = 0; i < m; i++) {
            patternHash =
                (patternHash * base + pattern.charAt(i)) % mod;

            windowHash =
                (windowHash * base + text.charAt(i)) % mod;
        }
        for (int i = 0; i <= n - m; i++) {
            if (patternHash == windowHash) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    positions.add(i);
                }
            }
            if (i < n - m) {
                windowHash =
                    (windowHash -
                     text.charAt(i) * power % mod + mod) % mod;
                windowHash =
                    (windowHash * base +
                     text.charAt(i + m)) % mod;
            }
        }
    }
    Map<String,Object> output = new LinkedHashMap<>();
    output.put("positions", positions);
    output.put("count", positions.size());
    return output;
}
    /** Order the text suffixes and use adjacent common prefixes to find repeated substrings. Implement suffix according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> suffix(Map<String,Object> input) {
    String text = (String) input.get("text");
    int n = text.length();

    Map<String,Object> output = new LinkedHashMap<>();

    if (n == 0) {
        output.put("sa", new ArrayList<Integer>());
        output.put("lcp", new ArrayList<Integer>());
        output.put("repeated", "");
        return output;
    }
    Integer[] order = new Integer[n];
    int[] rank = new int[n];

    for (int i = 0; i < n; i++) {
        order[i] = i;
        rank[i] = text.charAt(i);
    }

    for (int k = 1; k < n; k *= 2) {
        final int[] currentRank = rank;
        final int step = k;
        Arrays.sort(order, (a, b) -> {
            if (currentRank[a] != currentRank[b]) {
                return Integer.compare(
                    currentRank[a],
                    currentRank[b]
                );
            }
            int rankA = a + step < n
                ? currentRank[a + step] : -1;
            int rankB = b + step < n
                ? currentRank[b + step] : -1;
            return Integer.compare(rankA, rankB);
        });
        int[] newRank = new int[n];
        newRank[order[0]] = 0;
        for (int i = 1; i < n; i++) {
            int a = order[i - 1];
            int b = order[i];
            int aSecond = a + k < n
                ? rank[a + k] : -1;
            int bSecond = b + k < n
                ? rank[b + k] : -1;
            if (rank[a] != rank[b] ||
                aSecond != bSecond) {
                newRank[b] = newRank[a] + 1;
            } else {
                newRank[b] = newRank[a];
            }
        }
        rank = newRank;
        if (rank[order[n - 1]] == n - 1) {
            break;
        }
    }
    int[] sa = new int[n];
    for (int i = 0; i < n; i++) {
        sa[i] = order[i];
    }
    int[] inverse = new int[n];
    for (int i = 0; i < n; i++) {
        inverse[sa[i]] = i;
    }
    int[] lcp = new int[n];
    int h = 0;
    for (int i = 0; i < n; i++) {
        int position = inverse[i];
        if (position == 0) {
          h = 0;
          continue;
         }
        int j = sa[position - 1];
        while (i + h < n &&
               j + h < n &&
               text.charAt(i + h) == text.charAt(j + h)) {
            h++;
        }
        lcp[position] = h;
        if (h > 0) {
            h--;
        }
    }
    int maxLength = 0;
    int bestStart = 0;
    for (int i = 1; i < n; i++) {
        if (lcp[i] > maxLength) {
            maxLength = lcp[i];
            bestStart = sa[i];
        }
    }
    String repeated = text.substring(
        bestStart,
        bestStart + maxLength
    );
    List<Integer> saList = new ArrayList<>();
    List<Integer> lcpList = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        saList.add(sa[i]);
        lcpList.add(lcp[i]);
    }
    output.put("sa", saList);
    output.put("lcp", lcpList);
    output.put("repeated", repeated);
    return output;
}
    /** Build one dictionary automaton and count its patterns in several independent texts. Implement aho according to docs/guide/CONTRACTS.md. */
    static class AhoNode {
    int[] next = new int[95];
    int fail = 0;

    AhoNode() {
        Arrays.fill(next, -1);
    }
}
    static Map<String,Object> aho(Map<String,Object> input) {
    List<?> patterns = (List<?>) input.get("patterns");
    List<?> texts = (List<?>) input.get("texts");
    int p = patterns.size();
    List<AhoNode> nodes = new ArrayList<>();
    nodes.add(new AhoNode());
    int[] terminal = new int[p];
    for (int i = 0; i < p; i++) {
        String pattern = (String) patterns.get(i);
        int current = 0;
        for (int j = 0; j < pattern.length(); j++) {
            int c = pattern.charAt(j) - 32;
            if (nodes.get(current).next[c] == -1) {
                nodes.get(current).next[c] = nodes.size();
                nodes.add(new AhoNode());
            }
            current = nodes.get(current).next[c];
        }
        terminal[i] = current;
    }
    Queue<Integer> queue = new ArrayDeque<>();
    List<Integer> order = new ArrayList<>();
    order.add(0);
    for (int c = 0; c < 95; c++) {
        int child = nodes.get(0).next[c];
        if (child == -1) {
            nodes.get(0).next[c] = 0;
        } else {
            nodes.get(child).fail = 0;
            queue.add(child);
        }
    }
    while (!queue.isEmpty()) {
        int u = queue.remove();
        order.add(u);
        int failure = nodes.get(u).fail;
        for (int c = 0; c < 95; c++) {
            int child = nodes.get(u).next[c];
            if (child == -1) {
                nodes.get(u).next[c] =
                    nodes.get(failure).next[c];
            } else {
                nodes.get(child).fail =
                    nodes.get(failure).next[c];
                queue.add(child);
            }
        }
    }
    List<List<Long>> allCounts = new ArrayList<>();

    for (Object textObject : texts) {
        String text = (String) textObject;
        long[] visits = new long[nodes.size()];
        int current = 0;
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i) - 32;
            current = nodes.get(current).next[c];
            visits[current]++;
        }
        for (int i = order.size() - 1; i > 0; i--) {
            int u = order.get(i);
            int failure = nodes.get(u).fail;
            visits[failure] += visits[u];
        }
        List<Long> counts = new ArrayList<>();
        for (int i = 0; i < p; i++) {
            counts.add(visits[terminal[i]]);
        }
        allCounts.add(counts);
    }
    Map<String,Object> output = new LinkedHashMap<>();
    output.put("counts", allCounts);
    return output;
}
}
