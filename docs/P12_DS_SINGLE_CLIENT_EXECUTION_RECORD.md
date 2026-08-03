# P12-DS Single-Client Execution Record

**Date:** 2026-08-02  
**Owner:** Sol + operator  
**Status:** `NEEDS_FIX`  
**Batch:** `2026-08-02-ce64b9b`  
**Excluded row:** DS-06 remains `P12-DS-MP BACKLOG`

> Operational supersession, 2026-08-03: this document remains the failure
> record for batch `2026-08-02-ce64b9b`. The product corrections through
> `P12-TM-05F` are accepted in
> `docs/P12_TM_05F_FINAL_GATE_ACCEPTANCE.md`. The `P12-DS` task is now `READY`
> to create a different immutable artifact/batch and rerun DS-01 from a clean
> checkpoint. Nothing below converts the old batch into a pass.

## Immutable identity

```text
commit: ce64b9bbc1d3ef48d3231be13ebad1203d9eb7e7
MathMod: 0.2.0-beta.1
MathMod JAR SHA-256: 9FF1CFE7D094BBB8E86E5739E9600C954A42ED9DC164EFD49EC6F6B74CFC725F
Minecraft: 1.21.1
NeoForge: 21.1.234
Java: Oracle 21.0.8+12-LTS-250
```

Frozen optional integration artifacts:

```text
KubeJS SHA-256: 01767BB677A9C4A8F318717C4C21BCA7E7EF80995603403A551068A0E064E740
Rhino SHA-256: E0E9B0E78EDD380440266C0F4EA8D489DAC851EF075A4566A66A6DAE2F7BBB66
baseline configuration SHA-256: 86E69BF1828A97179919139858C67E5303330DC9043F4FE1CAC14985C24BEE77
```

## Client preparation

The existing authenticated Minecraft 1.21.1/NeoForge 21.1.234 profile was
prepared reversibly for the standalone fixture:

- 479 original mod files were moved intact to an operator-local backup;
- no original mod or script was deleted;
- the active profile contains exactly the accepted MathMod, KubeJS and Rhino
  JARs above;
- the committed fixture KubeJS startup script is installed;
- an operator-local restoration script preserves the three P12 client JARs
  separately and restores the complete original profile;
- authentication dialogs, tokens and account state remain exclusively under
  operator control.

The Windows capture boundary could not inspect the CurseForge window because
the application returned `0x80004002` (`interface not supported`). This is not
substituted by blind UI input. Launcher start, authentication and the normal
client join remain the operator's manual actions.

## DS-01 prepared checkpoint

The neutral DS-01 world was created from scratch with the baseline profile.
Observed before any client join:

```text
server ready markers: 1
server startup errors: 0
KubeJS startup scripts: 1/1, 0 errors, 0 warnings
whitelisted authenticated identities: 1
world: fresh DS-01 identifier
```

The server ran under an interactive Sol control bridge. The bridge expanded an
internal player placeholder without writing the player's name to the
repository or evidence commands.

### Required live sequence

1. Operator launches the prepared profile and joins the standalone fixture
   with the already whitelisted authenticated account.
2. Sol provisions two programmed talismans through ordinary server commands.
3. Client A inscribes a known baseline proof into the original talisman,
   closes the menu and records its visible identity/state.
4. Client A reopens the Rune Programmer and prepares a Laboratory edit without
   committing it.
5. Sol preserves the original talisman in another inventory slot and replaces
   the held target with the second talisman through ordinary server commands.
6. Client A submits from the still-open stale menu.
7. Record immediate result, connection state and the exact before/after state
   of both talismans.
8. Repeat the contract's closed-menu transition, again recording both items
   and confirming that no stale action mutates either target.
9. Stop the server cleanly before preserving the checkpoint and sanitized log.

Acceptance requires both talismans to remain exact, stale mutation rejection
and no client disconnect. A screen closing by itself, lack of an exception or
a visually plausible item is insufficient without the before/after item
observations.

### Runtime result and gate finding

The authenticated client joined and reached the real Laboratory on the
provisioned original talisman. Its pre-crash item observation showed a valid
program/resource state. Selecting `Self player` and then rendering its hovered
preview crashed the client twice with the same MathMod stack:

```text
java.lang.IllegalArgumentException: A Laboratory form must add at least one rune
    at com.mathmod.program.CustomActionPreview.<init>(CustomActionPreview.java:16)
    at com.mathmod.program.CustomSpellWorkspace.preview(CustomSpellWorkspace.java:87)
    at com.mathmod.screen.RuneProgrammerScreen.customActionTooltip(RuneProgrammerScreen.java:2886)
    at com.mathmod.screen.RuneProgrammerScreen.render(RuneProgrammerScreen.java:1191)
```

Sanitized raw-report identities:

```text
crash-2026-08-02_21.04.17-client.txt
SHA-256: EA1690321EB7D4C3B5C9E7ACC89B5E7DEB4EF2A4BA0A389A2281B08F66CBFC58

crash-2026-08-02_21.09.30-client.txt
SHA-256: 6F66643E28504B30C8DEF4105D1BE8B5523485139DC6EF66DCE25E5419716385
```

The raw reports remain outside Git. Both identify the accepted three-mod
client fixture. The server was then stopped cleanly through the control bridge
(`P12_DS01_STOPPED=true`, exit code 0); no server-side MathMod exception was
observed. The planned stale-item before/after sequence did not complete, so no
item-mutation pass is claimed.

Repository inspection also confirms the stale-target risk:
`RuneProgrammerMenu.canMutateWorkspace` checks the active container and item
type, while the component-exact captured-target comparison guards only the
read-only projection. This is a concrete authority counterexample even though
the client crash prevented the live stale submission.

The complete decision and bounded `P12-TM-04` correction ownership are frozen
in `docs/P12_DS_01_GATE_REVIEW.md`. That correction is now `DONE` with
`ACCEPT` under `docs/P12_TM_04_FINAL_GATE_ACCEPTANCE.md`; DS-01 still awaits
the accepted P12-TM-05F presentation closure, a new immutable artifact/batch
and a clean rerun.

## Row state

| Row | State | Next evidence boundary |
| --- | --- | --- |
| DS-01 | `FAIL` — `CLIENT_CRASH / AUTHORITY_RISK` in the old batch; new rerun `READY` | new JAR/batch and full clean-checkpoint rerun |
| DS-02 | `BLOCKED` | accepted DS-01 correction and rerun |
| DS-03 | `BLOCKED` | accepted DS-01 correction and rerun |
| DS-04 | `BLOCKED` | accepted DS-01 correction and rerun |
| DS-05 | `BLOCKED` | accepted DS-01 correction and rerun |
| DS-06 | `BACKLOG` | two distinct authenticated clients; not passed or waived |
| DS-07 | `BLOCKED` | accepted DS-01 correction and rerun |
| DS-08 | `BLOCKED` | accepted DS-01 correction and rerun |
| DS-09 | `BLOCKED` | accepted DS-01 correction and rerun |

No DS row is accepted by this preparation record. Each row receives its own
sanitized evidence file only after the required client and authoritative
server observations exist.

## Stop conditions

- Any authority, snapshot, resource, inventory, reload, chunk or terrain
  counterexample is a product finding, not permission to alter the fixture.
- A missing client action or launcher failure is `ENVIRONMENT_FAILURE`, not a
  pass.
- Raw logs, account/player names, addresses, ports, machine paths, access
  tokens and world seeds remain outside Git.
- DS-06 stays outside this batch under the explicit multiplayer deferral.
