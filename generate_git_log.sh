#!/bin/bash

# Check if the directory is a Git repository
if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    echo "❌ Error: This is not a Git repository. Please navigate to your Git project directory."
    exit 1
fi

# Prompt the user for their student number
read -p "Enter your student number: " student_number

# Validate input
if [[ -z "$student_number" ]]; then
    echo "❌ Error: Student number cannot be empty."
    exit 1
fi

# Generate the Git log file
git log --pretty=format:"%h - %an, %ar : %s" > "${student_number}.txt"

echo "✅ Git log successfully saved in: ${student_number}.txt"