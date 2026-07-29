# A0-TM-03 Dispatch Readiness Acceptance

**Task:** task 7 / `A0-TM-03`  
**Date:** 2026-07-26  
**Owner:** Sol  
**Decision:** `READY`

This document supersedes the current-state decisions in
`docs/A0_TM_03_READINESS_REVIEW.md` and
`docs/A0_TM_03_READINESS_REREVIEW.md`, which remain historical gate records.

## Dependencies satisfied

- A0-3 adapter gate: accepted;
- A1 read-only canvas and both bounded corrections: accepted;
- A1 screen/test/locale ownership: released;
- terminology/content decision: accepted;
- A0-LU-01 content and evidence correction: accepted;
- Luna content ownership: released;
- focused screen suite and standard build: passing;
- no persistent, graph, mode, networking, Data Component, or public API change.

## Exact A0-TM-03 write ownership

Existing production files:

```text
src/main/java/com/mathmod/client/screen/RuneProgrammerScreen.java
src/main/java/com/mathmod/client/UiPreviewMatrix.java
```

One authorized new package-private presentation helper:

```text
src/main/java/com/mathmod/client/screen/AuthoringPalettePresentation.java
```

Existing tests:

```text
src/test/java/com/mathmod/client/UiPreviewMatrixTest.java
src/test/java/com/mathmod/client/screen/PaletteSearchTest.java
src/test/java/com/mathmod/authoring/TrustedLegacyExpansionAdapterTest.java
```

Authorized new focused tests:

```text
src/test/java/com/mathmod/client/screen/AuthoringPalettePresentationTest.java
src/test/java/com/mathmod/client/screen/RuneProgrammerRegistrySourceTest.java
```

Documentation and handoff:

```text
docs/UI_PREVIEWS.md
docs/handoffs/A0_TM_03_HANDOFF.md
```

No other file may be created or changed without a prior board ownership update.

## Read-only authority inputs

```text
src/main/java/com/mathmod/authoring/AuthoringMetadata.java
src/main/java/com/mathmod/authoring/BuiltInAuthoringMetadata.java
src/main/java/com/mathmod/authoring/TrustedLegacyExpansionAdapter.java
src/main/java/com/mathmod/program/CustomSpellAction.java
src/main/java/com/mathmod/program/CustomSpellInvocation.java
src/main/java/com/mathmod/program/CustomSpellWorkspace.java
src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java
src/main/java/com/mathmod/client/screen/ProgramGraphPresentation.java
src/main/java/com/mathmod/client/screen/ProgramInspectorPresentation.java
src/main/resources/assets/mathmod/lang/en_us.json
src/main/resources/assets/mathmod/lang/pt_br.json
```

## Required integration behavior

- `BuiltInAuthoringMetadata.snapshot()` is the source of category and form
  enumeration, ordering, presentation, and numeric parameter descriptors.
- Existing form ids remain the invocation identity.
- The package-private presentation helper may adapt immutable metadata to
  screen rows but may not create semantic identity or execution authority.
- Existing pointer, keyboard, search, narration, appearance, and ordering must
  remain compatible.
- Missing presentation uses bounded technical fallback without substituting
  another form.
- Numeric dialogs consume registry descriptors and descriptor
  canonicalization.
- Existing Guided mutation and packet behavior remain unchanged; networking is
  read-only.
- Focused adapter tests must prove that emitted built-in form ids and
  canonicalized numeric arguments retain exact legacy graph replay.
- Preview cases must cover EN/PT-BR, minimum/ATM10 viewport, keyboard/pointer
  equivalence, search, narrator requirements, and technical fallback.

## Forbidden boundaries

```text
src/main/java/com/mathmod/runes/ProgramGraph.java
src/main/java/com/mathmod/program/GuidedWorkspaceState.java
src/main/java/com/mathmod/program/ProgramSurfaceMode.java
src/main/java/com/mathmod/registry/ModDataComponents.java
src/main/java/com/mathmod/network/**
src/main/java/com/mathmod/client/screen/RuneInspectorScreen.java
src/main/java/com/mathmod/client/screen/ProgramGraphPresentation.java
src/main/java/com/mathmod/client/screen/ProgramInspectorPresentation.java
all persistence codecs
all execution/inscription files
all public KubeJS/datapack APIs
all stable ids
```

The two locale files and Patchouli entries delivered by Luna are read-only for
A0-TM-03. New keys or content changes require a separate Sol ownership update.

## Required handoff

Create:

```text
docs/handoffs/A0_TM_03_HANDOFF.md
```

It must include:

- exact changed-file inventory;
- registry-versus-legacy enumeration/order comparison;
- fallback matrix;
- numeric parameter matrix;
- pointer/keyboard/search/narrator/viewport evidence;
- exact replay vectors through the accepted adapter test boundary;
- locale and preview evidence;
- focused test commands/results;
- standard build;
- proof of no persistence/network/public API change;
- limitations and escalations;
- release of all granted files.

## Operational result

`A0-TM-03` is `READY`. `A0-TM-04` and `A0-W4-GATE` remain `BLOCKED`.
