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
		
		# Print the search term
		echo $search_term
		
		# Print the search result with lines numbered
		cat -n $FULL_SEARCH_DIR
	fi
}

create() {
	search_term=$1
	included_sentences=$2
	creation_name=$3
	
	current_creation_dir=$CREATIONS_DIR"/"$creation_name.mp4
	
	# Save the specified range of sentences into a file to be used in text2wave
	head -n $included_sentences $FULL_SEARCH_DIR > $PARTIAL_SEARCH_DIR

	# Convert the sentences into a .wav format using festival
	text2wave -o $AUDIO_DIR $PARTIAL_SEARCH_DIR
	
	if [ ! -d "$CREATIONS_DIR" ]
	then
		mkdir $CREATIONS_DIR
	fi

	# Create the .mp4 creation using the .wav sound file and search term
	ffmpeg -f lavfi -i color=c=blue:s=320x240:d=5 -i $AUDIO_DIR -strict -2 -vf "drawtext=fontfile=$FONT_DIR:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='$search_term'" $current_creation_dir &> /dev/null
	
	if [ "$?" -eq 0 ]
	then
		echo "Creation was successfully created."
	else
		echo "Creation was not successfully created." >&2
	fi

	# Delete the temporary text and .wav files
	rm -rf $TMP_DIR
}
