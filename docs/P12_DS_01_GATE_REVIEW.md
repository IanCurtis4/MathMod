# P12-DS-01 Gate Review

**Date:** 2026-08-02  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Correction task:** `P12-TM-04` is `DONE` with `ACCEPT` under
`docs/P12_TM_04_FINAL_GATE_ACCEPTANCE.md`  
**Downstream:** the remaining single-client P12-DS rows are blocked; DS-06
remains the separate `P12-DS-MP BACKLOG`

## 1. Evidence identity

The failure occurred in the immutable single-client batch recorded by
`docs/P12_DS_SINGLE_CLIENT_EXECUTION_RECORD.md`:

```text
commit: ce64b9bbc1d3ef48d3231be13ebad1203d9eb7e7
MathMod: 0.2.0-beta.1
MathMod JAR SHA-256: 9FF1CFE7D094BBB8E86E5739E9600C954A42ED9DC164EFD49EC6F6B74CFC725F
Minecraft: 1.21.1
NeoForge: 21.1.234
Java: Oracle 21.0.8+12-LTS-250
```

Two independent client crash reports contain the same failure:

```text
crash-2026-08-02_21.04.17-client.txt
SHA-256: EA1690321EB7D4C3B5C9E7ACC89B5E7DEB4EF2A4BA0A389A2281B08F66CBFC58

crash-2026-08-02_21.09.30-client.txt
SHA-256: 6F66643E28504B30C8DEF4105D1BE8B5523485139DC6EF66DCE25E5419716385
```

The raw reports remain outside Git because they include machine-local runtime
details. Their hashes and the sanitized stack below are sufficient to bind the
finding without committing private runtime data.

## 2. Reproduced failure

Both reports terminate the render thread while the Rune Programmer Laboratory
is open and `Self player` remains under the pointer after being selected:

```text
Description: Rendering screen
java.lang.IllegalArgumentException: A Laboratory form must add at least one rune
    at com.mathmod.program.CustomActionPreview.<init>(CustomActionPreview.java:16)
    at com.mathmod.program.CustomSpellWorkspace.preview(CustomSpellWorkspace.java:87)
    at com.mathmod.screen.RuneProgrammerScreen.customActionTooltip(RuneProgrammerScreen.java:2886)
    at com.mathmod.screen.RuneProgrammerScreen.render(RuneProgrammerScreen.java:1191)
```

The client mod inventory in both reports is the accepted three-artifact
MathMod/KubeJS/Rhino fixture. This is a MathMod product failure, not a launcher,
authentication, KubeJS or dedicated-server fixture failure.

The server remained authoritative and stopped cleanly through the control
bridge (`P12_DS01_STOPPED=true`, exit code 0). Its only error observations were
socket disconnects concurrent with the client crashes; no server-side MathMod
exception occurred.

## 3. Findings

### DS01-R1 — repeated explicit Self crashes the client

`CustomActionPreview` correctly enforces that a Laboratory form expansion adds
at least one rune. `CustomSpellWorkspace.preview` copies the current workspace,
applies the hovered action, and calculates the node delta. The explicit
`SELF` action calls `ensureSelf`; after one Self node exists, that helper reuses
it, produces a zero-node delta and violates the preview invariant. Rendering
the tooltip therefore closes the client.

The existing unit oracle covers only `blank.preview(SELF)`. It does not cover
`apply(SELF)` followed by `preview(SELF)`, the exact reachable state that
failed twice in the real client.

**Resolution:** preserve the `addedRunes >= 1` invariant. Every explicit
`SELF` invocation must append its own `mathmod:self_player` rune and become the
current output. Inferred premises may continue to reuse the cached Self input.
Allowing a zero-rune preview or catching the exception in rendering is not an
acceptable correction because either would hide the semantic mismatch.

### DS01-R2 — ordinary Laboratory mutation is not bound to the captured item

Repository control flow independently exposes a stale-target authority defect.
`RuneProgrammerMenu.canMutateWorkspace` checks only the active container and
`stillValid`; `stillValid` checks only that the currently held item has the
programmed-talisman item type. The component-exact comparison with
`capturedProjectionTarget` guards the read-only functional projection but not
ordinary Laboratory name, action, preset, clear or save mutations.

