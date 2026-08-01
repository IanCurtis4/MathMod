# P12 dedicated-server fixture bundle

This is the committed, copyable input bundle for the P12 dedicated-server
matrix. It is fixture scaffolding, not DS evidence: do not claim DS-01 through
DS-09 passed merely because this bundle materializes or a server reaches ready
state.

## Ownership and boundary

This bundle may be copied into a newly created standalone NeoForge 21.1.234
server. It does not modify MathMod production code, data, configuration, API,
or ignored `run/` state. Sol/operator must select and hash the compatible
KubeJS stack, provision the standalone server, provide two distinct
authenticated accounts, and record the actual launch proof. Never commit a
world, seed, host, port, account name, token, IP, raw log, or machine path.

## Immutable preflight

Run before materializing a fixture and again before every DS batch:

```powershell
git status --porcelain
git rev-parse HEAD

$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat clean build --no-daemon
Get-FileHash -Algorithm SHA256 C:\mathmod-build\MathMod\libs\mathmod-0.2.0-beta.1.jar
```

The status output must be empty. Record the commit and JAR hash in a copied
fixture manifest. The source commit, JAR hash, installer hash, KubeJS hashes,
configuration hash, and datapack hashes must remain fixed for the entire
batch.

## Materialize a new standalone server

1. Sol/operator obtains the exact NeoForge `21.1.234` installer and records
   its coordinate/name and SHA-256 in the copied manifest.
2. Install it into a new empty directory outside the repository and launch the
   generated dedicated-server script with `nogui` once to obtain the EULA and
   generated layout. Do not copy or reuse `run/server/world`.
3. Accept the EULA locally, stop cleanly, then copy `server/server.properties`
   to the server root and `config/<profile>/mathmod-server.toml` to
   `config/mathmod-server.toml`.
4. Copy the clean built MathMod JAR to the server `mods/` directory. Install
   only the operator-recorded KubeJS artifact set, then copy
   `kubejs/startup_scripts/mathmod_manuscripts.js` unchanged.
5. Copy the listed fixture datapack directories to the new world's
   `datapacks/` directory only when that row calls for them; use one fresh
   neutral world per row. Enable/disable datapacks with ordinary server
   commands and use ordinary `/reload` only for reload rows.
6. Start with `nogui`; retain the raw log outside Git and record a sanitized
   ready-state artifact id. Client A and Client B must join with separate
   authenticated accounts while `online-mode=true` and `white-list=true`.

`fixture-manifest.example.json` deliberately contains operator-filled values:
inventing artifact hashes, account proof, or a ready-state result is invalid.

## Configuration profiles

| Profile | Intended use |
| --- | --- |
| `baseline` | control and DS-01 through DS-07/DS-09 unless a row says otherwise |
| `loot-disabled` | DS-08 fresh-world loot-disabled observation |
| `profession-disabled` | DS-08 fresh-world profession-disabled observation |
| `trades-disabled` | DS-08 fresh-world trades-disabled observation |
| `house-disabled` | DS-08 fresh-world house-disabled observation |

The baseline and house-disabled profiles deliberately both keep the house flag
false: DS-08 compares fresh worlds and independently observes the other flags;
the normal P10 default must not be inferred from an existing ignored runtime.

## Datapack inventory

Install only the fixture(s) named for the action and record each directory's
SHA-256 inventory in the copied manifest. The resource paths are real reload
paths, not test hooks.

| Fixture | Row | Resource(s) | Expected result |
| --- | --- | --- | --- |
| `p12-ds02-valid` | DS-02 | `mathmod:epiphanies/vital_correspondence`, `successful_casts = 3` | valid replacement publishes after reload and is distinguishable from the built-in value `2` |
| `p12-ds02-malformed` | DS-02 | the same `mathmod:epiphanies/vital_correspondence` path, schema version `2` | malformed override must not replace the active valid `successful_casts = 3` snapshot; fallback to built-in `2` or a mixed candidate is `SNAPSHOT_FAILURE` |
| removal after the malformed checkpoint | DS-02 | remove malformed override, then valid override | the frozen sequence is built-in -> valid replacement -> malformed override -> removal; record the authoritative missing/previous-record result without hand-editing a world |
| `p12-ds05-valid` | DS-05 | built-in `bound_measure` replacement plus `example:constant_fields` | valid manuscript publication; data pack wins the same id contributed by KubeJS |
| `p12-ds05-rejected-cross-reference` | DS-05 | `p12:broken_reference` manuscript with missing tradition | local rejected-record vector: only `p12:broken_reference` is omitted and grants no authority; unrelated valid built-in, KubeJS, and datapack winners remain coherent |
| `p12-ds07-remove-one-offer` | DS-07 | `rotated_horizon` acquisition without `trade` | exactly its marked trade is rejected on a closed-menu reconciliation; valid/unmarked state is checked separately |
| `p12-ds09-distinct-physical-profile` | DS-09 | `minecraft:stone` density changes 1.0 to 2.0 | valid bounded profile replacement provides a visibly distinct N+1 profile |

For DS-02, install the valid pack first, observe authoritative
`successful_casts = 3`, then give the malformed pack higher pack priority over
the exact same resource and reload. Do not call this retention if the state
falls back to the built-in `2` or contains a mixed candidate: that is a
`SNAPSHOT_FAILURE`. Only after preserving that checkpoint may the operator
remove the malformed override and then the valid override.

For DS-05, the bad cross-reference is deliberately a local rejected-record
case, not whole-generation last-known-good evidence. The row operator proves
that `p12:broken_reference` is absent/no-authority while the independent
`mathmod:bound_measure` and `example:constant_fields` winners remain coherent.

The row operator must first prove every observed candidate result from the
authoritative log/state. A reload failure or unexpected semantic result is a
P12 failure record, not permission to edit the fixture or world in place.

## Checkpoints and evidence

Create fresh worlds named only by neutral row ids. Stop the server before any
whole-world backup/restore; never restore a partial `world/`. Keep raw logs
outside Git. Copy `evidence/P12_DS_EVIDENCE_TEMPLATE.md` to the sanitized
evidence location specified by the readiness contract:

```text
docs/evidence/p12/<batch-id>/P12_DS_01.md ... P12_DS_09.md
docs/evidence/p12/<batch-id>/P12_DS_BATCH_SUMMARY.md
```

The batch summary records exact hashes, row order, reload/reconnect/restart
boundaries, sanitized log ids, failures/repeats/deviations, and confirmation
that neither GameTest nor integrated singleplayer evidence was substituted.
