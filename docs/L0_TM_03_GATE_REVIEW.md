# L0-TM-03 Gate Review

**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Reviewed handoff:** `docs/handoffs/L0_TM_03_HANDOFF.md`  
**Contract:** `docs/L0_ATOMIC_PERSISTENCE_READINESS.md`

## Decision

The handoff is not accepted. The implementation compiles and the evidence it
actually runs is green, but that evidence is materially narrower than the
frozen gate. Authority rechecks, rollback proof and required focused vectors
remain incomplete. `L0-TM-04` and every later L0 slice remain blocked.

The bounded correction is `L0-TM-03F`. It is suitable for a fresh Terra Medium
task because it has a closed defect list and must not reopen the accepted
architecture.

## Blocking findings

### L0-TM-03-R1 — Player knowledge is not a live commit authority

`ScopedCommitAuthority` stores one fixed `PlayerKnowledge`, not a supplier for
the current server-owned value. `ScopedFunctionalInscriptionService` therefore
compares the compile result with the same value supplied to compilation. It
cannot observe knowledge changing during the attempt.

The correction must capture current server knowledge for the request and read
the current value again at the final precommit boundary. The test seam must
prove a change after compilation yields `KNOWLEDGE_STALE` and mutates nothing.

### L0-TM-03-R2 — Rechecks do not occur immediately before mutation

The coordinator performs target, cancellation, generation, knowledge and
material checks before canonical source encoding, name normalization, resource
selection and candidate construction. It then commits without repeating them.
This leaves an unguarded interval between the required authority checks and the
first target mutation.

Build the full candidate off-item first, then perform every frozen recheck
immediately before the transaction. Source/result binding must remain internal.

### L0-TM-03-R3 — Rollback does not meet the frozen state-machine proof

The production transaction catches rollback failure and discards it without
logging a severe invariant breach or verifying exact restored six-component
equality. The test seam injects only before each component and skips the
production off-item preflight path. It cannot inject after each logical
application and therefore does not exercise the complete production state
machine required by section 6.5.

The correction must:

- prebuild forward and rollback patches off-item;
- use one state machine for production and tests;
- inject before and after each of all six logical applications;
- verify exact rollback equality, including byte-exact source;
- log rollback failure as a severe invariant breach while returning
  `COMMIT_FAILED`.

### L0-TM-03-R4 — Existing save routes can report success after commit failure

Both validated `ProgramStorage` save routes ignore the transaction boolean and
return their already-valid `ValidationResult`. A failed component transaction
can consequently be exposed as a valid save even though no candidate became
active. The public signatures must remain unchanged, but commit failure must not
be represented as successful inscription.

### L0-TM-03-R5 — The outer payload bound is enforced after list decoding

`ScopedSourceEnvelope.CODEC` uses `Codec.BYTE.listOf()` and only checks the size
when converting the already-decoded boxed list to bytes. That is not proof that
the 262,145th byte fails before the forbidden allocation. Constructor-only
tests do not cover outer codec decoding.

Use a bounded persistent codec that rejects an oversized outer payload before
building the complete list/value. Prove 262,144 accepted and 262,145 rejected
through the persistent codec, not only the Java constructor.

### L0-TM-03-R6 — Required focused and dedicated-server evidence is incomplete

The readiness authorizes and requires these focused files, but neither exists:

```text
src/test/java/com/mathmod/program/ScopedProgramComponentTransactionTest.java
src/test/java/com/mathmod/program/ScopedProgramPersistenceTest.java
```

Only 11 focused methods were delivered. Missing mandatory evidence includes the
4,096/4,097 token boundary, 1,024/1,025 type count, complete multibyte/Java
string boundaries, no-trim/default behavior, live authority changes,
source/result inseparability, exact resource preservation and before/after
rollback injection.

`L0ScopedSourcePersistenceGameTests` contains eight L0 methods. The reported
22/22 is the global server discovery count, not 22 L0 cases. Its L0 cases do
not prove stale generation, knowledge and materials, cancellation after
compilation, or before/after failure at every component position.

## Reproduced evidence

Focused subset:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --tests com.mathmod.program.ScopedSourceEnvelopeTest --tests com.mathmod.program.ScopedSourceWireCodecTest --no-build-cache
```

Result: `BUILD SUCCESSFUL`; 11 tests, 0 failures.

Dedicated server:

```powershell
.\gradlew.bat runGameTestServer --no-daemon
```

Result: `BUILD SUCCESSFUL`; 22/22 globally discovered GameTests passed. Eight
methods are owned by `L0ScopedSourcePersistenceGameTests`.

Standard build:

```powershell
.\gradlew.bat build
```

Result: `BUILD SUCCESSFUL`; 31 actionable tasks, 30 up-to-date and the test task
restored from cache.

These successes establish compilation and the delivered subset only. They do
not close R1–R6.

## L0-TM-03F boundary

Read, in order:

1. `docs/L0_ATOMIC_PERSISTENCE_READINESS.md`
2. `docs/L0_TM_03_GATE_REVIEW.md`
3. `docs/handoffs/L0_TM_03_HANDOFF.md`
4. `docs/DELIVERY_BOARD.md`

May edit only the existing L0-TM-03 ownership set and the two missing,
already-authorized focused test files. No public API, networking, client,
`ProgramGraph`, Guided schema or Data Component identity change is authorized.

Required output:

- close R1–R6 individually with file/test evidence;
- include the exact production, test and documentation file list;
- report every mandatory focused vector and total, using `--no-build-cache`;
- name the L0 GameTests separately from the global discovered total;
- reproduce `runGameTestServer --no-daemon` and `build`;
- disclose limitations and escalations without claiming client/reconnect
  coverage.

Handoff path:

```text
docs/handoffs/L0_TM_03F_HANDOFF.md
```

