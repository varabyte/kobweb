# Contributing

Are you interested in contributing code to Kobweb? First of all, let me say -- thanks! I really mean it. I appreciate
your time and that you're giving some of it to this project.

As it's still very early in the life of the project, the requirements set out for now will be pretty minimal, but I
expect to tighten them over time as the project stabilizes.

## Code of Conduct

First of all, **be aware of our code of conduct**

* For now, we're borrowing almost wholesale from the
  [Rust Code of conduct](https://www.rust-lang.org/policies/code-of-conduct)
* TL;DR - debate is welcome to generate the best ideas, but respect each other. We'll never tolerate behavior where
  someone is affecting the community negatively or attacking people and not ideas.

## Additional Rules

Other than that, just a few rules:

* **Code consistency and readability are the highest priorities right now**
    * Consider using IntelliJ IDEA (Community Edition is fine). This way, we can set common project settings in a file
      and lean on the tools to resolve disagreements, so we can focus on more interesting work.
    * Code test coverage will _eventually_ become very important but the pieces don't currently exist yet to enable
      this, and things are still a bit too experimental to commit to locking down on this quite yet. However, code
      changes will be run against example code.
* **Pull requests should be associated with a bug that you have assigned to yourself**
    * This ensures that we won't have multiple people working on the same issue at the same time
* **Use the IDE version control to commit code.**
    * This adds some hooks that ensure code is properly formatted when committed.
* **New features should be discussed with us first** (see the
  [contacting us](https://github.com/varabyte/kobweb#connecting-with-us) section in the main README)
    * This ensures that you won't waste your time on something we may have reasons to reject
    * Of course, pull requests can be an effective way of proposing a feature, so go for it as long as you're OK that
      there are no guarantees we'll take the change

These rules are NOT dogma. I am always open to exceptions, but be ready to explain why your situation is special!

## Kobweb Development

### Latest Branch

Kobweb development does *NOT* happen on `main`. You should consider the `main` branch as effectively read-only.

Instead, work is done on the latest version branch. For example, if the current version of Kobweb is "1.2.3", then look
for an active branch named "1.2.4" *or* "1.3.0".

### Kobweb Playground

When developing on Kobweb, instead of opening the `kobweb` project directly, you may prefer opening the
`kobweb/playground` project instead.

This module is a simple Kobweb project which is connected to the base project so that you can make changes to the base
project and see them reflected immediately in the output of a playground run.

Running the playground is simple! In the terminal:

```bash
$ cd playground/site
$ kobweb run
```

Feel free to aggressively change the playground on your own local machine. Checking changes in for the playground will
probably be fairly rare, but I'm open to it if you think they will be useful for future devs as well.

#### Development Server

If you want to get a breakpoint in a Kobweb server, then instead of using `kobweb run` in the playground folder,
run the "Playground Server" run configuration provided in the Playground project instead.

You'll lose the convenient live reloading experience with this approach, but it can be very useful when you're focusing
on server development. (See the `backend/server` module for the relevant code.)

### Publish to Maven Local

If you want to build Kobweb and test it out in a totally separate project, you can publish it to your local Maven
repository:

```bash
# In the root folder
$ ./gradlew publishToMavenLocal
```

Then, in the project you want to test it out in, search for all occurrences of the line
`maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")` and declare a `mavenLocal` declaration right above it
(to make sure the local publication will be found first).

Finally, (and optionally), I like to restrict my maven local declaration to just Kobweb stuff:

```kotlin
mavenLocal {
    content {
        includeGroupAndSubgroups("com.varabyte.kobweb")
        includeGroupAndSubgroups("com.varabyte.kobwebx")
    }
}
```

## Thanks!!

Thank you, again, for choosing to help out on Kobweb.
