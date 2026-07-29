# Handoff: A0-TM-01F

## Completed
- Replaced the delimiter-concatenated semantic fingerprint with immutable structured `SemanticFingerprint` and `ParameterSemantics` values.
- Split expansion-consumed input identities (`consumedInputIds`) from descriptive `inputHints`; only consumed identities enter semantic compatibility.
- Enforced the accepted A0 bounds before snapshot publication: 1,024 forms, 128 categories, 16 parameters, 16 consumed inputs, 16 descriptive hints, and 160-character textual keys.
- Added structured stable candidate diagnostics and deterministic category ordering.
- Reworked built-in identity characterization to compare `enumName -> canonical form id` without asserting `CustomSpellAction.values()` order.

## Decisions implemented
- Semantic fingerprints are internal comparison records only; they are not persisted, networked, or serialized as a public format.
- Candidate failures expose immutable `Diagnostic` values with severity, stable code, record kind, optional id, source kind/name, and technical message. Exception text is not the protocol.
- Built-in forms map legacy input slot names to `consumedInputIds`; built-ins have no descriptive input hints in this slice.
- Category and form ordering are explicit queries using sort order followed by stable id.

## Files changed
- `src/main/java/com/mathmod/authoring/AuthoringMetadata.java`
- `src/main/java/com/mathmod/authoring/BuiltInAuthoringMetadata.java`
- `src/test/java/com/mathmod/authoring/BuiltInAuthoringMetadataTest.java`
- `docs/handoffs/A0_TM_01F_HANDOFF.md`

## Contracts referenced
- `docs/A0_AUTHORING_METADATA_BOUNDARY_CONTRACT.md` sections 6.3, 6.8, 7, 9.5, and 10.
- `docs/A0_METADATA_SEMANTIC_REVIEW.md`
- `docs/A0_METADATA_REVIEW_SOL_RESOLUTION.md`
- `docs/DELIVERY_BOARD.md`, task `A0-TM-01F`.

## Tests and evidence
- command: `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat test --tests com.mathmod.authoring.BuiltInAuthoringMetadataTest --no-daemon`
  result: `BUILD SUCCESSFUL`.
- command: `$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build --no-daemon`
  result: `BUILD SUCCESSFUL`.
- Focused vectors cover FP-1 delimiter collision resistance, FP-2 descriptive-hint exclusion, parameter type/default/bound/order and adapter sensitivity, 17 parameters, 17 consumed inputs, 1,025 forms, 129 categories, 161-character key rejection, structured `DUPLICATE_ID`/`UNKNOWN_CATEGORY`/`LIMIT_EXCEEDED`, category tie ordering, and enum-order-independent identity characterization.

## Migration impact
- None. No persistence, Data Component, network payload, public codec, `ProgramGraph`, or `GuidedWorkspaceState` change was made.

## Known limitations
- The default `%USERPROFILE%\.gradle` cache may fail to bootstrap test workers on this Windows profile. Verification used the accepted ASCII cache `C:\codex-gradle-a0`.
- A0-3 legacy expansion/replay remains unimplemented and blocked pending the delta semantic re-review.

## Unresolved questions
- None for this bounded correction.

## Next owner
- Terra High

## Exact next task
- Execute `A0-TH-01R`: review only this delta and issue `APPROVE` or `REJECT` for A0-3.

## Files the next owner may edit
- `docs/A0_METADATA_SEMANTIC_REREVIEW.md`

## Files the next owner must not edit
- `src/main/java/com/mathmod/authoring/**`
- `src/test/java/com/mathmod/authoring/**`
- `src/main/java/com/mathmod/runes/ProgramGraph.java`
- `src/main/java/com/mathmod/program/GuidedWorkspaceState.java`
- `src/main/java/com/mathmod/program/ProgramSurfaceMode.java`
- `src/main/java/com/mathmod/registry/ModDataComponents.java`
- `src/main/java/com/mathmod/network/**`
- `src/main/java/com/mathmod/client/screen/**`
