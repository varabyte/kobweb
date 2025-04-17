The default Commonmark frontmatter YAML parser flattens all data into a `Map<String, List<String>>` which can absolutely
lose the initial structure of the data. This is a problem when you want to parse a nested structure like this:

```yaml
super:
  deeply:
    nested:
      option: "X"
```

This became especially problematic for us when we added the layout feature, which supports data arguments:

```yaml
data:
  title: "Kobweb"
  desc: "An article about how to use Kobweb to create a website in Kotlin."
```

and with the official solution, we can't tell where any particular data entry came from (so we couldn't tell the
difference between a top-level "title" and one nested below "data")

We therefore provide our own YAML frontmatter parsing solution to work around this limitation.