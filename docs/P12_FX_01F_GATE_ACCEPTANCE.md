# P12-FX-01F Gate Acceptance

**Date:** 2026-08-01  
**Reviewer:** Sol  
**Decision:** `ACCEPT`

## Scope accepted

Sol reviewed the handoff and real fixture delta for the bounded findings
`FX-R1` and `FX-R2` recorded in `docs/P12_FX_01_GATE_REVIEW.md`.

Accepted files:

```text
docs/fixtures/p12/README.md
docs/fixtures/p12/datapacks/p12-ds02-malformed/data/mathmod/mathmod/epiphanies/vital_correspondence.json
docs/handoffs/P12_FX_01F_HANDOFF.md
```

The obsolete fixture resource
`data/mathmod/mathmod/epiphanies/p12_malformed.json` no longer exists.

No production Java/resource, Gradle configuration, networking, Data
Component, schema, client/UI, public API or ignored runtime state changed in
this correction.

## Finding closure

### FX-R1 — closed

The malformed datapack now overrides the exact same resource path and id as
the valid DS-02 replacement:

```text
mathmod:epiphanies/vital_correspondence
```

The sequence is explicit:

```text
built-in successful_casts=2
    -> valid replacement successful_casts=3
    -> malformed same-resource schema_version=2
    -> removal
```

Fallback to `2` or mixed publication is explicitly classified as
`SNAPSHOT_FAILURE`; it cannot be recorded as retention evidence.

### FX-R2 — closed

The missing-tradition manuscript is now correctly described as a local
rejected-record vector under the P7 contract. The expected observation is
that only `p12:broken_reference` is omitted and grants no authority while the
independent built-in, KubeJS and datapack winners remain coherent. No
whole-generation last-known-good claim remains.

## Reproduced evidence

```powershell
Get-ChildItem docs/fixtures/p12 -Recurse -Filter *.json |
  ForEach-Object { Get-Content -Raw $_.FullName | ConvertFrom-Json }
```

Result: all eight fixture JSON files parsed.

Repository assertions:

```text
correct same-resource malformed file exists: true
obsolete p12_malformed file exists: false
valid schema/value: 1 / 3
malformed schema: 2
```

```powershell
git diff --check
```

Result: pass; line-ending conversion warnings are not whitespace errors.

## Gate decision

- `P12-FX-01F`: `DONE` with `ACCEPT`;
- the fixture content corrections are closed;
- `P12-FX-01`: remains `BLOCKED` only on the external Sol/operator evidence;
- `P12-DS`: remains `BLOCKED`;
- no DS-01 through DS-09 result has been run or accepted by this gate.

Remaining external dependencies are:

1. a clean immutable Git revision and JAR hash;
2. semantic runtime observation of the corrected datapack sequence;
3. fresh-world observation of every configuration profile;
4. distinct authenticated Client A and Client B joins;
5. sanitized proof artifacts.

These remain `ENVIRONMENT_FAILURE` dependencies. The next owner is
Sol + operator; Terra Medium ownership is released.
