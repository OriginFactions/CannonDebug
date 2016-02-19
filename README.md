# CannonDebug

*Best cannon debugger out there!*

## Supports

* WorldEdit 5.x -> 6.x
* CraftBukkit & Spigot 1.7.9 -> 1.8.8

## Features

* Per user cannon logging
* Select which dispensers and sand blocks to log
  * Click the blocks you wish to log with "/c select"
  * WorldEdit selection support with "/c region"
  * Selections can be viewed in game with "/c view"
* Provides a unique ID per dispenser and sand block tracked
* Logs locations and velocities for TnT and sand every tick
* Caches and indexes all cannoning debug information
* Interactive and helpful listing GUI
* Sort and filter information by
  * Server tick
  * Entities' selection ID

## Download

[Obtain the latest compiled version of CannonDebug here](https://github.com/OriginFactions/CannonDebug/raw/master/target/CannonDebug.jar)

## Installation

1. Download file to your computer
2. Drag and drop CannonDebug.jar to plugins folder in server
3. Restart your server

## Permissions

| **Permission**                      | **Description**                                           | **Default**    |
| ----------------------------------- | --------------------------------------------------------- | -------------- |
| cannondebug.clear                   | Clear either history or selections                        | everyone       |
| cannondebug.help                    | View the default help pages                               | everyone       |
| cannondebug.history                 | Core node to access the history command                   | everyone       |
| cannondebug.history.all             | View latest history for all profiled entities             | everyone       |
| cannondebug.history.help            | View the history help pages                               | everyone       |
| cannondebug.history.id              | View latest history for specific entity                   | everyone       |
| cannondebug.history.tick            | View all tracker history in a specific tick               | everyone       |
| cannondebug.maxarea.#               | Area of region allowed in blocks to select with WorldEdit | everyone (500) |
| cannondebug.maxselections.#         | Maximum amount of selected blocks to track at one time    | everyone (25)  |
| cannondebug.maxarea.unlimited       | Unlimited selection area with WorldEdit                   | operator       |
| cannondebug.maxselections.unlimited | Unlimited selected blocks at any one time                 | operator       |
| cannondebug.page                    | Access to use the pager system                            | everyone       |
| cannondebug.preview                 | Preview all selected blocks for profiling                 | everyone       |
| cannondebug.region                  | Use the region selector tool utilizing WorldEdit          | everyone       |
| cannondebug.select                  | Use the hand selector tool                                | everyone       |

## Contributing

* 4-space indentation
* UNIX line endings
* Braces on the same line

## License

CannonDebug is licensed under the [MIT license](https://tldrlegal.com/license/mit-license).
