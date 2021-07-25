Core classes in the Nekt framework.

Nekt sites will be required to use classes from this library, as the `nekt` binary will use reflection to search for
them in order to set up a bare minimum web server.

For example, `Page` is the base class for composable logic that will be converted into `.html` files, while `App` is a
singleton which owns the state and configuration of the entire site (such as plugins and style / theming values).

This also contains composables for all the standard HTML components, albiet with a very standard and stark theme that
most users will want to customize for their own site.