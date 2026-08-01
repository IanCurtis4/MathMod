# L0-TM-05F Graph Oracle Clarification

**Owner:** Sol  
**Date:** 2026-07-29  
**Decision:** no production ownership expansion

## 1. Finding

Terra Medium correctly identified that:

```text
compiledGraph.equals(FactoredLeapTheorem.presentationGraph()) == false
```

This is expected under the frozen theorem specification and is not evidence
that `FactoredLeapTheorem.java` is wrong.

`ScopedProgramLowerer` assigns generated node ids in lowering order:

```text
f0  self_player
f1  look_vector
f2  constant_number 0.5
f3  scale_vector
f4  constant_number 0
f5  constant_number 1
f6  constant_number 0
f7  vector_from_numbers
f8  constant_number 0.5
f9  scale_vector
f10 vector_add
f11 push_self
```

The presentation oracle uses descriptive ids:

```text
self, look, halfLook, scaledLook, x, y, z, up,
halfUp, scaledUp, sum, push
```

The one-to-one mapping is:

```text
f0=self
f1=look
f2=halfLook
f3=scaledLook
f4=x
f5=y
f6=z
f7=up
f8=halfUp
f9=scaledUp
f10=sum
f11=push
```

Under that mapping, rune identities, constants, all named input sockets,
output, and the explicitly shared `self` node match. The graphs are
semantically isomorphic even though Java record equality rejects their
different node ids.

The focused reproduction confirms two independent non-semantic differences:

```text
FactoredLeapTheoremTest: 4 tests, 2 failures
line 22: descriptive node ids versus generated f0...f11 ids
line 58: identical edge set asserted in a different list order
```

NUMBER literals also cross the accepted lowering canonicalization boundary:
the authored/oracle values `0` and `1` lower to the deterministic graph
strings `0.0` and `1.0`. For `constant_number`, semantic comparison therefore
uses the finite numeric value, while still requiring the exact `value` key and
rejecting missing, additional, non-finite or numerically different constants.
This does not permit arbitrary string normalization for other rune constants.

## 2. Contract precedence

Section 4.1 of
`docs/L0_FIRST_GAMEPLAY_THEOREM_SPECIFICATION.md` is explicit:

> Node ids and list order are not semantic oracles.

`docs/L0_INTERNAL_GAMEPLAY_INTEGRATION_READINESS.md` likewise requires
semantic identity between the compiled graph and the presentation oracle, not
`ProgramGraph.equals`.

The wording “exact compiled-graph equality with the presentation oracle” in
L0-TM-05-R5 was an overconstraint introduced by Sol's gate review. It is
corrected by this document.

## 3. Required test behavior

`FactoredLeapTheoremTest` must not use:

```java
assertEquals(FactoredLeapTheorem.presentationGraph(), compiledGraph);
```

It must instead prove semantic graph isomorphism using only:

- rune identity;
- canonical constant meaning, including the accepted NUMBER lowering
  representation;
- named socket connectivity;
- output rune;
- explicit sharing of the single `self_player` node;
- the frozen per-rune, node, edge and budget counts.

A test-local matcher or explicit assertions are permitted in
`FactoredLeapTheoremTest.java`. The comparison must be bijective and must fail
for a missing/extra node or edge, changed constant, changed socket, changed
output, or duplicated `self`. Node and edge collection order must not be
asserted as semantic.

This clarification does not weaken the separate persistence assertion:
the graph stored by the successful menu route must remain exactly equal to the
graph returned by the authoritative compile attempt from the same source.

## 4. Ownership decision

`FactoredLeapTheorem.java` remains read-only. No production file is added to
L0-TM-05F ownership.

Terra Medium may complete this correction inside the already authorized:

```text
src/test/java/com/mathmod/program/FactoredLeapTheoremTest.java
```

All other R1-R5 requirements and stop conditions in
`docs/L0_TM_05_GATE_REVIEW.md` remain unchanged.
