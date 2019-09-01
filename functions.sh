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

search() {
	echo "create" $1
	if [ -d "$TMP_DIR" ]
	then
		rm -rf $TMP_DIR
	fi
	mkdir $TMP_DIR	

	search_term=$1
	search_result=`wikit $search_term`
	echo $search_result | grep "not found :^(" &> /dev/null
	search_is_invalid=$?

	if	[ "$search_is_invalid" -eq 0 ]
	then
		echo "Term not found"
	else
		# Separate each sentence into a new line
		echo $search_result | sed 's/\. /.\n/g' > $FULL_SEARCH_DIR
		
		# Count and print the number of lines
		total_sentences=`cat $FULL_SEARCH_DIR | wc -l`
		echo $total_sentences
		
		# Print the search result with lines numbered
		cat -n $FULL_SEARCH_DIR
	fi
}
