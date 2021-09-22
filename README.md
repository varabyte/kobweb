<a href="https://discord.gg/bCdxPr7aTV">
  <img alt="Varabyte Discord" src="https://img.shields.io/discord/886036660767305799.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2" />
</a>

# K🕸️bweb

Hello! If you've landed here, you're a bit early. This is a placeholder README.

Our (very small) team is experimenting with a Kotlin framework for building websites that is inspired by Next.js, Chakra
UI, and other similar approaches. If you're reading this, it means it is early days yet, and we are still uncertain 
about upcoming questions we have about the feasibility of this approach and the shape of the API.

Hopefully, if we can satisfy all of our concerns, we'll have more to announce by end of 2021, if not earlier.

If you've somehow found this README and would like to be updated when the code is ready to be tested by the wider
community, [feel free to reach out to me](mailto:bitspittle@gmail.com), and I'll add you to a list of people to notify.
**Note: I am just an individual person, but I promise not to harvest, distribute, or in any way use any emails I
receive except to 1) respond to any questions asked or 2) ping when the status of this project changes.**

# Running

This section will be fleshed out more in the future. For now, to see what we have so far, run:

```bash
$ ./gradlew :examples:helloworld:jsBrowserRun
```

This demo on its own undersells the experience, which is more about incrementally developing a Kotlin web app with a
nice library and hot reloading, but hey, it's a start.

# Templates

Kobweb provides its templates in a separate git repository, which is referenced within this project as a submodule for
convenience. To pull down everything, run:

```bash
/path/to/my/src
$ git clone --recurse-submodules https://github.com/varabyte/kobweb

# or, if you've already previously cloned kobweb...
/path/to/my/src/kobweb
$ git submodule update --init
```