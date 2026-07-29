# L0-TM-01F3 Correction Guidance

**Task:** `L0-TM-01F3` — Pure Compile Evidence/Test Closure  
**Date:** 2026-07-26  
**Owner of decision:** Sol  
**State:** `NEEDS_FIX`; continue the same task  
**Downstream:** `L0-TH-01` remains `BLOCKED`

## 1. Sol decision

The F3 handoff describes an incomplete implementation accurately. It does not
identify a new architecture or ownership blocker.

Only five of the 22 mandatory vectors are currently marked fully proven:

```text
OBS-SHARE-4
TAIL-3
TAIL-4
TAIL-6
BOUND-7
```

The remaining work fits the exact ownership already granted in
`docs/L0_TM_01F2_GATE_REVIEW.md`. Do not create F4. Terra Medium must continue
`L0-TM-01F3` and update the same handoff after implementing the closure.

## 2. Minimal compiler seam

The only authorized production adjustment remains
`ScopedProgramCompiler.java`.

Recommended shape:

1. keep `public compile(source)` behavior unchanged;
2. make it create one fresh zero-charged `ScopedCompileBudget`;
3. delegate to one package-private overload that receives the budget and runs
   the exact same complete pipeline;
4. let tests precharge the supplied budget before calling that overload.

Do not duplicate the pipeline and do not add a public overload.

For a valid source whose measured pipeline cost is `k`:

```text
precharge = 4096 - k
    -> successful result
    -> chargedSteps == 4096

precharge = 4097 - k
    -> graph is empty
    -> exactly COMPILE_STEP_LIMIT
    -> chargedSteps remains 4096
```

This proves the real shared pipeline boundary rather than the meter object in
isolation.

The identity application:

```text
apply(lambda x:Number. x, 1)
```

currently has a 17-step complete-pipeline cost. Use the package-private seam to
prove that the application and its administrative binding are separate charged
events: limit/precharge equivalent to 16 must fail and 17 must succeed. The
test must fail if the binding charge is removed.

## 3. Regression-sensitive meter freshness

The current two trivial literal attempts would also pass with the former reused
meter.

Use a valid near-limit source and repeat it on the same public instance:

- checker: a valid 255-node typed expression repeated enough times that a
  reused meter would exceed 4,096;
- lowerer: a valid high-charge expression that lowers to bounded shared output,
  repeated enough times that cumulative reuse would exceed 4,096.

Every individual call must succeed. The test must document the per-attempt
charge and the repetition count that makes the former implementation fail.

## 4. Required vector closure

Each vector must have its own labeled assertion even when several share one
test method.

### OBS-SHARE

- OBS-1: application of a lambda using `x` in two add sockets; one observation,
  one add, same source node connected to `left` and `right`.
- OBS-2: explicit `let`; same oracle as OBS-1.
- OBS-3: application followed by nested `let`; one observation and both binder
  references reuse it.
- OBS-4: repeated unbound observation syntax; two observation nodes.
- OBS-5: let-bound NUMBER literal; one constant node reused by two sockets.
- OBS-6: observation inside lambda body; `IMPURE_LAMBDA_BODY`, no graph.

Do not use internal node ids or list order as the oracle. Resolve nodes by rune
identity and edges by socket name.

### TAIL

- TAIL-1: terminal effect with pure NUMBER input is structurally/type valid and
  produces a candidate Unit graph.
- TAIL-2: one let-bound observation feeds one terminal effect.
- TAIL-3: effect in let value is rejected.
- TAIL-4: effect in application argument is rejected.
- TAIL-5: effect nested as a pure-rune argument is rejected.
- TAIL-6: effect in lambda body is rejected as impure.
- TAIL-7: effect nested inside an effect argument is rejected.
- TAIL-8: a concrete non-Unit root may produce only a pure compile candidate.
  The test and handoff must not claim inscription or executable-policy
  admission; that rejection remains deferred to `L0-TM-02`.

TAIL-8 requires no new admission status, API, or executable-policy call.

### BOUND

- BOUND-1: 256 AST nodes accepted structurally; 257 reports `AST_LIMIT`.
- BOUND-2: depth 16 accepted; 17 reports `BINDING_DEPTH_LIMIT`.
- BOUND-3: 64 applications accepted structurally; 65 reports
  `APPLICATION_LIMIT`.
- BOUND-4: literal length 160 accepted; 161 reports `LITERAL_LIMIT`.
- BOUND-5: complete pipeline succeeds at exactly 4,096 charged steps.
- BOUND-6: attempted 4,097th charge returns only `COMPILE_STEP_LIMIT` and no
  graph.
- BOUND-7: existing graph-bound failure remains no-graph.
- BOUND-8: an uncontracted/future combinator has no compiler path and fails
  closed; do not implement a combinator.

## 5. Diagnostic normalization

Use four independent labeled assertions:

1. phase ordering;
2. numeric path ordering (`arguments[2]` before `arguments[10]`);
3. code-name ordering within the same phase and path;
4. duplicate collapse retains the first instance/message.

Messages remain non-semantic except that the fourth assertion proves which
duplicate instance was retained.

## 6. Handoff completion

Update `docs/handoffs/L0_TM_01F3_HANDOFF.md` in place. It must contain:

- a 22-row matrix with no missing or partial row;
- exact method and labeled assertion for every vector;
- exact changed files;
- focused language command, test count, and result;
- standard build command and result;
- confirmation that the public compiler path remains unchanged;
- confirmation that no forbidden boundary changed;
- remaining limitations, including TAIL-8 server admission deferral.

The task remains `NEEDS_FIX` until that repository evidence exists. No new task
or ownership expansion is authorized.
