# P12 ATM10 Candidate and Startup Warning Review

**Date:** 2026-08-03  
**Owner:** Sol + operator  
**Status:** `MANUAL_TEST_READY`  
**Artifact class:** test candidate, not an immutable P12-DS artifact

## 1. Candidate identity

The accepted P12-TM-05F source was rebuilt with:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat clean build --no-build-cache
```

Result:

```text
BUILD SUCCESSFUL
JUnit: 533/533, 0 failures, 0 errors, 0 skipped
JAR: C:\mathmod-build\MathMod\libs\mathmod-0.2.0-beta.1.jar
size: 2,400,824 bytes
SHA-256: 3ADA2D5D79640EC3304C58E4B95DC2CEAAA818B3F5F9570C4A8533E42630FD8D
```

This is not yet the immutable P12-DS artifact. The repository still had 26
porcelain entries after the build, while the fixture contract requires an empty
working tree and a source commit. Commit, clean verification and a fresh build
remain mandatory even if that future build happens to produce the same bytes.

## 2. Reversible ATM10 installation

The audited restore script restored the complete ATM10 client profile:

```text
restored original mods: 479
preserved minimal P12 mods: 3
active MathMod SHA-256: 3ADA2D5D79640EC3304C58E4B95DC2CEAAA818B3F5F9570C4A8533E42630FD8D
```

The previous full-profile MathMod JAR was moved intact to:

```text
C:\Users\João Pedro\curseforge\minecraft\Instances\All the Mods 10 - ATM10\mathmod-backups\mathmod-0.2.0-beta.1-pre-tm05f-94B30A1047E7.jar
```

Its SHA-256 is:

```text
94B30A1047E70A68966ACFF9AEEFCF704DAE1585B0021A32FA75E0BD5AAE0194
```

The P12 minimal client profile remains preserved under
`C:\mathmod-p12-client-backup-ce64b9b\p12-minimal-mods` and
`p12-minimal-kubejs`. No original ATM10 mod was deleted.

## 3. AllTheTweaks warning finding

The startup warning is not a MathMod incompatibility declaration. The ATM10
instance itself contains this NeoForge loader configuration:

```toml
[dependencyOverrides]
    allthetweaks = ["+bcc"]
```

The adjacent pack comments define `+bcc` as forcing the target mod to load
after `bcc`. NeoForge therefore emits:

```text
Found dependency overrides
Dependency overrides for mod 'allthetweaks': adding explicit AFTER ordering against 'bcc'
```

The message appears before MathMod discovery and names neither `mathmod` nor a
MathMod dependency. The restored profile contains All The Tweaks 2.9.4 and
Better Compatibility Checker 21.1.8, both enabled. MathMod's
`neoforge.mods.toml` declares only required Minecraft and NeoForge dependencies
and contains no AllTheTweaks, BCC, incompatibility or discouraged relation.

Conclusion: the visible `WARN` reports an intentional ATM10 load-order override
between two pack mods. It is not evidence that AllTheTweaks rejects MathMod.
The warning should remain unchanged during the manual run; removing the pack's
override is outside MathMod ownership and could break the pack's intended order.

## 4. Manual test boundary

Launch the restored `All the Mods 10 - ATM10` CurseForge profile and record:

1. whether the dependency-override warning text remains exactly the
   AllTheTweaks/BCC ordering message;
2. whether the main menu completes without a MathMod incompatibility screen;
3. whether an ATM10 singleplayer world opens;
4. whether the Rune Programmer opens and Factored Leap shows its complete
   three-line statement without overlap;
5. whether Hop preserves the legacy one-line header spacing;
6. whether Laboratory accepts repeated explicit `Self` without closing the
   client;
7. any new warning, exception or crash mentioning `mathmod`, with the exact
   action immediately preceding it.

After the client closes, preserve `logs/latest.log`, `logs/debug.log` and any
crash report outside Git for Sol inspection. This manual compatibility run does
not substitute for the clean standalone DS-01 rerun and does not pass any
P12-DS row.

## 5. Next immutable boundary

After the manual ATM10 result is reviewed:

1. commit all accepted repository changes intentionally;
2. prove `git status --porcelain` is empty;
3. run a fresh clean build;
4. record the new commit, JAR SHA-256 and batch id;
5. install that exact JAR in the standalone client/server fixture;
6. execute DS-01 from a clean checkpoint.
