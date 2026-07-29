# A0-LU-01 Content Gate Review

**Reviewed task:** `A0-LU-01`  
**Date:** 2026-07-26  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX` — documentation evidence only

## Accepted content delta

- All changed production content is within the exact authorized file list.
- The language and changed Patchouli JSON files parse as UTF-8.
- EN and PT-BR retain identical key sets: 804 keys each.
- The five PT-BR Inspector corrections are presentation-only.
- `inspector.json` remains explicitly read-only and distinguishes future
  Advanced editing.
- `beta_theorems.json` distinguishes compact formula presentation from
  persisted Source/Function and removes the `Advanced Movement` ambiguity.
- `docs/UI_PREVIEWS.md` adds requirements only; no Java harness or screenshot
  artifact changed.
- No texture, glyph, stable id, persistence, graph, adapter, networking, or
  public API changed.
- The standard build passes with these resources.

No production-content rollback is requested.

## Blocking handoff evidence

### LU-R1 — icon manifest is not an exact 67-form manifest

The handoff uses “`mathmod:self_player` through all existing `mathmod:*`
values” and refers readers to `CustomSpellAction`. The approved output requires
an icon-reuse manifest keyed by the existing ids. A range and source reference
do not establish that all 67 forms were enumerated, that each icon rune id
resolves, or that each expected texture exists.

Required evidence:

- one row per 67 frozen Rune Forms;
- form id;
- icon rune id;
- expected existing texture path;
- file existence result;
- fallback classification when applicable;
- duplicate/shared icon note without treating an icon as identity.

### LU-R2 — narrator matrix omits required columns and exact copy

The matrix refers to “Existing EN key” and “Corrected PT-BR key”. The Sol
decision requires exact EN copy, PT-BR copy, fallback, and future owner for
each current focus/state row.

Required evidence:

- exact key and state;
- exact EN text;
- exact PT-BR text;
- substitution arguments;
- missing-key/technical fallback;
- current consumer or future integrator owner;
- whether runtime evidence belongs to A0-TM-03 or A0-TM-04.

### LU-R3 — changed-key audit lacks old/new values in both locales

The handoff lists changed keys but does not record old and new EN/PT-BR values
as required by `A0_TERMINOLOGY_AND_CONTENT_DECISION.md`.

Required evidence:

- for each of the five Inspector keys, record old EN, new EN, old PT-BR, and
  new PT-BR;
- state explicitly that EN is unchanged where applicable.

## Bounded correction task

Create `A0-LU-01F`, owned by Luna.

Exact write ownership:

```text
docs/handoffs/A0_LU_01F_HANDOFF.md
```

All production resources, Java, tests, textures, previews, stable ids,
persistence, networking, and graph files are read-only. The correction is an
evidence completion, not a content rewrite.

Required output:

```text
docs/handoffs/A0_LU_01F_HANDOFF.md
```

It must close LU-R1 through LU-R3 with explicit tables and include the commands
used to parse JSON, compare locale keys, enumerate 67 forms, and check texture
existence.

## Downstream decision

`A0-LU-01` becomes `NEEDS_FIX`. `A0-LU-01F` is `READY`.

Because A0-TM-03 would consume the now-present Luna content and narrator/
fallback requirements, its start remains blocked until this evidence
correction is accepted.
