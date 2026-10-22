# Riley's Command Handler
A generic command handler with implementations for Spigot, the CLI and more on the way.

## Gradle
```gradle
repositories {
    mavenCentral()
    maven { "https://maven.rileycalhoun.me" }
}

dependencies {
    /* common is always required */ 
    implementation 'me.rileycalhoun.commandhandler:common:{VERSION}'
    
    /* Add any other implementation(s) you may need (spigot, bungee, cli...) */
    implementation 'me.rileycalhoun.commandhandler:<implementation>:{VERSION}'
}
```

## Basic Command Usage (CLI)
```java
public class Commands {

    public static void main(String[] args) {
        ConsoleCommandHandler handler = ConsoleCommandHandler.create();
        handler.registerCommands(new Commands());
        handler.requestInput();
    }
    
    @Command(name = "command", aliases = { "c" })
    public String execute() {
        return "You can return a string to send a message to the executor!";
    }
    
}
```

## Sub-Command Usage (CLI)
```java
@Command(name = "subcommands", aliases = { "ex" })
public class SubCommands {

    public static void main(String[] args) {
        ConsoleCommandHandler handler = ConsoleCommandHandler.create();
        handler.registerCommands(new SubCommands());
        handler.requestInput();
    }
    
    @SubCommand(name = "first", aliases = {"1"})
    public void first(CommandSubject subject) {
        subject.reply("Or you can reply with a method!");
    }

    @SubCommand(name = "second", aliases = {"2"})
    public void second(CommandSubject subject, String message) {
        context.getSubject().reply("Or you can get arguments: " + message);
    }
    
}
```

## Architecture

Command Handler is a multi-module Gradle project. The platform-independent
core lives in **common**; each runtime gets a thin adapter module on top of it.

| Module | Contents |
| --- | --- |
| `common` | The framework itself: annotation definitions (`@Command`, `@Subcommand`, `@Flag`, `@Cooldown`, `@RunAsync`, …), command registration and dispatch, argument parsing (`ArgumentStack`), type resolvers and parameter validators, conditions, help generation, and the exception types platforms report through. |
| `spigot` | Spigot adapter: registers commands with a Bukkit plugin, resolves `Player` arguments (including `me`), and provides tab-completion providers. |
| `bungee` | BungeeCord adapter: the same handler model for proxy plugins, including player selectors. |
| `velocity` | Velocity adapter for modern proxy servers. |
| `cli` | Console adapter: runs the same annotated commands against stdin/stdout, which is how the examples above work without a Minecraft server. |

### How dispatch works

1. `registerCommands(instance)` scans the object's methods for `@Command` /
   `@Subcommand` annotations (via the Reflections library) and builds a
   `HandledCommand` per method.
2. When a command runs, the platform adapter packages the raw input into a
   `LinkedArgumentStack` and a platform `CommandContext`.
3. The dispatcher matches parameters against the stack: each parameter's
   resolver converts the next argument(s) to the declared type, validators
   (for example `@Range`) check the converted value, and conditions
   (permissions, cooldowns) gate execution.
4. The method is invoked reflectively. Its return value — or an explicit
   `subject.reply(...)` — is routed back through the platform's response
   handling. Failures surface as typed exceptions (`MissingParameterException`,
   `ResolverFailedException`, `CooldownException`, …) and are rendered to the
   sender by the platform's `DefaultExceptionHandler`.

The `common` module has no platform dependencies, so the parsing and utility
core is unit-tested without a server (`./gradlew :common:test`).

## License

MIT — see [LICENSE](LICENSE).
