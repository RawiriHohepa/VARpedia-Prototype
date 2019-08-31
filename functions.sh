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
	# If there is no creations folder
	if [ ! -d "$CREATIONS_DIR" ]
	then
		num_of_creations=0	
		echo $num_of_creations	
	else
		num_of_creations=`ls $CREATIONS_DIR | wc -l`
		echo $num_of_creations	
		# Only list creations if there are any		
		if [ "$num_of_creations" -ne 0 ]
		then
			# List the creations and remove the file extensions
			ls $CREATIONS_DIR | sed 's/\(.*\)\..*/\1/'
		fi
	fi
}

play() {
	selected_creation_filename=$1
	selected_creation_dir=$CREATIONS_DIR/$selected_creation_filename.mp4
	ffplay -autoexit $selected_creation_dir &> /dev/null
	
	if [ "$?" -ne 0 ]
	then
		echo "Error playing video." >&2
	fi
}

delete() {
	selected_creation_filename=$1
	selected_creation_dir=$CREATIONS_DIR/$selected_creation_filename.mp4
	rm -f $selected_creation_dir
}

create() {
	echo "create" $1
}
