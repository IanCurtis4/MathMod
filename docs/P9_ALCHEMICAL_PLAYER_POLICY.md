# P9 Alchemical Player And Balance Policy

## Status And Purpose

Status: P9 Terra High policy authority frozen on 2026-07-22; its Terra Medium
temporary-effect slice is now implemented. This document defines the
player-facing balance and authorization rules for the alchemical effects.

P9 expands the existing `effect_plan` language with defensible temporary
effects: cleansing, resistance, absorption, and tightly controlled item
transmutation. It also establishes what a future permanent infusion must prove
before it can exist. The mod continues to use exact items as its only cast
resource: P9 introduces neither mana nor cooldowns.

P9 is constrained by the P8 transaction boundary, the P11 physical-profile
contract, the existing tier system, and player ownership. A visually helpful
effect must never become an unconsented multiplayer control surface or a way to
turn reloadable material metadata into permanent power.

## Non-Negotiable Separations

| Concern | Authority | P9 rule |
| --- | --- | --- |
| Exact item payment | server inventory transaction | Pays before an effect mutates a target. |
| Attribute evidence and rune tier | immutable cast cost plan | Gates graph construction and payment; it is not an effect magnitude. |
| Target authorization | server cast context | Determines who may be affected; the client never grants consent. |
| Temporary status effect | Minecraft effect lifecycle | Expires normally and carries no persistent player record. |
| Future permanent infusion | separate server-owned record | Is deferred until its own persistence and migration contract exists. |
| P11 physical profile | server physical snapshot | May inform only a separately bounded temporary schedule; it never changes item payment or grants a permanent attribute. |

An inscribed graph, selected resource list, saved talisman, or client preview
does not authorize a player target. It only describes a candidate cast.

## Cast Context And Target Firewall

P9-A recognizes three target classes:

| Class | Source | P9-A permission |
| --- | --- | --- |
| `SELF_PLAYER` | the casting player represented by `player_as_entity_list` | allowed for beneficial and cleansing plans |
| `NON_PLAYER_LIVING` | a server-resolved non-player living entity | allowed when the specific rune permits it |
| `OTHER_PLAYER` | any `ServerPlayer` other than the caster | rejected |

The initial player policy is deliberately self-first. A nearby-entity query,
filter, list supplied through a graph, or a raycast must not allow an
alchemical P9 plan to affect another player. Existing harmful plans retain the
stricter rule that they skip all players. Anchors have no player identity and
P9 plans cannot execute from an anchor at all, including one beside a nearby
owner: anchors carry neither a player identity nor an item-payment authority.

Cross-player support is deferred rather than inferred from proximity, party
membership, teams, claims, friendship mods, or a client checkbox. A future
consent design must provide a server-owned, visible, revocable opt-in state;
it must define logout, death, respawn, dimension transfer, and owner-change
behavior before a beneficial rune may use it.

Every P9-A effect plan resolves exactly one eligible living target. `SELF_PLAYER`
is the default. A non-player target must be alive, within the existing query
radius, and still eligible when the plan is committed. Area support and
multi-target defensive buffs are a later balance slice because they require an
explicit per-target payment model.

## Transaction And Execution Order

The current generic executor evaluates an effect plan before it consumes its
cost. That ordering is not valid for new P9-A effects. Their server execution
must use this sequence:

```text
validate graph and static limits
  -> resolve one authorized target and a bounded effect plan
  -> validate full item availability and tier evidence
  -> take an exact-item escrow snapshot
  -> revalidate target liveness and authorization
  -> apply the bounded temporary effect
  -> finalize escrow with existing Conservation rolls
  -> record successful cast and progression
```

If preflight, escrow, or immediate target revalidation fails, P9 performs no
effect mutation. If a target becomes invalid after escrow but before mutation,
the escrow is restored and the cast fails. After a valid effect mutates a
target, payment is final even if particles or sound fail to present.

Catalysts are validated and retained; consumed items are the only inputs that
can receive the existing Conservation roll. Parsimony reduces positive
attribute requirements under its current floor, but never fixed items,
target policy, tier, effect duration cap, or effect magnitude. P9 adds no new
percentage, fixed-item, target-count, or duration metamagic discounts.

## Balance Bands

P9 evaluates effect strength by a fixed band declared in the rune definition,
not by server tick rate, selected material count, physical mass, or a client
numeric value.

| Band | Typical use | Minimum tier | Payment shape | Initial bound |
| --- | --- | ---: | --- | --- |
| I | cleansing utility | II | one consumed reagent | one self target; remove at most three allowlisted harmful effects |
| II | resistance or absorption | III | one consumed reagent plus one catalyst | one eligible target; at most 30 seconds; level I-II only |
| III | controlled item transmutation | IV | two consumed reagents plus one catalyst | one exact input stack, one allowlisted output, no world blocks |
| IV | permanent infusion | IV | not yet defined | forbidden in P9-A |

The global execution budget remains a graph-complexity limit, not a currency.
The selected reagent's declared attributes and exact quantities remain the
visible price. Repeated casts have no hidden cooldown, escalating charge, or
mana drain; their limiting factor is the consumed item plan and the normal
temporary-effect lifecycle.

P9-A defensive effects use the following maximums, regardless of rune graph
shape:

