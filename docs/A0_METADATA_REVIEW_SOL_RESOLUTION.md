# Sol Resolution — A0 Metadata Semantic Review

**Date:** 2026-07-26

**Input:** `A0_METADATA_SEMANTIC_REVIEW.md`

**Decision:** accept Terra High's `REJECT` recommendation for A0-3.

## Operational consequence

`A0-TM-02` must not start. A bounded correction of the A0-1/A0-2 metadata
foundation and a delta semantic re-review are required first.

The accepted 67-form and 11-category compatibility characterization is not
discarded. The correction is limited to the metadata model, validation,
diagnostics, ordering queries, and focused tests.

## Sol decisions

1. Semantic compatibility must use an immutable structured value object, not a
   delimiter-concatenated string. It is an internal comparison value and is not
   persisted, networked, or exposed as a public format.
2. Expansion-consumed input identities must be represented separately from
   descriptive input hints. Only the consumed identities participate in
   semantic compatibility.
3. The section 10 limits of the frozen A0 contract are prerequisites for A0-3.
   Semantic collections fail before snapshot publication; they are never
   truncated.
4. Candidate failure must expose structured diagnostics with the contract
   fields and stable codes. Exception text is developer detail, not protocol.
5. The snapshot must expose deterministic ordered category and form queries.
   Consumers may not rely on `Map` iteration order.
6. Enum characterization must compare the `enumName -> canonical form id`
   mapping independently of `CustomSpellAction.values()` order. Palette order
   remains the explicit compatibility-table order.
7. No correction may add persistence, a Data Component, networking, public API,
   external loader, replay adapter, or `ProgramGraph` change.

## Required evidence

- FP-1 and FP-2 structured-fingerprint vectors;
- parameter key/type/default/bound/order and adapter sensitivity;
- 17 parameters and 17 consumed inputs rejected;
- 1,025 forms and 129 categories rejected;
- 161-character bounded keys rejected;
- structured `DUPLICATE_ID`, `UNKNOWN_CATEGORY`, and `LIMIT_EXCEEDED`;
- deterministic category tie ordering;
- enum-order-independent identity characterization;
- focused tests and standard build;
- complete handoff;
- Terra High delta re-review with `APPROVE` before A0-3.

