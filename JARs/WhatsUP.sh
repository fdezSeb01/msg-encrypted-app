#!/bin/bash

# Check if arg is passed
if [ $# -ne 1 ]; then
  echo "Number of clients not provided"
  exit 1
fi

server_jar="ServerWhatsUP.jar"
client_jar="ClientWhatsUP.jar"

gnome-terminal -- java -jar "$server_jar" &

num_clients="$1"
for ((i=0; i<num_clients; i++)); do
  gnome-terminal -- java -jar "$client_jar"
done

exit 0
