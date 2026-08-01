# Handoff: P12-FX-01

## Result

**BLOCKED — external standalone-fixture proof.** The complete committed
fixture bundle is assembled under `docs/fixtures/p12/`. Its required paths and
all JSON documents validate locally. The remaining artifact selection/hashing,
standalone launch, and distinct authenticated-actor proof are owned by Sol and
the operator under the frozen task; none can be truthfully inferred from the
ignored `run/` state or a GameTest run.

No DS-01 through DS-09 result is claimed.

## Files added

- `docs/fixtures/p12/README.md`
- `docs/fixtures/p12/fixture-manifest.example.json`
- `docs/fixtures/p12/server/server.properties`
- `docs/fixtures/p12/config/{baseline,loot-disabled,profession-disabled,trades-disabled,house-disabled}/mathmod-server.toml`
- `docs/fixtures/p12/kubejs/startup_scripts/mathmod_manuscripts.js`
- `docs/fixtures/p12/datapacks/p12-ds02-valid/**`
- `docs/fixtures/p12/datapacks/p12-ds02-malformed/**`
- `docs/fixtures/p12/datapacks/p12-ds05-valid/**`
- `docs/fixtures/p12/datapacks/p12-ds05-rejected-cross-reference/**`
- `docs/fixtures/p12/datapacks/p12-ds07-remove-one-offer/**`
- `docs/fixtures/p12/datapacks/p12-ds09-distinct-physical-profile/**`
- `docs/fixtures/p12/evidence/P12_DS_EVIDENCE_TEMPLATE.md`
- `docs/handoffs/P12_FX_01_HANDOFF.md`

No Java, `src/main/resources`, Gradle, networking, schema, Data Component,
client/UI, public API, or ignored `run/` file was changed.

## Fixture contents

- The server policy is survival, `online-mode=true`, whitelist enabled,
  command blocks disabled, and spawn protection zero.
- Five named acquisition configuration profiles provide baseline plus each
  DS-08 independent disabled flag.
- The KubeJS startup script is the declarative canonical example copied into
  the required fixture location.
- Datapacks name the required DS-02 valid/malformed/removal sequence, DS-05
  valid/cross-reference/precedence sequence, DS-07 one-marked-offer removal,
  and DS-09 visibly distinct bounded stone-profile replacement.
- The README supplies immutable preflight, new-server materialization,
  clean-world/checkpoint, hash capture, sanitization, and evidence rules.
- The evidence template enforces the P12 result/classification/artifact format
  without recording private address, port, account, world seed, token, raw
  log, or machine path.

## Local verification performed

```powershell
$required = @(...required P12 fixture paths...)
$required | Where-Object { -not (Test-Path (Join-Path 'docs/fixtures/p12' $_)) }
Get-ChildItem docs/fixtures/p12 -Recurse -Filter *.json |
  ForEach-Object { Get-Content -Raw $_.FullName | ConvertFrom-Json }
Get-ChildItem docs/fixtures/p12 -Recurse -File |
  Get-FileHash -Algorithm SHA256
git diff --check
```

Result: every required bundle path is present; all fixture JSON files parse;
SHA-256 values were emitted for the committed bundle files; `git diff --check`
found no whitespace error.

## External blocker and requested next action

The following required proofs cannot be produced by repository-only fixture
assembly and are explicitly reserved to Sol/operator:

1. choose an exact compatible Minecraft 1.21.1 KubeJS artifact set, install
   it, and record each coordinate/version/SHA-256;
2. obtain the exact NeoForge 21.1.234 installer, record its artifact/name and
   SHA-256, then materialize a new standalone server outside the repository;
3. run the immutable clean-build preflight from an empty Git status and record
   the source commit and MathMod JAR SHA-256 in a copied manifest;
4. prove the dedicated server reaches ready state with the frozen mod list;
5. prove Client A and Client B are distinct authenticated accounts joining
   under the committed server policy;
6. install each datapack/profile into fresh worlds and record its authoritative
   valid/rejected candidate observation and sanitized artifacts.

This is an `ENVIRONMENT_FAILURE`, not an implementation failure. The missing
artifact is the externally materialized standalone fixture and actor proof;
the owners who can unblock it are Sol and the operator. The next safe action is
to copy the bundle to an empty standalone server and fill the manifest with
observed values before starting any DS row.

## Limitations

- A fixture bundle, parsed JSON, or historical local `run/` directory does not
  prove a clean server, a KubeJS stack, a real reload, or authenticated actors.
- The proof launch is fixture validation only; it must not be recorded as a
  DS-01–DS-09 pass.
- `P12-DS` and `P12-M` remain blocked. No Foundation Beta surface is promoted
  beyond its accepted experimental classification.

## Next owner

Sol + operator, for external artifact/account/launch proof and a decision on
whether the resulting complete fixture evidence releases `P12-DS`.
