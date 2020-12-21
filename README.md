# HasteIt / DiscordBeam

<p>
    <a href="./HasteIt"><img src="./img/hasteit.svg" alt="HasteIt" height="200"/></a>
    <a href="./DiscordBeam"><img src="./img/discordbeam.svg" alt="DiscordBeam" height="200"/></a>
</p>

Both are plugins for [IDEA](https://www.jetbrains.com/idea/) -based IDEs.

Those two plugins help you to share files and code with a specific platform.

* HasteIt - [Hastebin](https://hastebin.com/)
* DiscordBeam - [Discord](https://discord.com/)

Both plugins use the common code base called [ShareBase](./ShareBase).

## Need help?

All input (questions, feedback, a new idea, etc.) is useful. Please open an issue, and I try to respond as fast as I
can.

## Contributing

To work one of the plugins, you'll need to clone the whole repository.

As a build tool, we're using Gradle in conjunction with the
[gradle-intellij-plugin](https://github.com/JetBrains/gradle-intellij-plugin)
which helps to ease the process of setting up your development environment.

If you want to test your changes, go to the plugin folder (DiscordBeam or HasteIt) and run

```bash
../gradlew runIde
```

Here are some interesting links for plugin development:

* https://github.com/JetBrains/gradle-intellij-plugin
* https://jetbrains.org/intellij/sdk/docs/intro/welcome.html
* https://jetbrains.design/intellij/principles/icons/

Once you are happy with your changes, feel free to open a pull request. All contributions no matter the size are
welcome.

## Git Tags

You may notice, that the tag names in this repository look a little strange. That's correct, let me explain why.

The version numbers of the both plugins aren't synchronous. Tags starting with letter `H` mark HasteIt releases, and
those starting with `D` DiscordBeam releases.

There are some tags for older HasteIt version which start with the letter `v`.

Currently, we're in the process of switching to [semantic versioning](https://semver.org/).
