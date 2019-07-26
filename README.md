# HasteIt / DiscordBeam

<img src="./img/hasteit.svg" alt="HasteIt" height="200"/>
<img src="./img/discordbeam.svg" alt="DiscordBeam" height="200"/>

Both are plugins for [IDEA](https://www.jetbrains.com/idea/) -based IDEs.

Those two plugins help you to share files or code with a platform.

* HasteIt - [Hastebin](https://hastebin.com/)
* DiscordBeam - [Discord](https://discordapp.com/)

That's the reason why both plugins share some code which is located in the [ShareBase](./ShareBase) folder.

## Need help?

Just create an issue and ask your question. I'll try to answer as fast as possible.

## Contributing

To work one of the plugins, you'll need to clone the whole repository.

As a build tool, we're using Gradle with the 
[gradle-intellij-plugin](https://github.com/JetBrains/gradle-intellij-plugin) 
which helps to easily setup your development environment.

If you want to test your changes, just go into the plugin folder (DiscordBeam or HasteIt) and run 
```bash
../gradlew runIde
```

Here are some interesting links for plugin development:

* https://github.com/JetBrains/gradle-intellij-plugin
* https://www.jetbrains.org/intellij/sdk/docs/welcome.html
* https://jetbrains.design/intellij/principles/icons/