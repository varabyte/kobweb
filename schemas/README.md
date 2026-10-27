`kobweb-conf.schema.json` is a [JSON schema](https://json-schema.org/) for `.kobweb/conf.yaml`, which editors can use
to offer completion, hover docs and validation.

It's written by hand to match the classes in `KobwebConf.kt`. If you change those classes, update the schema too.
`KobwebConfSchemaTest` fails if the two disagree on structure, types or defaults. Descriptions aren't checked, so keep
them in sync with the KDocs by hand.

Tools may fetch the schema by its URL on GitHub (for example, the Kobweb IntelliJ plugin fetches the copy at the release
tag matching a project's Kobweb version), so please don't move or rename it.

The copy on the `main` branch tracks the latest Kobweb release, so use it when you need a schema without a specific
version in mind.