| Effect | Player policy | Duration | Amplifier / amount | Refresh rule |
| --- | --- | ---: | --- | --- |
| Cleansing | self only | instant | remove up to three allowlisted harmful effects | never removes beneficial effects |
| Resistance | self or one non-player living target | 30 seconds | levels I-II | weaker casts cannot shorten or downgrade a stronger active effect |
| Absorption | self or one non-player living target | 30 seconds | 4 or 8 temporary health points | never accumulates beyond the strongest active instance |
| Vital Infusion | existing target policy until P9-A migration | existing cap | existing registered modifier | remains temporary; no persistent attribute write |

The allowlisted cleansing set starts with vanilla harmful effects only. It
does not remove unknown mod effects, beneficial effects, permanent modifiers,
server sanctions, or another mod's custom capability. Adding a mod effect to
the list requires a namespaced, data-validated declaration and a dedicated
compatibility review.

## Controlled Transmutation

P9-A's word "transmutation" refers only to an exact, server-validated item
transaction. It may consume one input stack and produce one explicitly
allowlisted output stack with a bounded count. It does not change placed
blocks, inventories owned by other players, entities, block entities, fluid,
NBT, components, durability, enchantments, or data from another mod.

The future executor must preflight output insertion before consuming input. If
the output cannot fit, it produces no output and leaves the input untouched.
No dropped overflow is created. A transmutation definition cannot call
JavaScript, inspect arbitrary item data, or choose an output from a tag at
cast time.

## P11 Physical Boundary

P9-A consumes no physical mass, density, inertia, hardness, blast resistance,
or P11 source label when it calculates a defensive effect, healing, cleansing,
or transmutation. Material profiles remain meaningful for construct planning
and future bounded presentation, but an iron or obsidian profile must not
silently increase player armor, absorption, duration, damage, or cost.

A future physics-scaled effect requires a new P9 annex that specifies:

1. the server-resolved physical input and snapshot version;
2. a monotonic finite transform with an explicit cap;
3. how its exact item payment is kept independent of physical mass;
4. its target, claim, rollback, and dedicated-server tests.

Terrain damage, penetration, explosions, knockback scaling, and permanent
physical infusion remain outside P9-A.

## Permanent Infusion Gate

Permanent infusion is not a longer status effect. Before implementation, a
separate persistence contract must define all of the following:

- a server-owned, versioned record with an explicit owner and immutable source;
- one bounded slot or mutually exclusive family per player, including removal;
- attribute allowlists, UUID stability, stacking and equipment interaction;
- exact item escrow, failure recovery, death/respawn behavior, and migration;
- operator controls, datapack removal behavior, multiplayer visibility, and
  dedicated-server tests.

P9-A may continue to use the existing temporary `Vital Infusion` effect. It
must not serialize its modifiers into player data or present it as permanent.

## Counterexample Matrix

| Situation | Rejected shortcut | Required result |
| --- | --- | --- |
| Nearby second player | query with `players` predicate | no P9 effect plan may target them |
| Anchor beside players | treat the anchor as its owner | P9 effect skips all players |
| Empty inventory after evaluation | apply then attempt payment | no mutation before escrow succeeds |
| Target dies during cast | consume and apply to a stale entity | restore escrow and fail before mutation |
| Conservation active | skip availability preflight | require the full plan, then roll only consumed units |
| Repeated resistance cast | add duration or amplifier without bound | retain the strongest bounded instance only |
| Cleansing | remove every active effect | remove only allowlisted harmful effects |
| Obsidian reagent/profile | use physical mass as armor | no P11 value changes P9-A strength |
| Full output inventory | consume and drop transmutation overflow | leave input untouched and make no output |
| Reload during temporary effect | rewrite active player state | existing temporary effect completes normally |

## Delivery Slices And Model Assignment

### P9-Terra High: Balance And Player Policy

- Completed by this document: target firewall, self-first player policy,
  anchor exclusion, pre-mutation escrow order, effect bands, refresh rules,
  metamagic restrictions, P11 boundary, and permanent-infusion gate.
- **Model:** Terra with High effort.

### P9-Terra: Temporary Effect Implementation

- Implemented: one-target preflight, pre-mutation exact-item escrow, and
  failure restoration on the shared talisman path.
- Implemented: cleansing, resistance, and absorption; the Homuncular Matrix
  is retained as a catalyst while Vital Salt is consumed.
- Implemented: structural validation plus a dedicated-server anchor rejection
  test. Player-target, escrow, refresh, full-output, and reload fixtures
  remain targeted follow-up coverage rather than implicit guarantees.
- **Model:** Terra with Medium effort.

### P9-Luna: Teaching And Presentation

- Implemented: existing reagent recipes are documented, the three defensive
  rune textures are present, the bilingual alchemical entry teaches the P9
  target and payment laws, and preview cases cover the defensive resource
  panel in both locales and the compact viewport.
- **Model:** Luna.

## P9-A Acceptance Criteria

1. No P9-A cast can affect a non-caster player or any player from an anchor.
2. A failed availability or target check produces no effect mutation.
3. Every P9-A effect consumes exactly the committed resource plan, subject only
   to the existing per-unit Conservation rule.
4. Defensive effects cannot downgrade, unboundedly stack, or persist through a
   player record.
5. Cleansing cannot remove a beneficial or unallowlisted effect.
6. Transmutation cannot mutate the world or lose an input when output lacks
   space.
7. P11 physical values cannot scale a P9-A player effect.
8. The dedicated server passes the target, transaction, and reload tests before
   a permanent infusion proposal is accepted.
