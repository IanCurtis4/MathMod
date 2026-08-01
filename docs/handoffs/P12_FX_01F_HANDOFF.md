# Handoff: P12-FX-01F

## Result

Closed the two bounded fixture findings from
`docs/P12_FX_01_GATE_REVIEW.md`. This is a fixture-only correction; it does
not claim a DS row pass or release `P12-DS`.

## FX-R1 — DS-02 same-resource snapshot sequence

`p12-ds02-malformed` now provides:

```text
data/mathmod/mathmod/epiphanies/vital_correspondence.json
```

This is the exact resource path/id used by `p12-ds02-valid`. The README now
freezes the required order:

```text
built-in (successful_casts = 2)
  -> valid override (successful_casts = 3)
  -> malformed override of the same path (schema_version = 2)
  -> removal
```

It requires an authoritative observation of `3` before malformed reload and
explicitly classifies fallback to built-in `2` or publication of mixed state as
`SNAPSHOT_FAILURE`, never as retention evidence.

## FX-R2 — DS-05 local rejected-record semantics

The README now describes `p12:broken_reference` as the P7-contract local
rejected-record vector: only that missing-tradition candidate is omitted and
grants no authority. It requires unrelated valid built-in, KubeJS, and
datapack winners to remain coherent. It no longer claims whole-generation
last-known-good retention from this cross-reference case.

## Files changed

- `docs/fixtures/p12/README.md`
- `docs/fixtures/p12/datapacks/p12-ds02-malformed/data/mathmod/mathmod/epiphanies/vital_correspondence.json`
- removed `docs/fixtures/p12/datapacks/p12-ds02-malformed/data/mathmod/mathmod/epiphanies/p12_malformed.json`
- `docs/handoffs/P12_FX_01_HANDOFF.md` remains read-only in this correction
- `docs/handoffs/P12_FX_01F_HANDOFF.md`

No Java, production resource, Gradle, networking, schema, Data Component,
client/UI, public API, `run/`, or external runtime artifact changed.

## Verification

```powershell
Get-ChildItem docs/fixtures/p12 -Recurse -Filter *.json |
  ForEach-Object { Get-Content -Raw $_.FullName | ConvertFrom-Json }
git diff --check
```

Result: every fixture JSON parses and `git diff --check` passes.

## Remaining external dependencies

Sol/operator still own the clean immutable revision/JAR hash, standalone
materialization, corrected semantic datapack observations, all configuration
profiles in fresh worlds, distinct authenticated Client A/Client B proof, and
sanitized artifacts. These remain `ENVIRONMENT_FAILURE` dependencies. No
DS-01–DS-09 row has been run or claimed.

## Next owner

Sol, to review FX-R1/FX-R2 and then resume the external fixture proof if the
corrected bundle is accepted.
