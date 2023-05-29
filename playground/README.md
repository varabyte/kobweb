This directory contains a very basic Kobweb project. It is _intentionally_ set up as a standalone project, excluded
from the main project, as this better mimics what such a project feels like in the wild. (See
[Gradle Composite Builds](https://docs.gradle.org/current/userguide/composite_builds.html) for more information.)

Also, the project depends on the Kobweb Gradle plugins, and it's a chicken and egg problem to have one monolithic
project that both defines a plugin and has a project inside it trying to apply it in their plugins block.

Ultimately, this project is a space where a developer can play with and verify API changes made to the libraries and
plugins they use before checking something in. Any changes made to Kobweb are picked up immediately by this project,
instead of requiring an expensive "publish to maven local".

You can run it like a normal Kobweb project. Just run `kobweb run` inside this directory. Note that it will may
take significantly longer than your average project because this will configure and rebuild Kobweb as necessary!

