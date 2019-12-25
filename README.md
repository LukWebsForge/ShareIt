# HasteIt / DiscordBeam

<p>
    <a href="./HasteIt"><img src="./img/hasteit.svg" alt="HasteIt" height="200"/></a>
    <a href="./DiscordBeam"><img src="./img/discordbeam.svg" alt="DiscordBeam" height="200"/></a>
</p>

Both are plugins for [IDEA](https://www.jetbrains.com/idea/) -based IDEs.

Those two plugins help you to share files or code with a platform.

* HasteIt - [Hastebin](https://hastebin.com/)
* DiscordBeam - [Discord](https://discordapp.com/)

That's the reason why both plugins share some code which is located in the [ShareBase](./ShareBase) folder.

## DiscordBeam and 2019.3 versions

_You recently upgraded your IDE to the 2019.3 version and are using DiscordBeam?_

To continue using DiscordBeam please uninstall the plugin and reinstall it. Everything should work fine now.

_Why?_

The API of the IDEA Github plugin has changed between the versions (2019.2 and 2019.3). I still wanted to bring new
features to users of older IDEs versions, so there are now to separate versions of the plugin.  
It may be helpful for understanding my approach to take a look at the 
[plugin page](https://plugins.jetbrains.com/plugin/12804-discordbeam/versions).
Compare the `1.03` and `1.03--` versions.

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

## Git Tags

You may notice, that the tags in this repository look a little bit strange.
You're right, but there's system behind this naming logic.

The versions of the both plugins aren't synchronous, so every tag beginning with the letter 'H' 
and a following version number marks a new HasteIt version. In comparision to that every tag 
which begins with 'D' marks a new DiscordBeam version.

There are some older tags which don't have a letter in front, they are just old HasteIt versions.