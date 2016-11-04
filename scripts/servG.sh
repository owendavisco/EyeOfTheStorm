#!/bin/sh

################################
# Gerhard van Andel            #
# Version 1.0, 2016-09-29      #
# Colorado State University    #
################################

# To ssh into multipule machines at a time

# to setup %  ./servG -s

# have more time to have fun!

RED='\033[0;31m'
NC='\033[0m' # No Color

serverFile="servG_machines"

setup() {
	sshFile="${HOME}/.ssh/servG_519"

	read -p "Are you sure you want to continue setup [yes or no]? " yn
	case $yn in
		[Yy]* ) break;;
		* ) echo "Will now exit."; exit 1;;
	esac

	if ! [ -f $sshFile ]; then
		ssh-keygen -q -t ed25519 -f ${HOME}/.ssh/servG_519 -N ""
	fi

	if ! [ -f ${HOME}/.ssh/config ]; then
		touch ${HOME}/.ssh/config
	fi

	U=$USER
	read -p "Would you like to enter an alternative username [yes or no]? " yn
	case $yn in
		[Yy]* ) read -p "Please enter username: " U;;
		* ) break;;
	esac

	grep -q -F '*.cs.colostate.edu' ${HOME}/.ssh/config
	if [ $? -eq 1 ]; then
		echo "" >> ${HOME}/.ssh/config
		echo "Host *.cs.colostate.edu" >> ${HOME}/.ssh/config
		echo "IdentityFile ${sshFile}" >> ${HOME}/.ssh/config
		echo "User ${U}" >> ${HOME}/.ssh/config
	else
		echo "Host configuration already set in ${HOME}/.ssh/config" >&2
	fi

	if ! [ -f $serverFile ]; then
		echo "[ $RED ERROR $NC ] server list file not found. Will make one for you." >&2
		wget -q https://www.cs.colostate.edu/~info/machines -O machines
		touch $serverFile
		head -n60 machines | tail -n58 | awk -F"\t" '{ print $1".cs.colostate.edu" }' | shuf -n10 > $serverFile
		rm machines
	fi

	read -r line < "$serverFile"
	cat ${sshFile}.pub | ssh $line "cat >> ~/.ssh/authorized_keys"

}

# read file
if [ $# -eq 1 ] && [ "$1" = "-s" ]; then
	setup
	exit 0
elif [ $# -ne 0 ]; then
	echo "Usage: $0 [ -s ] setup ssh keys." >&2
	echo "Usage: $0 run program." >&2
	exit 1
fi

if ! [ -f $serverFile ]; then
	echo "[ $RED ERROR $NC ] $serverFile not found, please run with -s flag." >&2
	exit 1
fi

while read -r line; do
	xfce4-terminal -T $line -H -e "ssh $line" &
done < "$serverFile"

