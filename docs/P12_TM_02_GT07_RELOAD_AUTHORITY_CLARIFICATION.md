# P12-TM-02 GT-07 Reload Authority Clarification

**Date:** 2026-07-30  
**Reviewer:** Sol  
**Decision:** the direct-publication instruction in
`P12_TM_02_GT07_FIXTURE_REVIEW.md` is superseded by the real public server
reload authority below.

## Finding

Terra Medium's objection is correct:

- `P11CapturedFlightGameTests` must live in `com.mathmod.program` to access the
  existing package-private flight fixture operations;
- `PhysicalProfiles.publishData` is package-private in
  `com.mathmod.physics`;
- direct invocation from the new holder would require reflection, widened
  visibility or a cross-package bridge.

None of those changes is authorized or architecturally necessary.

## Existing authority

The repository's Minecraft 1.21.1 server exposes:

```java
MinecraftServer.reloadResources(Collection<String> selectedPackIds)
```

The selected ids are available from the server's public pack repository. The
returned `CompletableFuture<Void>` completes only after reload application.
MathMod's registered `PhysicalProfileReloadListener` then validates the
candidate and calls the package-private `PhysicalProfiles.publishData` from its
own package. This is the production reload authority that GT-07 is intended to
exercise.

## Corrected GT-07 instruction

In the authorized
`src/main/java/com/mathmod/program/P11CapturedFlightGameTests.java`, the
captured-version test must:

1. capture snapshot version N;
2. launch a funded, non-creative owner-A flight and prove its captured version
   is N;
3. copy the selected pack ids to an immutable list, then call
   `server.reloadResources(selectedPackIds)`;
4. wait asynchronously for the returned future without blocking the server
   thread;
5. fail explicitly if the future completes exceptionally;
6. after completion, prove the active owner-A flight still reports N;
7. prove the published snapshot advanced exactly once to N+1;
8. launch a funded owner-B future flight and prove it captured N+1;
9. clear the static flight fixture in an exact `finally` path.

The test name is corrected to:

```text
capturedFlightRetainsProfileVersionAcrossRealReload
```

It must use a dedicated GameTest batch:

```text
p12_p11_reload
```

and an explicit bounded timeout sufficient for the repository reload. The
future must be polled through a GameTest sequence or equivalent non-blocking
mechanism. Calling `join`, `get` or otherwise waiting synchronously on the
server thread before `isDone()` is forbidden because it can deadlock the
reload executor/application boundary.

The collision and unloaded-chunk tests authorized by
`P12_TM_02_GT07_FIXTURE_REVIEW.md` remain unchanged and must not start another
reload.

## Ownership

The authorized files remain:

```text
src/main/java/com/mathmod/program/P11CapturedFlightGameTests.java
src/main/java/com/mathmod/acquisition/P10GameTests.java
docs/handoffs/P12_TM_02_HANDOFF.md
```

The previously frozen P9/P10/P11 GameTest files remain within their existing
ownership, but no additional change is implied.

No production file, visibility modifier, facade, reflection, networking,
schema, Data Component, client/UI, content or configuration change is
authorized.

## Remaining gate state

- the direct `PhysicalProfiles.publishData` requirement from the prior review
  must not be followed;
- GT-05 remains closed;
- the two GT-06 residual observations remain required;
- P12-TM-02 remains `NEEDS_FIX` until the corrected full handoff, focused
  suites, GameTest server and build are reviewed;
- P12-DS, P12-M and later expansion remain blocked.
