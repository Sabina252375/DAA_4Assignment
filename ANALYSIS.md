# Assignment 4 — Analysis

## Environment and commands

I completed this assignment using Java on my MacBook Air.

- Device: MacBook Air (Apple M5)
- CPU: 10 cores
- RAM: 16 GB
- OS: macOS 26.5.1
- Java version: 26.0.2.1
- Python version: 3.14.8

I used the following commands:

```bash
python3 tools/run.py --setup
python3 tools/run.py --check
```

All 44 public tests passed.

Git commit: [insert the final commit hash after committing all submission files]

## Algorithm explanations

### 1. LCS (Longest Common Subsequence)

**Idea:**

LCS finds the longest sequence of characters that appears in both strings in the same order. The characters do not need to be next to each other.

**State:**

`dp[i][j]` stores the length of the longest common subsequence of the first `i` characters of string `a` and the first `j` characters of string `b`.

**Base case:**

If one string is empty, the LCS length is 0. Therefore, the first row and first column of the DP table contain zeros.

**Transition:**

- If the current characters are equal, we add 1 to the previous diagonal value.
- If they are different, we take the maximum value from the top and left cells.

Formula:

```text
If a[i-1] == b[j-1]:
    dp[i][j] = dp[i-1][j-1] + 1

Otherwise:
    dp[i][j] = max(dp[i-1][j], dp[i][j-1])
```

**Processing order:**

The table is filled from left to right and from top to bottom. This order makes sure that the values needed for each cell are already calculated.

**Reconstruction:**

After filling the table, we start from the bottom-right cell and move backwards.

- If characters are equal, we add the character to the result and move diagonally.
- Otherwise, we move to the cell with the larger value.
- If both values are equal, our implementation moves up.

Finally, we reverse the result to get the correct order.

**Correctness:**

The algorithm considers all possible ways to build a common subsequence. It uses the best result from smaller prefixes, so the final cell contains the maximum possible length. Reconstruction follows these decisions and returns a valid subsequence.

**Complexity:**

- Time: O(n × m)
- Auxiliary memory: O((n + 1) × (m + 1)) for the DP table.
- Output memory: O(L), where L is the length of the resulting subsequence.

Here, n and m are the lengths of the two input strings.

**Boundary test:**

Input: `a = ""`, `b = "abc"`

Expected output: `length = 0`, `sequence = ""`.

This test checks whether the algorithm handles an empty string correctly.

### 2. Merge (Interval Dynamic Programming)

**Idea:**

The task is to merge blocks while keeping their original order. We can only merge two adjacent blocks. The goal is to find the minimum total cost.

**State:**

`dp[l][r]` stores the minimum cost of merging all blocks from index `l` to index `r` into one block.

`split[l][r]` stores the position where the interval is divided to get the minimum cost.

**Base case:**

If an interval contains only one block, no merge is needed:

`dp[i][i] = 0`

**Transition:**

We try every possible split position `k` between `l` and `r`.

The cost is:

```text
dp[l][k] + dp[k+1][r] + sum(l, r)
```

Here, `sum(l, r)` is the total size of all blocks in the interval.

We choose the split with the smallest total cost and save it in `split[l][r]`.

**Processing order:**

We calculate intervals by increasing length, starting with length 2. This means smaller intervals are ready before we calculate larger intervals.

Prefix sums are used to calculate interval sums quickly.

**Reconstruction:**

We use the `split` table to build the merge plan.

First, we merge the left and right parts. Then we merge the two resulting blocks. This gives a valid chronological plan.

**Correctness:**

Every valid merge plan has a final split between two adjacent groups. The algorithm checks every possible split and uses the optimal solutions for both smaller groups. Therefore, it finds the minimum total cost.

**Complexity:**

- Time: O(n³)
- Auxiliary memory: O(n²)
- Output memory: O(n), because the plan contains n-1 merges.

**Boundary test:**

Input: `sizes = [5]`

Expected output: `cost = 0`, `plan = []`.

This test checks that a single block does not need to be merged.

### 3. Tree DP (Maximum Weight Independent Set)

**Idea:**

We need to select vertices with the maximum total weight. Two connected vertices cannot both be selected.

The empty set is also allowed, so the answer can be 0.

**State:**

For each vertex `u`, we calculate two values:

