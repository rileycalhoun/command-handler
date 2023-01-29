# Riley's Command Handler
A generic command handler with implementations for Spigot, the CLI and more on the way.

## Gradle
Build with: ``./gradlew clean build publishToMavenLocal``

```gradle
repositories {
    mavenLocal()
}

dependencies {
    // Core is required
    implementation 'me.rileycalhoun.commandhandler:core:1.2.1'
    // Any other implementation you need
    implementation 'me.rileycalhoun.commandhandler:<implementation>:1.2.1'
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
    
    @Command(name = "command", description = "An example command.", aliases = { "c" })
    public void execute(CommandContext, String[] args) {
        context.getSubject.reply("This is a reply!");
    }
    
}
```

## Sub-Command Usage (CLI)
```java
@Command(name = "subcommands", description = "An example command with sub commands.", aliases = { "ex" })
public class SubCommands {

    public static void main(String[] args) {
        ConsoleCommandHandler handler = ConsoleCommandHandler.create();
        handler.registerCommands(new SubCommands());
        handler.requestInput();
    }
    
    @SubCommand(name = "first", description = "The first sub command!", aliases = {"1"})
    public void first(CommandContext context, String[] args) {
        context.getSubject().reply("This is the first reply!");
    }

    @SubCommand(name = "second", description = "The second sub command!", aliases = {"2"})
    public void second(CommandContext context, String[] args) {
        context.getSubject().reply("This is the second reply!");
    }
    
}
```
