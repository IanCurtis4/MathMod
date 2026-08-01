// P12 fixture copy of docs/examples/kubejs/mathmod_manuscripts.js.
// It is declarative and must be copied unchanged into kubejs/startup_scripts.
const MathMod = Java.loadClass('com.mathmod.kubejs.MathModKubeJS')

MathMod.tradition('example:field_notation')
  .schemaVersion(1)
  .nameKey('tradition.example.field_notation.name')
  .summaryKey('tradition.example.field_notation.summary')
  .icon('minecraft:book')
  .register()

MathMod.manuscript('example:constant_fields')
  .schemaVersion(1)
  .tradition('example:field_notation')
  .titleKey('manuscript.example.constant_fields.title')
  .page('A constant field is a value that remains stable while the anchor is active.')
  .page('Start with a scalar, then connect it to a field rune.')
  .icon('minecraft:paper')
  .rarity('COMMON')
  .patchouliEntry('mathmod:programming/kubejs')
  .register()

MathMod.manuscriptAlias(1, 'example:old_constant_fields', 'example:constant_fields')