A component-distinct programmed talisman can therefore replace the captured
target while the menu stays open and still pass the ordinary mutation guard.
The client crash prevented the planned live stale-menu submission, but the
reachable production control flow is itself a concrete authority
counterexample and must be corrected before the row is rerun.

**Resolution:** every ordinary Laboratory mutation must remain inseparably
bound to the component-exact target captured when the menu opened. A
component-distinct replacement must reject without mutating either the
original or replacement item and without disconnecting the client. Exact
physical identity between component-for-component indistinguishable copies is
not introduced here: that would require a new identity/schema contract and is
outside P12-TM-04.

## 4. P12-TM-04 frozen ownership

**Owner:** Terra Medium  
**Status:** `DONE` with `ACCEPT` under
`docs/P12_TM_04_FINAL_GATE_ACCEPTANCE.md`

Production ownership:

```text
src/main/java/com/mathmod/program/CustomSpellWorkspace.java
src/main/java/com/mathmod/screen/RuneProgrammerMenu.java
```

Test and runtime-evidence ownership:

```text
src/test/java/com/mathmod/program/CustomSpellWorkspaceTest.java
src/main/java/com/mathmod/program/P12DsProgrammerGameTests.java
src/main/java/com/mathmod/client/UiPreviewHarness.java          (only if required)
src/main/java/com/mathmod/client/UiPreviewMatrix.java           (only if required)
src/test/java/com/mathmod/client/UiPreviewMatrixTest.java       (only if required)
docs/handoffs/P12_TM_04_HANDOFF.md
```

`CustomActionPreview.java` and `RuneProgrammerScreen.java` are read-only. If
the correction cannot be completed within the ownership above, Terra Medium
must stop and provide a repository-backed escalation before changing either
file.

**Harness clarification, 2026-08-02:** the escalation caused by the unrelated
Factored Leap 78-pixel catalog preflight is accepted and narrowly resolved in
`docs/P12_TM_04_HARNESS_PREFLIGHT_CLARIFICATION.md`. It authorizes only the
`laboratory-self-repeat` exclusion in the already conditionally owned
`UiPreviewHarness`; formulas, localization and production UI remain read-only.

Forbidden throughout:

- Data Component identities or schemas;
- public APIs or visibility expansion;
- networking or payloads;
- unrelated client/UI behavior or layout;
- content, progression or KubeJS contracts.

## 5. Mandatory correction evidence

The handoff and real delta must prove all of the following:

1. blank `SELF` preview still adds exactly one rune;
2. `apply(SELF)` followed by `preview(SELF)` does not throw and reports exactly
   one added rune;
3. two explicit `SELF` applications append two deterministic steps/nodes, with
   the newest node as output, and preserve replay/undo behavior;
4. every Laboratory action can be previewed without exception from reachable
   post-action states, including same-action repetition and existing bounds;
5. a GameTest opens the real menu on an original programmed talisman, replaces
   it with a component-distinct programmed talisman, and proves every relevant
   ordinary mutation route rejects while both exact stacks/resources remain
   unchanged and the player remains connected;
6. runtime client evidence exercises click-and-hover of repeated Self without
   a render-thread crash, at minimum at 1024x800 in EN-US and PT-BR; an
   equivalent deterministic client harness is acceptable if it reaches the
   real preview/render boundary;
7. focused unit tests, the named P12 GameTests, the global GameTest suite and
   the standard build all pass, with focused and global counts reported
   separately.

The handoff must inventory every changed file, commands, method names, counts,
limitations and escalations. A green build alone is insufficient.

## 6. Gate disposition

`P12-DS` is `NEEDS_FIX`. No later single-client DS row may continue on this
artifact. After an accepted `P12-TM-04` handoff, Sol must generate a new clean
JAR, record its commit and SHA-256, refresh the immutable fixture batch and
rerun DS-01 from a clean checkpoint. DS-02 through DS-05 and DS-07 through
DS-09 remain blocked until DS-01 passes. DS-06 remains unpassed and deferred,
not waived.
