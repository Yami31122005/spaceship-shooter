# Spaceship Shooter (Java OOP project)

A console space shooter built to practise object-oriented design in Java. The player's ship
defends against waves of enemies that come from above. Each wave, enemy stats and wave
composition are read from CSV files, so the game is data-driven.

## How to run

Requirements: JDK 17+ and Maven.

```bash
mvn package                       # compiles, runs the tests, builds target/spaceship-shooter-1.0.0.jar
java -jar target/spaceship-shooter-1.0.0.jar
```

The console run is an autopilot demo: the ship steers toward the nearest enemy and fires
continuously. The fixed random seed makes every run identical.

## How to run the tests

```bash
mvn test
```

44 JUnit 5 tests cover normal cases, edge cases and invalid input for the business logic.

## Main features

- Three enemy types with different behaviour: fast straight-line scouts, sine-wave fighters, slow swaying tanks.
- Bullets with a fire cooldown; a bullet is consumed by the first enemy it hits.
- Enemies that ram the ship or slip past it cost one health point each.
- Power-ups dropped by killed enemies: a repair kit (+1 health) and rapid fire (double fire rate for 8 s).
- Score with a streak multiplier (up to x5). Taking damage resets the streak. Clearing a wave gives a bonus.
- Win when every wave is cleared, lose when the ship runs out of health.

## Main classes

| Package | Class | Role |
|---|---|---|
| `model` | `Entity` | Abstract base for anything with a position and size; provides collision test. |
| `model` | `Ship` | Abstract; health rules (damage, heal, destroyed). Base of player and enemies. |
| `model` | `PlayerShip` | Moves horizontally, fires with cooldown, can be boosted. |
| `model` | `Enemy` | Abstract enemy; `update()` is final and calls the subclass `move()`. |
| `model` | `ScoutEnemy`, `FighterEnemy`, `TankEnemy` | Concrete enemies with their own movement. |
| `model` | `Bullet` | Projectile moving upward. |
| `model` | `PowerUp` | Abstract falling collectible. Subclasses: `RepairKit`, `RapidFire`. |
| `model` | `EnemyDefinition`, `Wave`, `WaveEntry` | Immutable records describing data loaded from files. |
| `service` | `Game` | Coordinates one game: update loop, spawning, win/loss. |
| `service` | `CollisionService` | Decides hits, rams and pick-ups. |
| `service` | `ScoreService` | Scoring rules and streak multiplier. |
| `service` | `WaveManager` | Hands out waves in order; validates data against enemy definitions. |
| `service` | `EnemyFactory` | Creates the right `Enemy` subclass for a definition. |
| `service` | `GameDataSource` / `CsvGameDataSource` | Loads enemies and waves from CSV. |
| `utils` | `CsvParser` | Reads CSV rows and validates column counts. |
| `Application` | | Entry point: load data, build game, run it. |

## Where inheritance is used

`Entity` → `Ship` → `PlayerShip` and `Enemy` → `ScoutEnemy` / `FighterEnemy` / `TankEnemy`.
`Entity` → `Bullet` and `Entity` → `PowerUp` → `RepairKit` / `RapidFire`.
Ships share health rules; enemies share spawn/stats handling; power-ups share the falling movement.

## Where polymorphism is used

- `Game.update()` calls `update(dt)` on every entity through `Entity`; each subclass moves differently.
- `Enemy.move()` is implemented differently by each enemy type; `Enemy.update()` is the template method calling it.
- `EnemyFactory.create()` returns an `Enemy`; callers never know the concrete class.
- `PowerUp.applyTo()` runs the effect of whichever power-up was picked up.
- `EnemyFactoryTest` checks that one loop over `List<Enemy>` moves each type correctly.

## Where the abstract class and interfaces are used

- **Abstract classes** (`Entity`, `Ship`, `Enemy`, `PowerUp`): they hold shared state and rules
  (position, health, speed, falling) and force subclasses to define behaviour (`update`, `move`).
  Abstract classes fit because these types share fields and implementation, not only a contract.
- **Interface `Healable`**: anything that can recover health. Power-ups depend on this, not on `PlayerShip`.
- **Interface `Shooter`**: anything that can emit a bullet. `PlayerShip` implements it.
- **Interface `Collectible`**: an object whose effect applies to the ship. Implemented by `PowerUp`.
- **Interface `GameDataSource`**: the game depends on this, so CSV can be swapped for JSON or test data.

## Custom exceptions

- `InvalidGameDataException` (unchecked): data breaks a game rule (unknown enemy kind, zero health,
  a wave using an undefined enemy, malformed CSV row, duplicate definition).
- `DataLoadException` (checked): a data file is missing or cannot be read. Checked on purpose,
  because the caller must decide what to do when the game cannot start.

Standard exceptions are used for programming errors: `IllegalArgumentException` for bad arguments
(negative damage, direction out of range) and `NullPointerException` for null references.

## How the application loads its data

At start-up `Application` creates a `CsvGameDataSource` which reads two files from the classpath
(`src/main/resources`):

- `enemies.csv`: `kind,health,speed,points`, one line per enemy type.
- `waves.csv`: `wave,kind,count`, one line per group of enemies; lines sharing a wave number form one wave.

Nothing is hard-coded: changing the CSV files changes the game. `WaveManager` checks the data
once (every wave enemy must be defined, no duplicate definitions), so the game loop can trust it.

## Technical decisions

1. **Records for immutable data** (`EnemyDefinition`, `WaveEntry`, `Wave`): these are pure values
   loaded from a file. Records give equality, immutability and validation in a compact constructor.
2. **Template method in `Enemy`**: `update()` is final and always calls `move()`. This keeps the
   shared behaviour in one place while each enemy supplies only its movement.
3. **Separate services instead of one `Game` god class**: collisions, scoring, waves and data
   loading each have their own class. `Game` only coordinates them, which makes each rule testable alone.
4. **Checked vs unchecked exceptions**: missing files are an expected failure at start-up (checked);
   invalid data is a bug in the content (unchecked, reported with a clear message).
5. **Fixed random seed and autopilot** in `Application`: the console run is repeatable, which makes
   the demo and debugging easier, without needing a keyboard loop.

## SOLID principles

- **Single Responsibility**: each class has one job (`CollisionService` only resolves hits,
  `ScoreService` only scores, `CsvParser` only parses).
- **Open/Closed**: a new enemy type means a new `Enemy` subclass, a new `EnemyKind` value and a
  `case` in `EnemyFactory`; the game loop does not change. A new power-up is a new `PowerUp` subclass.
- **Liskov Substitution**: every `Enemy` can be used wherever an `Enemy` or `Entity` is expected; the
  game loop never checks the concrete type.
- **Interface Segregation**: `Healable`, `Shooter` and `Collectible` are small and separate, so a class
  implements only the capabilities it has.
- **Dependency Inversion**: `Game` and `WaveManager` depend on the `GameDataSource` abstraction and on
  the `Random` they receive, not on CSV files or a global random source.

## Project layout

```
src/main/java/com/example/spaceshooter/
├── Application.java
├── exception/   DataLoadException, InvalidGameDataException
├── model/       entities, enemies, power-ups, value records
├── service/     Game, services, factory, data sources
└── utils/       CsvParser
src/main/resources/   enemies.csv, waves.csv
src/test/java/...     JUnit 5 tests mirroring the packages above
```
