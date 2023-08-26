A collection of official plugins related to Kobweb.

## Core

Not meant to be applied by users directly. Instead, this plugin provides a bunch of common functionality that is shared
by the Library and Application plugins.

This core plugin adds the `kobweb` block and functionality for parsing and understanding the Kobweb bits
of your project. Calling plugins can consume that information and use it in ways that make sense to them.

## Library

A plugin which should be applied to a module using Kobweb that you intend to publish as a library. Such a library can
define pages, server API routes, and styled widgets.

Among other things, this plugin causes your library to get populated with some extra metadata information, which can be
used by the Application plugin.

## Application

A plugin which should be applied to a module using Kobweb that represents the entry point of your website. A Kobweb
application should have a `.kobweb` folder in its root.

Among other things, this plugin provides the `kobwebRun` task, which spins up a webserver that hosts your site, and
`kobwebExport`, which takes a snapshot of your project and saves it out as pre-rendered html files.

## Markdown

A plugin applied to a Kobweb project that has markdown files in it which should get converted into Kobweb code.

If you are creating a blog site, it's far more convenient to 



If you apply this plugin, you *must* have first applied a Kobweb Library *or* Application plugin first.