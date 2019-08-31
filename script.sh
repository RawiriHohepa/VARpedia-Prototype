#!/bin/bash

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
