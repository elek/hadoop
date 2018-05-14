#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DOCDIR="$DIR/../.."

if [ ! "$(which hugo)" ]; then
   echo "Hugo is not yet installed. Doc generation is skipped."
   exit -1
fi

THEMEDIR="$DOCDIR/themes/docdock"
if [ ! -d "$THEMEDIR" ]; then
   echo "Theme dir is not yet cloned to $THEMEDIR"
   git clone git@github.com:matcornic/hugo-theme-learn.git $THEMEDIR
   cd $THEMEDIR
   git reset --hard 59c4cbf
   cd -
fi

DESTDIR=$DOCDIR/target/classes
mkdir -p $DESTDIR
cd $DOCDIR
hugo -d $DESTDIR
cd -
