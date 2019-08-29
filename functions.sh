#!/bin/bash

print_menu() {
	echo "=============================================================="
	echo "Welcome to the Wiki-Speak Authoring Tool"
	echo "=============================================================="
	echo "Please select from one of the following options:"
	echo "(l)ist existing creations"
	echo "(p)lay an existing creation"
	echo "(d)elete an existing creation"
	echo "(c)reate a new creation"
	echo "(q)uit authoring tool"

	return 0
}

list() {
	echo "list" $1
}

play() {
	echo "play" $1
}

delete() {
	echo "delete" $1
}

create() {
	echo "create" $1
}
