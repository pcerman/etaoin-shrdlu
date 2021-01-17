# etaoin-shrdlu

This is my attempt to make the
[SHRDLU](https://hci.stanford.edu/winograd/shrdlu/) program
executable.  Other information about SHRDLU project is possible to
find on the [wikipedia](https://en.wikipedia.org/wiki/SHRDLU) pages.

In order to run it, I chose a different approach. Instead of rewriting
it to the current version of the programming language, I programmed a
simple maclisp version 1970 interpreter. My goal was to change the
original SHRDLU source code as little as possible.

I did not program all its functions, only those that are necessary to
run the SHRDLU program. Some features are not implemented, some are
partially implemented. I've added a few more functions to debug
programs much easier. The interpreter is not programmed effectively
now. For example, a macro expands during evaluation instead of reading
expressions. The project is still in the "middle of the work"
state. Because there are differences between my interpreter and the
maclisp I named it etaoin to avoid confusion.

Any comments, improvement ideas, bug fixes are welcome.

It is developed on ubuntu mate Linux version 20.04.

## Prerequisites

To build this project requires some tools to be installed:

- OpenJDK 14 or higher version
- ant
- wget
- diff, patch tools
- rlwrap

## How to build

Whole the project is possible to build by `build.sh` script. It builds
etaoin.jar file, downloads original SHRDLU sources
[code.tar](http://hci.stanford.edu/winograd/shrdlu/code.tar) and
applies patches to them.

```
cd etaoin-shrdlu
./build.sh
```

## Example of use

```
cd etaoin-shrdlu
./etaoin
(load 'loader)
(run)
Pick up a big red block.
Find a block which is taller than the one you are holding and put it into the box.
What does the box contain?
How many blocks are not in the box?
Did you touch any block?
How many things are on top of green cubes?
~d
```

## License

This code is released under Mozilla Public License 2.0.

Copyright (c) 2021 Peter Cerman (https://github.com/pcerman)
