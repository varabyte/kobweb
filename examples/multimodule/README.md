This is an example of how to break up a Kobweb project into multiple modules.

As a concrete use-case, we create a chat server which requires logging in with an account to use it.

## Motivation

Two common reasons for splitting up a project are:

### Project organization

It can be easier to break major features up into their own separate module areas, to reduce the amount of code you have
to worry about at one time, or (if on a team) to give people different areas that they own.

### Publishing a library

You can create reusable libraries that you can publish to Maven. While this may not be useful for pages and API routes,
it can be a great way to share custom widgets.

## Structure

In this project, the `site` module represents the entire application. It is in this project that you'll trigger
`kobweb run`.

Then, there are three library modules: `core` (a bunch of general components, shared by multiple modules), `auth`
(which provides pages and server support for creating an account and logging in), and `chat` (which handles posting and
fetching messages).

While you can put components, pages, and API routes in both library *and* application modules, it is recommended that if
you do decide to split up your project, you should strive to keep the application module relatively thin. The main thing
that should go in there is the `@App` composable (if you declare one) and maybe the site root (i.e. `pages/Index.kt`).
The rest, it should delegate to the other modules.

### Compared to a monolith project

Some things to notice about this multimodule app (vs. how it differs from a Kobweb monolith project):

* The `core`, `auth`, and `chat` modules apply the `com.varabyte.kobweb.library` plugin in their build script, while
  the `site` module applies the `com.varabyte.kobweb.application` plugin.

* The `.kobweb/conf.yaml` file lives under `site` and not at the root level.
  * Note that its dev script path points into the *root* build folder, not the *site* build folder.
  * There is a `.gitignore` file in the `site` subfolder in addition to the one at the root level. This is because it is
    responsible for ignoring the `.kobweb` folder, which moved into the `site` folder. 

`core`, `auth`, and `chat` each get processed by the Kobweb library plugin at build time, generating some intermediate
artifacts that the Kobweb application plugin looks for and consumes.

The application plugin is responsible for bundling everything together into a final, cohesive, single site.

## Run

To run the demo:

```bash
cd site
kobweb run
```