#!/bin/bash

# Array of well-known book and music album terms
query_terms=(
  "1984"
  "To Kill a Mockingbird"
  "Pride and Prejudice"
  "The Great Gatsby"
  "Moby Dick"
  "The Catcher in the Rye"
  "Harry Potter and the Philosopher's Stone"
  "The Lord of the Rings"
  "The Chronicles of Narnia"
  "The Da Vinci Code"
  "Thriller"
  "Abbey Road"
  "Dark Side of the Moon"
  "Rumours"
  "The Joshua Tree"
  "Nevermind"
  "Back in Black"
  "Sgt. Pepper's Lonely Hearts Club Band"
  "Born to Run"
  "The Wall"
  "The Hobbit"
  "Alice's Adventures in Wonderland"
  "Crime and Punishment"
  "The Picture of Dorian Gray"
  "The Odyssey"
  "Jane Eyre"
  "War and Peace"
  "The Divine Comedy"
  "The Alchemist"
  "The Little Prince"
  "Pet Sounds"
  "Thriller"
  "Hotel California"
  "Sgt. Pepper's Lonely Hearts Club Band"
  "The Velvet Underground & Nico"
  "Bridge Over Troubled Water"
  "Rumours"
  "Purple Rain"
  "Back in Black"
  "The Dark Side of the Moon"
  "Kind of Blue"
  "Led Zeppelin IV"
  "Exile on Main St."
)

# Function to generate a random index within the range of the query_terms array
get_random_index() {
  echo $(($RANDOM % ${#query_terms[@]}))
}

# Function to URL encode a string
url_encode() {
  local string="$1"
  # Use Python to URL encode the string
  python3 -c "import urllib.parse; print(urllib.parse.quote('''$string'''))"
}

# Perform a single API call
perform_api_call() {
  query="${query_terms[$(get_random_index)]}"
  encoded_query=$(url_encode "$query")
  endpoint="http://localhost:8080/search?query=$encoded_query"

  # Make the API call
  curl "$endpoint"
}

# Iterate through the query terms
for ((i = 1; i <= 1000; i++)); do
  perform_api_call

  # Check if 10 calls have been made
  if ((i % 10 == 0)); then
    sleep 0.2  # Sleep for 200 milliseconds
  fi
done
