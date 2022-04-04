# The Desolate by Christopher Ravosa
This repository contains the prototype version of _The Desolate_, a mobile game about isolation and survival. The game follows a lonely survivor as they struggle to survive the dangers of everyday life in an arid wasteland. Players guide the survivor as they forage for supplies. Along the way, the survivor will cope with starvation, dehydration, and the creatures who roam the desert. With luck, they may come across a friend to support them in their efforts. A player's final score is recorded as the number of days they kept the survivor alive multiplied by the number of parts of the map they visited, so exploration helps a player's final score.

## Roadmap
This roadmap details the features which were originally planned for _The Desolate_. Not all of them have made it in yet, but I plan on implementing them all soon!

- [x] Procedurally generated world
    - [x] Place and connect rooms
    - [x] Add room descriptions
- [ ] Map view overlay
- [ ] User option for small (10x10), medium (20x20), or large (30x30) world size
- [x] Text-based traversal of world
- [x] Vitality system
- [ ] Resources spawn every night
- [x] Day counter
- [ ] Day/night cycle
- [ ] Wandering enemies
    - [ ] Enemy spawns
    - [ ] Shake phone to fight enemies
    - [ ] Threads for attacks
    - [ ] If player makes noise enemies appear more frequently
- [ ] Top scores (# days survived)
    - [ ] Score persistency
- [ ] Real-world temperature changes how sprites appear

## Procedural Generation
The game world is procedurally generated with every playthrough of the game using an algorithm developed by Christopher Ravosa. For more information on Christopher's generation algorithm, visit the [Recursive Dungeon Generation](https://github.com/crav12345/Recursive-Dungeon-Generation) algorithm repository.

The algorithm developed for the game forces the world onto a 20x20 grid for the time being. However, it works on grids of uneven dimensions and various sizes.
    
## Acknowledgements
This list contains links to resources which were helpful in the development of this project:

* [Shepardskin](https://twitter.com/Shepardskin)
* [MrMeek](https://pixeljoint.com/p/67652.htm)