- `take[u]` — maximum weight if vertex `u` is selected.
- `skip[u]` — maximum weight if vertex `u` is not selected.

**Base case:**

For a vertex without children:

```text
take[u] = weights[u]
skip[u] = 0
```

**Transition:**

If we select a vertex, we cannot select its children:

```text
take[u] = weights[u] + sum(skip[child])
```

If we do not select it, each child can be selected or skipped:

```text
skip[u] = sum(max(take[child], skip[child]))
```

**Processing order:**

We first build the tree using an adjacency list and choose vertex 0 as the root.

Then we create a traversal order using an iterative stack. We process vertices in reverse order, so children are calculated before their parents.

We use an iterative approach to avoid stack overflow on deep trees.

**Reconstruction:**

We compare `take[u]` and `skip[u]`.

- If the parent is selected, the current vertex must be skipped.
- Otherwise, we select the current vertex only if `take[u] > skip[u]`.
- If both values are equal, our implementation skips the vertex.

**Correctness:**

For every vertex, there are only two possible decisions: take it or skip it. The transitions consider all valid choices for its children. Since the tree has no cycles, processing children before parents gives the optimal result for every subtree.

**Complexity:**

- Time: O(n)
- Auxiliary memory: O(n)
- Output memory: O(n) in the worst case.

Here, `n` is the number of vertices.

**Boundary test:**

Input: `n = 2`, `weights = [-5, -3]`, `edges = [[0, 1]]`

Expected output: `weight = 0`, `selected = []`.

This test checks that the algorithm allows an empty set when all weights are negative.

### 4. Naive String Search

**Idea:**

Naive Search finds all occurrences of a pattern in a text. It checks the pattern at every possible starting position.

**State:**

We use `i` as the starting position in the text and `j` as the position inside the pattern.

**Base cases:**

- If the pattern is empty, every boundary in the text is a valid match.
- If the pattern is longer than the text, there are no matches.

**Transition:**

For every valid starting position `i`, we compare the characters one by one:

```text
text[i + j] == pattern[j]
```

If all characters match, we add `i` to the result.

After each attempt, we move to the next starting position. This allows overlapping matches.

**Processing order:**

We check positions from left to right, so the returned positions are in increasing order.

**Correctness:**

The algorithm checks every possible valid starting position. It reports a position only when all pattern characters match. Therefore, it finds all occurrences, including overlaps.

**Complexity:**

- Time: O(n × m + n + 1)
- Auxiliary memory: O(1)
- Output memory: O(z), where `z` is the number of matches.

Here, `n` is the text length and `m` is the pattern length.

**Boundary test:**

Input: `text = "aaaaa"`, `pattern = "aaa"`

Expected output: `positions = [0, 1, 2]`.

This test checks that overlapping matches are included.

### 5. KMP (Knuth–Morris–Pratt)

**Idea:**

KMP searches for a pattern without checking the same text characters again and again. It uses an LPS array to decide where to continue after a mismatch.

**State:**

`lps[i]` stores the length of the longest proper prefix of the pattern that is also a suffix ending at position `i`.

We also use:

- `textIndex` — current position in the text.
- `patternIndex` — current position in the pattern.

**Base case:**

The first LPS value is 0. If the pattern is empty, every text boundary is returned as a match.

**Transition:**

When two characters match, we increase both indexes.

When characters do not match:

- If `patternIndex` is not 0, we move it to `lps[patternIndex - 1]`.
- Otherwise, we move to the next text character.

After a complete match, we save its starting position and use the LPS value to continue searching. This allows overlapping matches.

**Processing order:**

First, we build the LPS array from left to right. Then we scan the text from left to right using this array.

**Correctness:**

The LPS array stores the prefix information needed after a mismatch. It allows KMP to skip positions that cannot produce a new match. Every valid occurrence is still found, including overlapping occurrences.

**Complexity:**

- Time: O(n + m + z + 1)
- Auxiliary memory: O(m + 1)
- Output memory: O(z + 1)

Here, `n` is the text length, `m` is the pattern length and `z` is the number of matches.

**Boundary test:**

Input: `text = "abababa"`, `pattern = "aba"`

Expected output: `positions = [0, 2, 4]`.

This test checks whether KMP finds overlapping matches after a full match.

### 6. Rabin–Karp Algorithm

**Idea:**

