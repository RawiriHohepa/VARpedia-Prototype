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
	l)
		list
		;;
	p)
		selected_creation=$2		
		play $selected_creation
		;;
	d)
		selected_creation=$2
		delete $selected_creation
		;;
	s)
		search_term=$2
		search $search_term
		;;
	c)
		create
		;;
	q)
		echo "Thank you for using the Wiki-Speak Authoring Tool."
		;;
	*)
		echo "Invalid selection, please try again."
		;;
	esac
