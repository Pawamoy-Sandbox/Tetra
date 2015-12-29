# Tetranucleo

Growth function for autocomplementary circular tetranucleotide codes.

## Theorem

Let c be a tetranucleotide code, c = {AACG, GTAC}.
When building the corresponding graph like:

(A)-(ACG)
(AA)-(CG)
(AAC)-(G)
(G)-(TAC)
(GT)-(AC)
(GTA)-(C)

the absence of cycle in the graph tells us that the code is circular.

[See the 2015 paper](https://dl.acm.org/citation.cfm?id=2148596) by Christian J. Michel, Giuseppe Pirillo, Mario A. Pirillo.

## Problem

To analyse the growth function for codes of length 1 to length 60, we need to find the exact number of valid (circular) codes. We then have to test all possible code combinations. For length 60: `(256 choose 60) = 2.029299e+59` combinations!

Thankfully, some tetranucleotides are eliminated, and we can build our combination from S12 and S108:

- S12: the twelve autocomplementary tetranuclotides
- S16: the sixteen tetranucleotides that create cycles
- S6: the six tetranucleotides that, combined with their complementary, create cycles
- S216: the rest of tetranucleotides (256 - 16 - 12 - 6*2)
- S108: one half of S216 (tetranucleotides on one side, their complementary on the other, C(S108))

This reduces the number of combinations for length 60 to:
`(12 choose 6) * (108 choose 27) + (12 choose 4) * (108 choose 28) + (12 choose 2) * (108 choose 29) + (108 choose 30) = 6.0932847e+28`

## Solution

We use a Breadth-first search to iterate on lengths, from L1 to L60. Valid codes are written on disk, and read again for next lengths. Each length computed re-uses the three previous even lengths.

```
valid_codes(L) :

  // Initialization
  if L ≤ 6:
    maxS12 ←  L
  else if L is even:
    maxS12 ← 6
  else:
    maxS12 ← 5

  // 1st step: building codes with S12, S108 and previous results
  for nbS12 from maxS12 to 0 (excluded), 2 by 2:
    
    // A tetra from S108, its complementary and the rest from S12
    if L - nbS12 == 2:
      for each tetra T of S108:
        for each code C of S12nbS12:
          code ← C ⋃ T ⋃ compl(T)
          verify(code)

    // Only S12. Redundancy but negligible execution time
    // Executed only from L1 to L6.
    else if L == nbS12:
      for each code C of S12nbS12:
        code ← C
        verify(code)

    // A valid S108 code combined to a valid S12 code 
    else:
      for each code C1 of S108L-nbS12:
        for each code C2 of S12nbS12:
          code ← C1 ⋃ C2
          verify(code)

  // 2nd step: building codes with S108 and previous results
  // Only when L is even (an odd code has always at least one tetra from S12)
  if L is even:

    // Executed only once, when L=2
    if L == 2:
      for each tetra T of S108:
        code ← T ⋃ compl(T)
        verify(code)

    else:
      for each code C of S108L-2:
        for each tetra T of absent(C):
          code ← C ⋃ T ⋃ compl(T)
            verify(code)

  total ← getResult()
  return total
```
