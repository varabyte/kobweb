# Compatibility

This page lists the versions of Compose and Kotlin that each version of Kobweb is built against.

It is not currently necessary to match these versions exactly in your Kobweb project, but it may be useful to keep them
in sync to avoid unexpected issues.

| Kobweb      | Compose                                                      | Kotlin |
|-------------|--------------------------------------------------------------|--------|
| 0.25.0+     | [1.11.1 (HTML),<br/>1.11.2 (Runtime)](#compose-dependencies) | 2.4.0  |
| 0.24.0+     | [1.10.0 (HTML),<br/>1.10.2 (Runtime)](#compose-dependencies) | 2.3.10 |
| 0.23.3+     | 1.8.0                                                        | 2.2.20 |
| 0.23.1+     | 1.8.0                                                        | 2.2.10 |
| 0.23.0      | 1.8.0                                                        | 2.2.0  |
| 0.22.0      | 1.8.0                                                        | 2.1.21 |
| 0.21.0+     | 1.7.3                                                        | 2.1.20 |
| 0.20.3+     | 1.7.3                                                        | 2.1.10 |
| 0.20.1+     | 1.7.3                                                        | 2.1.0  |
| 0.20.0      | 1.7.1                                                        | 2.1.0  |
| 0.19.3+     | 1.7.1                                                        | 2.0.20 |
| 0.19.1+     | 1.6.11                                                       | 2.0.20 |
| 0.19.0[^k2] | 1.6.11                                                       | 2.0.10 |
| 0.18.0+     | 1.6.2                                                        | 1.9.23 |
| 0.17.2+     | 1.6.2                                                        | 1.9.23 |
| 0.17.1      | 1.6.1                                                        | 1.9.23 |
| 0.17.0      | 1.6.0                                                        | 1.9.22 |
| 0.16.1+     | 1.5.12                                                       | 1.9.22 |
| 0.16.0      | 1.5.11                                                       | 1.9.21 |
| 0.15.2+     | 1.5.11                                                       | 1.9.21 |
| 0.15.0+     | 1.5.10                                                       | 1.9.20 |
| 0.14.0+     | 1.5.1                                                        | 1.9.10 |
| 0.13.7+     | 1.4.1                                                        | 1.8.20 |
| 0.13.0+     | 1.4.0                                                        | 1.8.20 |
| 0.12.1+     | 1.3.1                                                        | 1.8.10 |
| 0.12.0      | 1.3.0                                                        | 1.8.0  |
| 0.11.4+     | 1.2.2                                                        | 1.7.20 |
| 0.11.0+     | 1.2.2                                                        | 1.7.20 |
| 0.10.4+     | 1.2.1                                                        | 1.7.20 |
| 0.10.0+     | 1.2.0                                                        | 1.7.10 |
| 0.9.12+     | 1.1.1                                                        | 1.6.10 |
| 0.9.11-     | 1.0.0                                                        | 1.6.10 |

[^k2]: See [migration docs](https://github.com/varabyte/kobweb/blob/v0.19.0/docs/k2-migration.md) if upgrading to Kobweb
0.19.x from an older project.

## Compose Dependencies

Starting from 0.24.0, Kobweb depends
on both [AndroidX Compose Runtime](https://mvnrepository.com/artifact/androidx.compose.runtime/runtime)
and [JetBrains Compose HTML](https://mvnrepository.com/artifact/org.jetbrains.compose.html/html-core).

```toml
compose-html = "..."
compose-runtime = "..."

[libraries]
compose-html-core = { module = "org.jetbrains.compose.html:html-core", version.ref = "compose-html" }
compose-runtime = { module = "androidx.compose.runtime:runtime", version.ref = "compose-runtime" }
```