Rabin–Karp uses hashing to search for a pattern. Instead of comparing every window character by character, it first compares hash values. If the hashes are equal, it checks the actual characters.

**State:**

- `patternHash` — hash value of the pattern.
- `windowHash` — hash value of the current text window.
- `power` — highest power of the base used for the rolling hash.
- `i` — starting position of the current window.

**Base cases:**

- If the pattern is empty, every text boundary is a match.
- If the pattern is longer than the text, there are no matches.

**Transition:**

We use base 31 and modulus 101. Character codes are used to calculate the hash.

When the window moves one position to the right, we remove the leftmost character, multiply the remaining hash by the base, and add the new character.

If the pattern hash and window hash are equal, we compare the actual characters. This prevents false matches caused by hash collisions.

**Processing order:**

First, we calculate the initial pattern and window hashes. Then we move the window from left to right and update its hash after each step.

**Correctness:**

Different hashes mean the strings cannot be equal. Equal hashes are only candidates, so the algorithm verifies the characters before reporting a match. Therefore, hash collisions do not produce incorrect results.

**Complexity:**

- Expected time depends on the number of hash candidates.
- Candidate-sensitive time: O(n + m + c × m), where `c` is the number of windows with the same hash as the pattern.
- Worst-case time: O(n × m + n + m + 1).
- Auxiliary memory: O(1), excluding the output.
- Output memory: O(z), where `z` is the number of matches.

Here, `n` is the text length and `m` is the pattern length.

**Boundary test:**

Input: `text = "abdj"`, `pattern = "ab"`

Expected output: `positions = [0]`.

The windows `"ab"` and `"dj"` have the same hash with base 31 and modulus 101. This is a hash collision: two different strings produce the same hash value. The algorithm does not report every hash match as a real match. It compares the actual characters and reports position 0 for `"ab"` only. Therefore, the collision does not create a false match.

### 7. Suffix Array and Kasai LCP

#### 7.1. Suffix Array (Prefix Doubling)

**Idea:**

A suffix is a part of a string starting at a certain position and continuing to the end. The suffix array stores the starting positions of all suffixes in lexicographical order.

**State:**

- `sa` stores the starting positions of suffixes.
- `rank[i]` stores the current rank of the suffix starting at position `i`.
- `k` is the length of the parts compared at each step.

**Base case:**

At the beginning, each suffix is ranked by its first character.

**Transition:**

We sort suffixes using pairs of ranks:

```text
(rank[i], rank[i + k])
```

If the second part is outside the string, its rank is treated as -1.

After sorting, we assign new ranks. Then we double `k` and repeat until all suffixes have different ranks or the whole string has been covered.

**Processing order:**

We start with `k = 1` and double it after every sorting step. Each round uses the ranks calculated in the previous round.

**Correctness:**

Each round compares longer prefixes of the suffixes. When all ranks are different, their order is the lexicographical order of the complete suffixes.

**Complexity:**

- Time: O(n log²(n + 1)), because the implementation uses comparison sorting in each doubling round.
- Auxiliary memory: O(n).
- Output memory: O(n).

**Boundary test:**

Input: `text = ""`

Expected output: `sa = []`.

This test checks whether the algorithm handles an empty string without creating an empty suffix.

#### 7.2. Kasai LCP Algorithm

**Idea:**

Kasai calculates the Longest Common Prefix (LCP) between neighbouring suffixes in the suffix array.

**State:**

- `inverse[i]` stores the position of suffix `i` in the suffix array.
- `lcp[i]` stores the common prefix length between `sa[i-1]` and `sa[i]`.
- `h` stores the current common prefix length.

**Base case:**

`lcp[0] = 0`, because the first suffix has no previous suffix.

**Transition:**

For every starting position `i`, we find its previous suffix in the sorted order and compare their characters.

If characters match, we increase `h`. After calculating the LCP, we decrease `h` by one before moving to the next position.

If the suffix is first in the sorted order, we reset `h` to zero.

**Processing order:**

We process the original text positions from left to right. The previous LCP information helps us avoid comparing characters from the beginning again.

**Correctness:**

The inverse array identifies the previous suffix in sorted order. Kasai compares the characters and calculates their exact common prefix length. Reusing `h - 1` is valid because removing the first character from two matching prefixes leaves a common prefix one character shorter.

**Finding the repeated substring:**

