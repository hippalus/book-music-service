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
  "The Lord of the Rings" "The Chronicles of Narnia"
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
)

# Function to generate a random index within the range of the query_terms array
get_random_index() {
  # shellcheck disable=SC2004
  echo $(($RANDOM % ${#query_terms[@]}))
}

# Perform high concurrent calls using ab
perform_concurrent_calls() {
  query="${query_terms[$(get_random_index)]}"
  endpoint="http://localhost:8080/search?query=$query"

  # Make the concurrent API calls using ab
  ab -c 5 -n 100 "$endpoint"
}

# Iterate 20 times to perform 5 concurrent calls
for ((i = 1; i <= 20; i++)); do
  perform_concurrent_calls &
done

# Wait for all the background processes to finish
wait
