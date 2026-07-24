# Rune Tiers And Metamagic

## Implemented Slice

- Every rune has a `RuneTier`: Fundamental (I), Refined (II), Arcane (III), or Metamagical (IV).
- A graph requires the highest tier among its runes.
- Tier I is the baseline. Tier II-IV require a selected or fixed material of sufficient tier that actually contributes a required attribute, necessary budget, or fixed selector.
- The cost planner exposes required and provided tier. Unrelated high-tier filler does not unlock a graph.

## Cast Snapshot

Player casts snapshot active metamagic before validation and world execution. The same immutable `ProgramCostPlan` is then consumed. An effect granted during evaluation cannot discount or conserve the cast that granted it.

Anchors do not have a player inventory or player effects and remain outside this modifier path.

## Parsimony

- Registered beneficial effect: `mathmod:parsimony`.
- Tier-IV rune: `mathmod:parsimony_plan`.
- Reduces every positive attribute requirement by one per effect level.
- Reduction is capped at two and no requirement falls below one.
- Fixed item requirements, budget, and rune tier are unchanged.

## Conservation

- Registered beneficial effect: `mathmod:conservation`.
- Tier-IV rune: `mathmod:conservation_plan`.
- Each consumed unit independently survives with a 15% chance per effect level, capped at 45%.
- Full planned availability is checked before execution.
- Catalysts are never rolled because they were not consumable.

## Default Tier-IV Witnesses

| Material | Mode | Attributes | Budget |
| --- | --- | --- | --- |
| `axiomatic_ink` | consumed | metamagic 3, economy 3 | 10 |
| `recursive_seal` | catalyst | metamagic 3, conservation 3, stability 2 | 12 |

Both are craftable and configurable through the material API.

## Acceptance Evidence

- `metamagic-parsimony-cast` consumes two Axiomatic Ink before granting Parsimony, then validates a one-ink follow-up plan at reduced attribute requirements.
- `metamagic-conservation-cast` preserves two Recursive Seal catalysts and validates the Conservation effect.
- Pure tests cover contributing-tier rejection, unrelated high-tier filler, recommendation quality, attribute floors, immutable base cost, and per-unit conservation rolls.
- The dedicated GameTest distribution loads MathMod without client classes. No world GameTest functions are registered yet.

## Deferred

- Percentage attribute reductions, fixed-item discounts, shared-party buffs, duration extension, and automatic rune substitution. P9 also freezes these exclusions for new defensive and transmutation effects until cost telemetry and a new player-policy review exist.
- Additional tier gates tied to progression, discoveries, manuscripts, or professions.
- Permanent metamagic or arbitrary KubeJS execution callbacks.
