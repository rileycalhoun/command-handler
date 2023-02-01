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
