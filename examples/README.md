This directory contains Kobweb example sites and apps. They are _intentionally_ treated as standalone projects, excluded
from the main project, as this better mimics what these projects feel like in the wild.

Also, the projects themselves depend on the Kobweb Gradle plugins, and it's a bit of a chicken and egg problem to have
one monolithic project that both defines a plugin and has projects trying to use it in their plugins block.

Otherwise, these sample projects are a useful way to play with and verify API changes made to the libraries and plugins
they use.