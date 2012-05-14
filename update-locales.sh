#!/bin/bash

function copy_language
{
    mkdir -p $1/res/$3
    cp locale-$1/$2/strings.xml $1/res/$3/
}

function deploy
{
    wget -O locale-$1.zip http://mygengo.com/string/p/$1-1/export/all/$2
    unzip -o locale-$1.zip -d locale-$1
    copy_language $1 en values
    copy_language $1 pt-br values-pt-rBR
    copy_language $1 es values-es
    copy_language $1 fa values-fa

    rm -r locale-$1.zip locale-$1/
}

deploy bbb-android 05d7e8bc27e2a666fae7868ffa5e640b91bc0fdfdc4755faa3831b8786a5bcd6

deploy bbb-android-core 876a603d781a5c87ba07b4aa4b8dffaa5fbeea7884ccb8f09e855c162bd5c03d

deploy mconf-mobile 8e038fd513d3b68c709395b6ffec7eeb1a1e9602c4cdb67837b614ac97f98276