The largest value in the LCP array gives the length of the longest repeated substring. We take that substring from the original text using its starting position.

**Complexity:**

- Time: O(n).
- Auxiliary memory: O(n).
- Output memory: O(n).

**Boundary test:**

Input: `text = "banana"`

Expected output:

```text
sa = [5, 3, 1, 0, 4, 2]
lcp = [0, 1, 3, 0, 0, 2]
repeated = "ana"
```

This test checks suffix ordering, LCP calculation and overlapping repeated substrings.

### 8. Aho–Corasick Algorithm

**Idea:**

Aho–Corasick searches for several patterns in one or more texts. It builds one Trie containing all patterns and uses failure links to continue searching after a mismatch.

**State:**

Each Trie node contains:

- `next` — transitions to other nodes for each character.
- `fail` — the failure link.
- `terminal` — the node where a pattern ends (stored separately for each dictionary position).
- `visits` — the number of times the search reaches this node.

**Base case:**

The Trie starts with one root node. Its failure link points to the root. If the dictionary is empty, the result contains an empty row for every input text.

**Transition:**

First, we insert all patterns into the Trie.

Then we build failure links using BFS:

- If a transition does not exist, we use the transition from the failure node.
- If a transition exists, we calculate the child's failure link.

During searching, we follow the transition for the current character and increase the visit count of the reached node.

**Processing order:**

We use BFS to build failure links. After searching a text, we process the nodes in reverse BFS order. This passes visit counts from each node to its failure node.

This allows us to count suffix-pattern matches without checking every pattern separately.

**Correctness:**

Failure links connect a node to the longest suffix that is also a Trie prefix. Therefore, patterns that end as suffixes of other matched patterns are also counted.

After reverse propagation, every terminal node contains the total number of matches for its pattern. Duplicate patterns use the same terminal node, so they receive the same count.

We reset the visit array for every new text, so counts from different texts do not affect each other.

**Complexity:**

Let:

- `L` be the total pattern length, including duplicates.
- `S` be the number of Trie nodes.
- `p` be the number of patterns.
- `n` be the length of the current text.

For the fixed 95-character ASCII alphabet:

- Construction: O(L + 95 × S + p).
- Searching and count propagation: O(n + S + p) per text.
- Auxiliary memory: O(95 × S + p).
- Output memory: O(q × (p + 1) + 1), where `q` is the number of texts and `p` is the number of patterns. This accounts for the result rows and their pattern counts, including fixed collection overhead.

**Boundary test:**

Input:

```json
{"patterns":["he","she","he"],"texts":["sheshe","sheshe",""]}
```

Expected output:

```json
{"counts":[[2,2,2],[2,2,2],[0,0,0]]}
```

This test checks duplicate patterns, suffix matches, overlapping matches and resetting counts for independent texts.

## Own tests

I created my own test file: `tests/student/own-tests.jsonl`.

I ran it using:

```bash
python3 tools/run.py --input tests/student/own-tests.jsonl
```

All 8 test cases produced the expected results.

| Algorithm | Input | Expected result | Actual result | What it checks |
|---|---|---|---|---|
| LCS | `abcde`, `ace` | Length = 3, sequence = `ace` | Length = 3, sequence = `ace` | Checks subsequence reconstruction. |
| Merge | `[10,20,30]` | Cost = 90 | Cost = 90 | Checks minimum merge cost and merge order. |
| Tree | Weights `[5,1,4]`, edges `[[0,1],[1,2]]` | Weight = 9, selected = `[0,2]` | Weight = 9, selected = `[0,2]` | Checks that adjacent vertices are not selected together. |
| Naive | `aaaaa`, pattern `aaa` | Positions = `[0,1,2]` | Positions = `[0,1,2]` | Checks overlapping matches. |
| KMP | `abababa`, pattern `aba` | Positions = `[0,2,4]` | Positions = `[0,2,4]` | Checks overlapping matches and prefix fallback. |
| RK | `bBaa`, pattern `aa` | Positions = `[2]` | Positions = `[2]` | Checks character verification after hash matching. |
| Suffix | `banana` | SA = `[5,3,1,0,4,2]`, LCP = `[0,1,3,0,0,2]`, repeated = `ana` | Same as expected | Checks suffix ordering and repeated substring detection. |
| Aho | Patterns `he`, `she`, `he`; text `sheshe` and empty text | `[[2,2,2],[0,0,0]]` | `[[2,2,2],[0,0,0]]` | Checks duplicate patterns, suffix matches and resetting counts between texts. |

