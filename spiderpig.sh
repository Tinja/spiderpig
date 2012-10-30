#!/bin/bash

findself() {
    SELF=`dirname $0`
}
findself

shopt -s huponexit #Makes sure doesn't get killed on exit, usefull for ssh
cd $SELF
    java -jar spiderpig.jar $*
    excode=$?
cd - > /dev/null

exit $excode
