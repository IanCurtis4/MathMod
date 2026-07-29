# L0-TM-01F3 Gate Review

**Task:** `L0-TM-01F3` — Pure Compile Evidence/Test Closure  
**Date:** 2026-07-27  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Downstream:** `L0-TH-01` remains `BLOCKED`

## 1. Decision

The updated handoff is materially closer, but it is not accepted yet.

The implementation correctly closes the package-private complete-pipeline
seam, exact 4,096/4,097 meter behavior, beta/binding accounting, meter
freshness, whitespace, authored beta path, and most vector coverage. Ownership
is conforming.

Four semantic evidence gaps remain. They require only a short continuation of
the same `L0-TM-01F3`; no new task or ownership escalation is justified.

## 2. Reproduced commands

Focused suite:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat test --tests 'com.mathmod.language.*' --rerun-tasks --no-daemon --console=plain
```

Result:

```text
BUILD SUCCESSFUL
7 suites
34 tests
0 failures
0 errors
0 skipped
```

Standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build --no-daemon --console=plain
```

Result:

```text
BUILD SUCCESSFUL
```

Sol's independent build closes the handoff's `pending rerun` execution
uncertainty. The remaining blockers are test-oracle defects, not build
integrity.

## 3. Accepted portions

- Public `compile(source)` still creates a fresh default meter.
- The new overload is package-private and executes the same compiler body.
- BOUND-5 succeeds at exactly 4,096.
- BOUND-6 rejects the attempted 4,097th charge with only
  `COMPILE_STEP_LIMIT`, no graph, and a final charged count of 4,096.
- The measured 17-step identity application makes the beta administrative
  binding charge regression-sensitive.
- Repeated public checker/lowerer attempts are now sufficiently expensive to
  detect meter reuse.
- Raw whitespace and authored lambda-body paths remain correctly handled.
- The F3 delta is limited to the four files declared in the handoff and stays
  inside exact ownership.
- No persistence, codec, component, registry mutation, networking, item,
  `ProgramGraph`, client, executable-policy, or public API boundary changed.

## 4. Remaining corrections

### L0-1F3-R1 — BOUND-1 skips the first failing boundary

`addTree(n)` contains `2n - 1` AST nodes. Wrapping it in one lambda produces:

```text
lambda(addTree(128)) = 256 nodes
lambda(addTree(129)) = 258 nodes
```

The assertion labeled “257 AST nodes” therefore tests 258. Preserve the valid
256 case and construct exactly 257 nodes for the rejected case. One valid
option is a `let` whose value is one literal and whose body is `addTree(128)`:

```text
1 let + 1 value literal + 255 body nodes = 257
```

Assert `AST_LIMIT` on that exact source.

### L0-1F3-R2 — TAIL-2 proves validity but not observation sharing

The current assertion only checks that the candidate compiles.

For the graph produced by:

```text
let x = observe_number() in effect(value = x)
```

assert:

- exactly one observation node;
- exactly one terminal effect node;
- exactly one edge from that observation to the effect's `value` socket;
- the effect is the graph output.

This prevents a duplicated or disconnected observation from satisfying
TAIL-2.

### L0-1F3-R3 — OBS-SHARE-6 does not exercise application

The audit vector requires applying a function whose body observes. The current
test compiles a bare lambda.

Wrap the impure lambda in an application with a valid NUMBER argument. Assert:

- `IMPURE_LAMBDA_BODY`;
- no graph;
- no lowered observation can escape.

The rejection should occur before beta/lowering, but the source must include
the application route required by the vector.

### L0-1F3-R4 — Phase ordering assertion is incomplete

The normalized list includes phases 2, 3, and 4, but the test asserts only the
first two entries, both from phase 2. Phase 3 and phase 4 could be reversed and
the test would still pass.

Add explicit assertions for the complete expected sequence, including one
phase-1 issue:

```text
phase 1 structural
phase 2 checker
phase 3 lowering
phase 4 graph validation
```

Keep the existing independent assertions for numeric path order, code order,
and first-duplicate retention.

## 5. Handoff correction

Update `docs/handoffs/L0_TM_01F3_HANDOFF.md` in place:

1. replace grouped matrix rows with one explicit row for each of the 22
   vectors;
2. map the four corrected oracles above to exact methods/assertions;
3. record the final focused count;
4. replace the pending build line with the successful final build result;
5. leave TAIL-8 admission explicitly deferred to `L0-TM-02`.

Only these files remain writable:

```text
src/test/java/com/mathmod/language/ScopedStructureValidatorTest.java
src/test/java/com/mathmod/language/ScopedProgramCompilerTest.java
docs/handoffs/L0_TM_01F3_HANDOFF.md
```

`ScopedProgramCompiler.java` is now read-only; its authorized seam is accepted.
All other files and forbidden boundaries remain read-only.

## 6. Gate state

```text
L0-TM-01F3
    -> remains NEEDS_FIX
    -> same-task short correction

L0-TH-01
    -> remains BLOCKED
    -> requires Sol ACCEPT of the corrected F3 handoff
```
