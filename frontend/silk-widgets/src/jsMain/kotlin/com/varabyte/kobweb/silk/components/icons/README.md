This directory provides some basic SVG icons. Users will more likely reach to Font Awesome or Google Material icons, but
SVG icons can be a simple way to get some quick icons working in your project without having to pull in a large
dependency.

Some of the icons in here are based on those found in Chakra UI: https://chakra-ui.com/docs/components/icon

IMPORTANT: We intentionally separate icon methods, one per file, to avoid a huge site size penalty because of the way
Compose works. What it will otherwise do is wrap all icon methods into a "ComposableSingleton$IconsKt" singleton which
means that if a user uses a single icon, then the DCE pass won't be able to remove any others. Since some of these icons
are decently large (thanks to complex path data strings), it is not an insignificant hit.