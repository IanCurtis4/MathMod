# L0-TM-02 Handoff — Server Compile Service and Registry Generation

## Result

L0-TM-02F closes R1–R4 without extending the authority boundary. Successful
results now require all captured evidence and candidate-rune proof; service
diagnostics carry a stable pipeline phase and normalize by phase/path/code.

## Changed files

Production:

- `src/main/java/com/mathmod/runes/RuneRegistry.java`
- `src/main/java/com/mathmod/program/ScopedServerCompileIssue.java`
- `src/main/java/com/mathmod/program/ScopedServerCompileResult.java`
- `src/main/java/com/mathmod/program/ScopedServerCompileService.java`

Tests:

- `src/test/java/com/mathmod/runes/RuneRegistrySnapshotTest.java`
- `src/test/java/com/mathmod/program/ScopedServerCompileServiceTest.java`

Documentation:

- `docs/handoffs/L0_TM_02_HANDOFF.md`

This is the exact L0-TM-02F delta; the other original-handoff files are not
changed by this correction.

## Contract evidence

- `RuneRegistry` owns generation (zero at construction); real register,
  replacement, enable/disable and update changes advance it once. Equal
  replacement, same enabled state and equal updater results are no-ops.
- Capture is synchronized and returns an immutable ordered `RuneRegistrySnapshot`.
  Complete publication validates a detached copy before replacement; invalid
  publication leaves definitions and generation unchanged, equal publication is
  a no-op, and generation overflow fails before a mutation. A-to-B-to-A retains
  distinct monotonically increasing generations.
- The service never reads a graph, generation, resource list or knowledge
  definition from the request. It uses a captured rune map for pure compilation,
  resource calculation and execution validation, a captured material list for
  recommendation/admission, and a captured knowledge snapshot for canonical
  distinct-rune checks.
- Rejections remove both candidate and recommendations. Service codes are
  `CANCELLED`, `EXECUTABLE_REJECTED`, `RESOURCE_REJECTED`,
  `KNOWLEDGE_REJECTED`, and `REGISTRY_GENERATION_STALE`; pure issues remain
  `ScopedLanguageIssue`s. Cancellation is checked at entry, after pure compile,
  between admissions and before return; there is no retry.
- Fabricated candidate success rejects null rune/material/knowledge/player
  evidence, negative generation, and candidate ids absent from the captured
  definitions. Any issue exposes neither candidate nor recommendation.
- Service issues carry explicit cancellation/admission/recheck phases and
  normalize by phase, canonical path, then code; duplicate triples collapse.
- Focused vectors now cover executable/resource/knowledge rejection, absent
  knowledge requirement admission, fabricated-result invariants and diagnostic
  ordering/deduplication.

## Verification

Focused command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat cleanTest test --no-build-cache --tests com.mathmod.runes.RuneRegistrySnapshotTest --tests com.mathmod.program.ScopedServerCompileServiceTest --tests com.mathmod.program.ProgramCostsTest --tests com.mathmod.program.ProgramExecutionPolicyTest
```

Result: `BUILD SUCCESSFUL` — 29 JUnit vectors (7 new service/registry vectors,
17 resource vectors and 5 executable-policy vectors). The focused suite covers immutable snapshot,
mutation/no-op generation behavior, failed publication, complete publication,
overflow, successful captured evidence, pure rejection, entry cancellation and
stale generation. Existing resource and executable policy suites also passed.

L0-TM-02F correction result: `BUILD SUCCESSFUL` — 30 JUnit vectors, 0 failures,
0 errors, 0 skipped: 3 registry snapshot vectors, 5 server compile service
vectors, 17 resource vectors and 5 executable-policy vectors. This supersedes
the previous 29-vector count above.

Standard build command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'; .\gradlew.bat build
```

Result: `BUILD SUCCESSFUL` (31 actionable tasks; 2 executed, 29 up-to-date).

L0-TM-02F standard build result: `BUILD SUCCESSFUL` (31 actionable tasks; 1
executed, 30 up-to-date).

## Scope and limitations

No item, player, world or persistent state is mutated. No Data Component,
source codec, networking, menu/client/UI, `ProgramGraph`, `GuidedWorkspaceState`
or `ProgramStorage` change was made. No loader, active last-known-good cache,
background execution, retry mechanism or public KubeJS/datapack/network API was
added or claimed.

L0-TM-03 remains responsible for atomic item commit and its immediate rechecks
of current knowledge/material identity, request validity and generation.
