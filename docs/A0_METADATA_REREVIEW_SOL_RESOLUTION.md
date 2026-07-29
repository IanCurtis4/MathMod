# Sol Resolution — A0 Metadata Semantic Re-review

**Date:** 2026-07-26

**Input:** `A0_METADATA_SEMANTIC_REREVIEW.md`

**Decision:** accept Terra High's second `REJECT` recommendation for A0-3.

## Cause analysis

`A0-TM-01F` correctly closed the original semantic-fingerprint, consumed-input,
diagnostic, ordering, enum-characterization, and tested-bound findings.

The remaining rejection has one cause:

- `AuthoringMetadata.Snapshot` limits forms, categories, aliases, and
  diagnostics, but does not limit `runePresentations` to the contract maximum
  of 2,048.

The built-in factory currently supplies an empty presentation map, so ordinary
built-in construction masks the defect. The public snapshot invariant still
accepts 2,049 entries, which violates section 10 of the frozen A0 contract.

This is a boundedness omission, not a change to identity, persistence,
authority, precedence, migration, or ownership.

## Sol decision

Create one minimal implementation task and one delta-only review:

```text
A0-TM-01P — enforce RunePresentation snapshot bound
    ->
A0-TH-01R2 — verify only the presentation-bound delta
    ->
A0-TM-02 may become READY only after APPROVE accepted by Sol
```

The already accepted 67 forms, 11 categories, structured fingerprint,
consumed-input separation, diagnostics, and ordering findings are not reopened.

## Required correction evidence

- a named maximum of 2,048 Rune Presentation descriptors;
- 2,048 presentations accepted;
- 2,049 presentations rejected with structured
  `DiagnosticCode.LIMIT_EXCEEDED`;
- no snapshot returned for the rejected candidate;
- focused A0 metadata tests pass;
- standard build passes;
- no adapter, replay, persistence, Data Component, networking,
  `ProgramSurfaceMode`, screen, public API, or `ProgramGraph` change;
- repository handoff.

## Deferred observations

The re-review's observations about fully encapsulating direct snapshot
construction and completing external-source candidate assembly remain deferred
to the external A0 loader/public-source contract. They do not authorize
external loaders or broaden this correction.

