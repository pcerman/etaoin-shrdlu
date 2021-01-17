#!/usr/bin/sh

#  +---------------------------------------------------------------+
#  | etaoin-shrdlu: LISP interpreter for the SHRDLU project        |
#  |                                                               |
#  | Original source code is published in github resository:       |
#  | https://github.com/pcerman/etaoin-shrdlu.                     |
#  |                                                               |
#  | Copyright (c) 2021 Peter Cerman (https://github.com/pcerman)  |
#  |                                                               |
#  | This source code is released under Mozilla Public License 2.0 |
#  +---------------------------------------------------------------+


if [ ! -f dist/etaoin.jar ]; then
    # build the etaoin.jar
    ant -Dnb.internal.action.name=rebuild clean jar
fi

if [ ! -f code.tar ]; then
    # download original code.tar
    wget http://hci.stanford.edu/winograd/shrdlu/code.tar
fi

if [ ! -d code ]; then
    # untar original code
    tar xf code.tar code
fi

if [ ! -f SHRDLU/PLNR ]; then
    # apply patches
    cp code/blockl  SHRDLU/BLOCKL
    cp code/blockp  SHRDLU/BLOCKP
    cp code/cgram   SHRDLU/CGRAM
    cp code/data    SHRDLU/DATA
    cp code/dictio  SHRDLU/DICTIO
    cp code/ginter  SHRDLU/GINTER
    cp code/morpho  SHRDLU/MORPHO
    cp code/newans  SHRDLU/NEWANS
    cp code/plnr    SHRDLU/PLNR
    cp code/progmr  SHRDLU/PROGMR
    cp code/setup   SHRDLU/SETUP
    cp code/show    SHRDLU/SHOW
    cp code/smass   SHRDLU/SMASS
    cp code/smspec  SHRDLU/SMSPEC
    cp code/smutil  SHRDLU/SMUTIL
    cp code/syscom  SHRDLU/SYSCOM
    cp code/thtrac  SHRDLU/THTRAC

    patch SHRDLU/BLOCKL  patch/BLOCKL.patch
    patch SHRDLU/BLOCKP  patch/BLOCKP.patch
    patch SHRDLU/CGRAM   patch/CGRAM.patch
    patch SHRDLU/DATA    patch/DATA.patch
    patch SHRDLU/DICTIO  patch/DICTIO.patch
    patch SHRDLU/GINTER  patch/GINTER.patch
    patch SHRDLU/MORPHO  patch/MORPHO.patch
    patch SHRDLU/NEWANS  patch/NEWANS.patch
    patch SHRDLU/PLNR    patch/PLNR.patch
    patch SHRDLU/PROGMR  patch/PROGMR.patch
    patch SHRDLU/SETUP   patch/SETUP.patch
    patch SHRDLU/SHOW    patch/SHOW.patch
    patch SHRDLU/SMASS   patch/SMASS.patch
    patch SHRDLU/SMSPEC  patch/SMSPEC.patch
    patch SHRDLU/SMUTIL  patch/SMUTIL.patch
    patch SHRDLU/SYSCOM  patch/SYSCOM.patch
    patch SHRDLU/THTRAC  patch/THTRAC.patch
fi
