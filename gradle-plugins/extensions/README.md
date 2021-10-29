Official artifacts that we ship as part of the Kobweb experience but do not consider part of its core. That is, we think
they are nice to haves, but you can still do a lot of great web app work without them, and maybe a percentage of
projects won't need them.

Any code included in these projects will live in the `kobwebx` namespace.

Extensions are expected to extend the Gradle `kobwebx` block with their own
configuration subblocks, e.g. what the Markdown plugin does:

```groovy
kobwebx {
    markdown {
        /* ... */
    }
}
```

These projects can also serve as example code for anyone else in the community who wants to consider extending Kobweb
with their own powerful extensions.