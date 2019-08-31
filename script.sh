#!/bin/bash

CREATIONS_DIR=./creations
TMP_DIR=./tmp
FONT_DIR=./BodoniFLF-Roman.ttf

FULL_SEARCH_DIR=$TMP_DIR/full_search.txt
PARTIAL_SEARCH_DIR=$TMP_DIR/part_search.txt
AUDIO_DIR=$TMP_DIR/audio.wav

source ./functions.sh

#print_menu

case $1 in
	l|L)
		list
		;;
	p|P)
		play
		;;
	d|D)
		delete
		;;
	c|C)
		create
		;;
	q|Q)
		echo "Thank you for using the Wiki-Speak Authoring Tool."
		;;
	*)
		echo "Invalid selection, please try again."
		;;
	esac
