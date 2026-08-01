# P12 Multiplayer Evidence Deferral

**Date:** 2026-08-01  
**Owner:** Sol  
**Decision:** `BACKLOG`, not waived and not passed

## Decision

The release project currently has no second independently authenticated
Minecraft account and no available second player. The two-client evidence
required by DS-06 is therefore deliberately deferred.

This is a product-planning decision, not an inference about runtime safety.
No singleplayer session, offline duplicate identity, simulated second player,
GameTest, or prose claim may be recorded as a DS-06 pass. Public recruitment
on forums is not required to continue repository work.

## Scope of the deferral

The following evidence moves to backlog:

- DS-06, which explicitly requires two independently authenticated clients;
- any future claim whose acceptance result depends on two simultaneous real
  clients or distinct authenticated identities.

Single-client dedicated-server rows, automated tests, fixture verification,
reload diagnostics, immutable-build proof and other repository work are not
deferred by this decision.

## Product classification while deferred

- P9 remains `experimental` and must not be described as multiplayer-ready or
  survival-ready.
- P12 dedicated-server acceptance remains incomplete.
- P14 and later work gain no authority from the missing evidence.
- The absence of a known defect is not a pass.

## Resume condition

Resume DS-06 only when the operator can provide:

1. two distinct licensed accounts authenticated with `online-mode=true`;
2. two simultaneous normal clients allowed by the server whitelist;
3. the same immutable MathMod build and frozen fixture manifest;
4. sanitized evidence containing no player names, addresses, ports or world
   seed.

Any future contract may refine the scenario, but it may not weaken distinct
identity or server-authority requirements without a new Sol decision.
