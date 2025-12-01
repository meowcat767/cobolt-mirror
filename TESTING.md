# Testing Git Integration

This document shows how to test Cobolt's Git integration.

## Test 1: Basic Git Repository

```bash
# Create a test Git repository
cd /tmp
mkdir cobolt-git-test
cd cobolt-git-test
git init

# Create a file
echo "# Testing Cobolt with Git" > README.md

# Use Cobolt commands on Git repo
cobolt add README.md
cobolt commit -m "Initial commit with Cobolt"
cobolt status

# Verify it's a real Git commit
git log
```

Expected output:
- `cobolt add` should stage the file in Git
- `cobolt commit` should create a Git commit
- `git log` should show the Cobolt-created commit

## Test 2: Push to Remote

```bash
# Create a test repository on GitHub/GitLab first, then:
git remote add origin https://github.com/user/test-repo.git
cobolt push origin main

# Should push to Git remote with Cobolt's pretty output
```

## Test 3: Pull Updates

```bash
# After someone pushes to remote:
cobolt pull

# Should pull Git changes with progress indicator
```

## Test 4: Mixed Usage

```bash
# Use both Git and Cobolt interchangeably
git add file1.txt
cobolt add file2.txt
cobolt commit -m "Mixed commit"

git log  # Should show both additions
```

## Test 5: Status Comparison

```bash
# Compare output
git status

echo ""
echo "--- Cobolt Status ---"
cobolt status
```

Cobolt's status should be more visually appealing with colors and symbols.

## Verification Checklist

- [ ] `cobolt add` stages files in `.git/index`
- [ ] `cobolt commit` creates commits in `.git/objects/`
- [ ] Commits are visible with `git log`
- [ ] `cobolt push` works with Git remotes
- [ ] `cobolt pull` fetches from Git remotes
- [ ] Both Git and Cobolt can be used interchangeably
- [ ] Cobolt shows better visual feedback than Git
