# P7 KubeJS Manuscript Display API Contract

Status: Sol architecture, Terra public-builder/reload integration, and Luna
documentation fixtures completed on 2026-07-22. A real dedicated-server
reload remains follow-up work.

## Purpose

P7 lets a modpack declare manuscript display records and mathematical
traditions from KubeJS startup scripts. It does not let JavaScript read a
manuscript, grant knowledge, execute a graph, alter loot, or mutate a player.

Discovery grants remain in the existing `MathMod.discovery(...)` API. A
manuscript display record and a discovery may share an id, but neither creates
the other implicitly.

## Exact Public Surface

The implemented Terra slice exposes these entry points on `MathModKubeJS`:

```java
static KubeJsTraditionSpec tradition(String id)
static KubeJsManuscriptSpec manuscript(String id)
static void manuscriptAlias(int schemaVersion, String from, String to)
```

Tradition builder:

```text
schemaVersion(int)       required; currently 1
nameKey(String)          required
summaryKey(String)       required
icon(String)             required exact item id
register()               terminal, one call
```

Manuscript builder:

```text
schemaVersion(int)       required; currently 1
tradition(String)        required namespaced tradition id
titleKey(String)         required
page(String)             required 1..8 times, ordered
icon(String)             required exact item id
rarity(String)           required: common|uncommon|rare|epic
patchouliEntry(String)   optional navigation target
theorem(String)          optional stable built-in theorem id
register()               terminal, one call
```

Every record id must be namespaced. Builders do not infer the script namespace,
accept inline translated prose, or use the caller's filename as authority.
Calling `register()` twice or registering the same kind/id twice is an error.

Example:

```js
const MathMod = Java.loadClass('com.mathmod.kubejs.MathModKubeJS')

MathMod.tradition('pack:ridge_surveyors')
  .schemaVersion(1)
  .nameKey('tradition.pack.ridge_surveyors.name')
  .summaryKey('tradition.pack.ridge_surveyors.summary')
  .icon('minecraft:spyglass')
  .register()

MathMod.manuscript('pack:ridge_measurement')
  .schemaVersion(1)
  .tradition('pack:ridge_surveyors')
  .titleKey('manuscript.pack.ridge_measurement.title')
  .page('manuscript.pack.ridge_measurement.page.1')
  .page('manuscript.pack.ridge_measurement.page.2')
  .icon('minecraft:paper')
  .rarity('uncommon')
  .patchouliEntry('pack:lore/ridge_measurement')
  .theorem('mathmod:right_angle')
  .register()

MathMod.manuscriptAlias(1, 'pack:old_ridge_note', 'pack:ridge_measurement')
```

## Lifecycle

The API is startup-only. Calls stage locally validated definitions before the
first manuscript data reload. The first reload freezes one immutable KubeJS
generation with source coordinate:

```text
layer = KUBEJS
priority = 0
sourceName = kubejs:startup_scripts
```

Later `/reload` operations reuse that generation and never call JavaScript.
Changing a startup declaration requires a server restart. Calls after freeze
fail explicitly; they never mutate the active snapshot in place.

`KubeJsManuscriptDeclarationStore`, the public builders, and the reload listener
now consume this staging/freeze boundary.

## Precedence

The frozen order is:

```text
built-in < KubeJS startup generation < active data packs
```

The reload adapter creates one candidate builder and adds all three layers
before one atomic publication. It must distinguish MathMod's built-in resource
pack from external data packs; treating every selected resource as
`DATA_PACK` would incorrectly make built-ins override KubeJS.

Within KubeJS there is one source coordinate because `Java.loadClass` does not
reliably provide the calling script path. Therefore duplicate KubeJS ids are
rejected, including identical duplicates. Last-call-wins is forbidden.

For data packs, Minecraft pack priority decides the winner. The adapter should
use resource stacks when it needs diagnostics for shadowed candidates; file
enumeration and filesystem timestamps never decide precedence.

Aliases use the same layer order. An alias cannot shadow a current id, target a
missing record, form a cycle, or exceed the existing 16-hop build limit.

## Validation Phases

Builder registration validates only context-free facts: schema, ids, key/page
limits, rarity, and required fields. Registry-dependent validation occurs on
the game thread during candidate assembly:

- icons must resolve to registered items;
- traditions must resolve after precedence;
- theorem ids must resolve through the stable built-in theorem index;
- aliases resolve after all three layers are present;
- global counts use the existing 256/1,024/2,048 caps.

A bad local KubeJS registration throws at startup with kind and id. A bad
cross-reference becomes a source-aware manuscript diagnostic and omits only the
affected candidate unless a global cap makes the generation unpublishable.
The previous active snapshot survives any unpublishable generation.

## Security And Ownership

The builders accept strings, integers, and declarative records only. They never
accept callbacks, suppliers, commands, predicates, executor keys, grant lists,
item-consumption behavior, loot selectors, player references, world references,
or arbitrary NBT/components.

KubeJS display data cannot:

- grant or revoke knowledge;
- create a discovery automatically;
- make a conjecture castable;
- inscribe or edit a talisman;
- open an unvalidated client target;
- run code when read, synchronized, migrated, or displayed.

The server remains authoritative for definitions, aliases, item ids, and
navigation references. Clients receive only the bounded reader projection
already defined by P6.

## Terra Acceptance Matrix

The Terra slice covers:

- exact builder signatures and one startup-script example;
- valid tradition, manuscript, optional references, and alias registration;
- missing schema, duplicate KubeJS id, post-freeze registration, and pure
  built-in/KubeJS/datapack precedence tests;
- registry validation for KubeJS icons plus existing snapshot validation for
  traditions, theorem ids, aliases, and global caps;
- `/reload` consuming the frozen KubeJS generation without a JavaScript call.

The remaining Terra acceptance work is a live dedicated-server reload with
KubeJS present and absent, plus fixtures for every rejected cross-reference.

Luna documentation and fixtures are complete: the canonical sample is
`docs/examples/kubejs/mathmod_manuscripts.js`, and the bilingual Patchouli
entry `programming/kubejs` teaches the same contract in-game. P7 does not add
profession, trades, loot mutation, or world generation.
