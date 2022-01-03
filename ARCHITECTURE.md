# Architecture

This document contains a high level overview of the Kobweb project.

Since it's easy for documents like this to become stale over time, this one only describes the project layout in rough
strokes to decrease the likeliness of it happening here. You are encouraged to read it to get an understanding of all
the various pieces, after which you can dive in deeper to each module, which contain their own `README`s with more
information.

However, if you read this document and feel that anything mentioned within is obsolete, consider
[contacting us](https://github.com/varabyte/kobweb#connecting-with-us) and we'll update it if so. Thanks!

# Overview

Kobweb is divided into the following parts:

* the `kobweb` binary
* the frontend
* the backend
* Gradle plugins
* common code
* example projects
* templates

## Binary

**Folder: `cli`**

The binary will be most users' initial experience with Kobweb.

The binary itself is relatively thin - it mostly delegates to `git` (for fetching templates) and to `gradle` (for
building code and triggering our [Kobweb Gradle plugins ▼](#gradle-plugins) with all the right arguments.)

If you ever find yourself working in here, you may want to familiarize yourself with the
[Kotter](https://github.com/varabyte/kotter) library as well, since it makes heavy use of it.

## Frontend

**Folder: `frontend`**

This module represents the "website support" part of Kobweb. It contains JS libraries which are built on top of Web
Compose. Kobweb and Silk live here.

## Backend

**Folder: `backend`**

This module owns the server logic as well as APIs for interacting with it. This includes an actual API library used for
talking to the server as well as the "Kobweb API" library (which is a JVM library that a Kobweb project can use to
define API routes).

## Gradle Plugins

**Folder: `gradle-plugins`**

Although it's hoped that most users will only interact with the `kobweb` binary and not need to be aware of the Kobweb
Gradle plugins behind the scenes, the plugins are responsible for most of the heavy lifting. This is because it is
through Gradle that we get access to so much relevant information that helps us iterate through the user's codebase and
parse it quickly and effectively.

The main plugin, called the Application Plugin, lives in the root folder, but it's expected that we'll introduce many
additional plugins over time, such as the Markdown plugin. These additional plugins should live under the `extensions`
subfolder.

## Common

**Folder: `common`**

Occasionally, code comes up that is generally useful to both the frontend and backend modules. Intuitively enough, they
live here!

## Example Projects

**Folder: `examples`**

Projects nested under the `examples` folder are technically separate projects. They use Gradle's
[Composite Builds](https://docs.gradle.org/current/userguide/composite_builds.html) feature to reference this Kobweb
project via an `includeBuild` dependency. This allows us to experiment with dev changes to Kobweb, Silk, the server, and
all the Gradle plugins.

For example, with the `helloworld` module, I often open it up in a separate IDE window, where it resolves dependencies
correctly. (Within the Kobweb project view, the IDE thinks it's just dead code, since it's not included in our
`settings.gradle` files). Then, after making a change to the Kobweb codebase, in a terminal, I can run `kobweb run`
under the `examples/helloworld` directory to see the results. After I can be sure the change is working, I may choose to
update some of the [templates ▼](#templates) as well.

## Templates

**Folder: `templates`**

This is actually a submodule clone of https://github.com/varabyte/kobweb-templates.

To update the templates, I modify them in place and `git push` the changes. I then `cd` back into the parent Kobweb
project, at which point I `git add templates` and `git push` that as well, so the submodule is kept in sync with latest.