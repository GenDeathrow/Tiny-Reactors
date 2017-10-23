# [Tiny Reactors](https:/www.minecraft.curseforge.com/projects/tiny-reactors)
Build a tiny reactor... Or a massive one!

< Image Coming Soon >

* [Tracker](#tracker)
* [About](#about)
* [Guide](#guide)
* [Roadmap](#roadmap)


## Tracker

_This space being empty is a good sign ( it means there are no bug checks/fixes required at this time )._

* Controller UI updates when block invalidates but not when validates.

* Loadbalancing for Reactor Controller


## About

Tiny Reactors is all about bringing power generation to Minecraft through the construction of fully scalable, easily expandable multiblock Reactor structures.  Place your favourite Vanilla ore ( or block variant ) inside the Reactor structure to generate a limited amount of RF/tick infinitely.

All Reactants are fully configurable at runtime using the Mod Options.  The minimum reactor size is a 3 x 3 x 3 structure, providing 1 Reactant slot; there is no configured maximum reactor size, so expand to your needs!

Originally inspired by [Kashdeya](https://www.twitter.com/Kashdeya), with content created and authored by [ArclightTW](https://www.twitter.com/ArclightTW), Tiny Reactors is [SNIs](http://www.skillsnotincluded.com) take on the multiblock reactor mod style!


## Guide

A Tiny Reactor is built primarily out of Reactor Casing blocks.  The bottom and top layers of the Reactor structure must be entirely built from Reactor Casing.  The 4 corner pillars must also be built from Reactor Casing; however, blocks in these pillars can be replaced by both the Reactor Controller and Reactor Energy Port blocks.  The remaining 4 faces can be filled in with a combination of Reactor Casing, Glass, Controller and Energy Port blocks, ensuring the structure only contains 1x Reactor Controller and 1x Energy Port.

To power your Tiny Reactor, place your favourite ores within the Reactor structure; with default settings applied, rarer ores produce more power ( e.g. Coal ore produces 1 RF/tick, whereas Emerald Ore produces 32 RF/tick ).  Crafting these ores into their associated blocks produces significantly more power in a smaller space ( e.g. a Coal Block produces 8 RF/tick, versus the 1 RF/tick for a Coal Ore block ). 

You can modify the rates of all the default ores or even remove them entirely, as well as adding block's from any other mod ( e.g. minecraft:glowstone:32 or tp:netherstar_block:1024 ).  These settings can even be modified at runtime to allow for full modpack building support, using the in game Mod Options.


## Roadmap

### Indefinite

* Config option for Reactant decay ( for those wanting a system to maintain ).
* _^ Reactor Waste Port to accumulate product decay._
* _^ Radioactive Waste Ingot._

* Tiered Energy Ports.

* UI for Energy Port to show current energy level.

* Reactor Controller Tier 2 allows redstone interaction ( Ignored, Inactive, Active ).
* _^ Would need to implement Comparator methods so can be deactivated by redstone if nearing full._
* _^ Click efficiency bar to increase/decrease power output._

### Version 0.3.1

__CHANGES__

_Dependencies:_
* Tiny Reactors now requires RedstoneFlux as a dependency (this is - hopefully - temporary).

_Front-End:_
* The Reactor Controller is now controlled from a UI.
* The Reactor can be enabled and disabled after a valid structure built.
* Reactor Energy Ports and Capacitors now produce compatible RF.
* Config option for Reactor structures to go into meltdown if Reactor Controller energy buffer overfills.
* Reimplemented Capacitors.
* Energy Ports can modify the amount of power they can input/output at any time.

_Back-End:_
* The Reactor Controller now has a _valid_ and _active_ flag to allow valid structures to be disabled.
* The Reactor Controllor now handles power generation directly and not the Energy Ports.

__BUG FIXES__

* Fixed recipes for all Blocks.

### Version 0.3.0 changed a number of features with Tiny Reactors;

__WARNING: Due to the changes implemented in Tiny Reactors 0.3.0, it is no longer compatible with earlier versions.__
__Loading a world containing pre-0.3.0 Reactor components will invalidate the existing blocks; I recommend either removing all Reactors and associated blocks ( Casing, Glass, etc. ) prior to installing the new version or removing all Tiny Reactor blocks post-update and replacing them in the world.__

__Front-End:__
* Removed ability to have more than 1 Energy Port in a Reactor structure.
* _^ This is due to be reimplemented but in a better way._
* The config file now supports adding/removing ANY block from the Reactant registry.
* The Reactor Controller must be activated ( clicked ) to validate a Reactor structure.
* The Reactor Controller has a UI that displays key information, such as Efficiency and Output.
* Removed Capacitors.
* _^ This is due to be reimplemented but in a better way._

__Back-End:__
* Reactor Controllers no longer check validity once a second.
* _^ All Reactor blocks are aware of their associated Controller._
* _^ The Controller is informed when a Reactor block is broken and invalidated._