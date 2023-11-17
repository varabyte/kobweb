# Installation

To get the Kobweb binary (CLI).

Which helps you to create the apps in the command line, configure them and export them, and more
You can either install it use a package manager to
install a version that is already built for you, or build it yourself

## Methods
- [Installation](#installation)
  - [Methods](#methods)
    - [Using a package manager](#using-a-package-manager)
      - [Homebrew](#homebrew)
      - [Scoop](#scoop)
      - [SDKMAN!](#sdkman)
      - [Arch Linux](#arch-linux)
      - [Don't see your favorite package manager?](#dont-see-your-favorite-package-manager)
    - [Build from scratch](#build-from-scratch)
      - [Build the Kobweb binary](#build-the-kobweb-binary)
      - [Download a JDK](#download-a-jdk)
      - [Install a JDK with the IntelliJ IDE](#install-a-jdk-with-the-intellij-ide)
      - [Building the Kobweb CLI](#building-the-kobweb-cli)
  - [Update the Kobweb binary](#update-the-kobweb-binary)

### Using a package manager

*Major thanks to [aalmiray](https://github.com/aalmiray) and [helpermethod](https://github.com/helpermethod) to helping
me get these installation options working. Check out [JReleaser](https://github.com/jreleaser/jreleaser) if you ever
need to do this in your own project!*

#### [Homebrew](https://brew.sh/)

*Supports macOS and Linux*

  ```bash
  $ brew install varabyte/tap/kobweb
  ```

#### [Scoop](https://scoop.sh/)

*Supports Windows*

  ```shell
  # Note: Adding buckets only has to be done once.
  
  # Feel free to skip java if you already have it
  > scoop bucket add java
  > scoop install java/openjdk
  
  # Install kobweb
  > scoop bucket add varabyte https://github.com/varabyte/scoop-varabyte.git
  > scoop install varabyte/kobweb
  ```

#### [SDKMAN!](https://sdkman.io/)

*OS: Windows, Mac, and \*nix*

  ```shell
  $ sdk install kobweb
  ```

#### Arch Linux

*Thanks a ton to [aksh1618](https://github.com/aksh1618) for adding support for this target!*

With an AUR helper:

  ```shell
  $ trizen -S kobweb
  ```

Without an AUR helper:

  ```shell
  $ git clone https://aur.archlinux.org/kobweb.git
  $ cd kobweb
  $ makepkg -si
  ```

#### Don't see your favorite package manager?

Please see: https://github.com/varabyte/kobweb/issues/117 and consider leaving a comment!

### Build from scratch

Our binary artifact is hosted on GitHub. To download latest, you can either
[grab the zip or tar file from the GitHub](https://github.com/varabyte/kobweb-cli/releases/tag/v0.9.13) or you can fetch
it from your terminal:

  ```bash
  $ cd /path/to/applications
  
  # You can either pull down the zip file
  
  $ wget https://github.com/varabyte/kobweb-cli/releases/download/v0.9.13/kobweb-0.9.13.zip
  $ unzip kobweb-0.9.13.zip
  
  # ... or the tar file
  
  $ wget https://github.com/varabyte/kobweb-cli/releases/download/v0.9.13/kobweb-0.9.13.tar
  $ tar -xvf kobweb-0.9.13.tar
  ```

and I recommend adding it to your path, either directly:

  ```bash
  $ PATH=$PATH:/path/to/applications/kobweb-0.9.13/bin
  $ kobweb version # to check it's working
  ```

or via symbolic link:

  ```bash
  $ cd /path/to/bin # some folder you've created that's in your PATH
  $ ln -s /path/to/applications/kobweb-0.9.13/bin/kobweb kobweb
  ```

#### Build the Kobweb binary

Although we host Kobweb artifacts on GitHub, it's easy enough to build your own.

Building Kobweb requires JDK11 or newer. We'll first discuss how to add it.

#### Download a JDK

If you want full control over your JDK installation, manually downloading is a good option.

* [Download a JDK for your OS](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* Unzip it somewhere
* Update your `JAVA_HOME` variable to point at it.

```bash
JAVA_HOME=/path/to/jdks/corretto-11.0.12
# ... or whatever version or path you chose
```

#### Install a JDK with the IntelliJ IDE

For a more automated approach, you can request IntelliJ to install a JDK for you.

Follow their instructions here: https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk

#### Building the Kobweb CLI

The Kobweb CLI is actually maintained in a separate GitHub repo. Once you have the JDK set up, it should be easy to
clone and build it:

```bash
$ cd /path/to/src/root # some folder you've created for storing src code
$ git clone https://github.com/varabyte/kobweb-cli
$ cd kobweb-cli
$ ./gradlew :kobweb:installDist
```

Finally, update your PATH:

```bash
$ PATH=$PATH:/path/to/src/root/kobweb-cli/kobweb/build/install/kobweb/bin
$ kobweb version # to check it's working
```

## Update the Kobweb binary

If you previously installed Kobweb and are aware that a new version is available, the way you update it depends on how
you installed it.

| Method                    | Instructions                                                                                                                         |
|---------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| Homebrew                  | `brew update`<br/>`brew upgrade kobweb`                                                                                              |
| Scoop                     | `scoop update kobweb`                                                                                                                |
| SDKMAN!                   | `sdk upgrade kobweb`                                                                                                                 |
| Downloaded from<br>Github | Visit the [latest release](https://github.com/varabyte/kobweb-cli/releases/tag/v0.9.13). You can find both a zip and tar file there. |