These tests helped me check different situations, including overlapping matches, duplicate patterns, and the selection of non-adjacent tree vertices.

## Search comparison

I compared three string-search algorithms: Naive, KMP and Rabin-Karp (RK).

### Test environment and settings

I used the following input sizes:

- Text sizes: 500, 1500 and 3000 characters.
- Pattern sizes: 50, 150 and 300 characters.
- Two types of input: random and repetitive.

**Random test:**

- Random generator seed: `20261108 + n`.
- The text was generated using random lowercase English letters.
- The pattern was selected from a random position in the generated text. This guaranteed at least one match.

**Repetitive test:**

- The text contained only `a` characters.
- The pattern contained `a` characters and ended with `b`.
- No match was expected in this case.

**Measurement settings:**

- JVM warm-up: 1000 calls per algorithm for each test case.
- Each measured run: 5000 calls per algorithm.
- Number of measured runs: 3.
- Compilation was performed before measurement.
- The order of algorithms was shuffled between runs.
- Time was measured using `System.nanoTime()`.
- Measurements included algorithm execution, result creation, result access and checksum updates.
- Before measuring, I checked that all three algorithms returned the same positions for each input.

I used the following commands:

```bash
javac --release 17 -encoding UTF-8 -d Java/build Java/source/*.java tests/student/Benchmark.java
java -Xmx384m -cp Java/build Benchmark
```

### Results

All times are in milliseconds per algorithm call. The median is calculated from the three measured runs.

| Case | n | m | Algorithm | Run 1 | Run 2 | Run 3 | Median |
|---|---:|---:|---|---:|---:|---:|---:|
| Random | 500 | 50 | Naive | 0.000725 | 0.000664 | 0.000590 | 0.000664 |
| Random | 500 | 50 | KMP | 0.000751 | 0.000652 | 0.000798 | 0.000751 |
| Random | 500 | 50 | RK | 0.006002 | 0.003237 | 0.003338 | 0.003338 |
| Repetitive | 500 | 50 | Naive | 0.010896 | 0.010952 | 0.010906 | 0.010906 |
| Repetitive | 500 | 50 | KMP | 0.001001 | 0.000982 | 0.000995 | 0.000995 |
| Repetitive | 500 | 50 | RK | 0.003169 | 0.003172 | 0.003188 | 0.003172 |
| Random | 1500 | 150 | Naive | 0.001627 | 0.001714 | 0.001651 | 0.001651 |
| Random | 1500 | 150 | KMP | 0.002752 | 0.002773 | 0.002779 | 0.002773 |
| Random | 1500 | 150 | RK | 0.009558 | 0.009554 | 0.009607 | 0.009558 |
| Repetitive | 1500 | 150 | Naive | 0.109506 | 0.109583 | 0.109800 | 0.109583 |
| Repetitive | 1500 | 150 | KMP | 0.002986 | 0.003492 | 0.002924 | 0.002986 |
| Repetitive | 1500 | 150 | RK | 0.009459 | 0.010985 | 0.009560 | 0.009560 |
| Random | 3000 | 300 | Naive | 0.003393 | 0.003309 | 0.003303 | 0.003309 |
| Random | 3000 | 300 | KMP | 0.005591 | 0.007418 | 0.005515 | 0.005591 |
| Random | 3000 | 300 | RK | 0.019626 | 0.019298 | 0.019105 | 0.019298 |
| Repetitive | 3000 | 300 | Naive | 0.412274 | 0.415632 | 0.413105 | 0.413105 |
| Repetitive | 3000 | 300 | KMP | 0.005789 | 0.005743 | 0.005786 | 0.005786 |
| Repetitive | 3000 | 300 | RK | 0.018995 | 0.019004 | 0.019128 | 0.019004 |

## Sources and AI assistance

I used the assignment instructions, `docs/guide/CONTRACTS.md` and the examples provided by the teacher.

I also used ChatGPT to help me understand the algorithms, organize the explanations and prepare tests. I reviewed the code, ran the tests and checked the results myself.

The benchmark was created and run locally using Java. I used the actual results from my computer in the comparison table.

I am responsible for the final code, explanations and test results.