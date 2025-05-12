# Check if in a Git repository
try {
    git rev-parse --is-inside-work-tree > $null 2>&1
} catch {
    Write-Host "❌ Error: This is not a Git repository. Please navigate to your Git project directory."
    exit 1
}

# Prompt for student number
$student_number = Read-Host "Enter your student number"

# Validate input
if ([string]::IsNullOrWhiteSpace($student_number)) {
    Write-Host "❌ Error: Student number cannot be empty."
    exit 1
}

# Generate Git log
git log --pretty=format:"%h - %an, %ar : %s" > "$student_number.txt"

Write-Host "✅ Git log successfully saved in: $student_number.txt"