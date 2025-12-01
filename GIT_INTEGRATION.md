# Git Integration Guide

Cobolt now works seamlessly with Git repositories! You can use Cobolt's beautiful CLI on your existing Git projects.

## How It Works

Cobolt automatically detects whether you're in a **Git repository** or a **Cobolt repository** and adapts:

- In a **Git repo** (`.git` exists): Cobolt commands work directly with Git
- In a **Cobolt repo** (`.cobolt` exists): Cobolt uses its own storage
- **Git takes priority**: If both exist, Cobolt uses Git

## Git-Enabled Commands

These commands work with Git repositories:

### ✅ Fully Integrated
- `cobolt add` → `git add`
- `cobolt commit` → `git commit`
- `cobolt status` → `git status` (with Cobolt's pretty output)
- `cobolt pull` → `git pull`
- `cobolt push` → `git push`

### 🔄 Coming Soon
- `cobolt log` → `git log`
- `cobolt branch` → `git branch`
- `cobolt checkout` → `git checkout`
- `cobolt merge` → `git merge`

## Example Workflow

```bash
# Clone a Git repository
git clone https://github.com/user/repo.git
cd repo

# Use Cobolt for daily workflow with better UX
cobolt status          # Beautiful colored status
cobolt add .           # ✓ Added files with progress
cobolt commit -m "Fix" # Creates a Git commit
cobolt push            # Pushes to Git remote

# Your commits are real Git commits!
git log  # Shows commits made with Cobolt
```

## Advantages

### Why use Cobolt on Git repos?

1. **Better Visual Feedback**
   ```
   ✓ Added 5 file(s) to staging area    # vs git's silence
   ```

2. **Colored Output**
   - Green for success
   - Red for errors  
   - Yellow for warnings
   - Cyan for info

3. **Progress Indicators**
   ```
   ⟳ Pulling from origin...
   ✓ Successfully pulled from origin
   ```

4. **Clear Status Display**
   - Staged files in green with `+`
   - Modified files in yellow with `~`
   - Untracked files dimmed

5. **ASCII Banner**
   ```
      ____      _           _ _   
     / ___|___ | |__   ___ | | |_ 
    | |   / _ \| '_ \ / _ \| | __|
    | |__| (_) | |_) | (_) | | |_ 
     \____\___/|_.__/ \___/|_|\__|
   ```

## Installation for Git Users

```bash
# Install Cobolt
sudo cp cobolt /usr/local/bin/
sudo cp target/cobolt.jar /usr/local/bin/

# Use on any Git repository
cd ~/my-git-project
cobolt status
cobolt add .
cobolt commit -m "Updated docs"
cobolt push
```

## Compatibility

✅ **100% Compatible** - Cobolt creates real Git commits  
✅ **Works with GitHub/GitLab** - Push/pull to any Git remote  
✅ **Team-friendly** - Your team doesn't need Cobolt  
✅ **No migration needed** - Use on existing Git repos immediately  

## Under the Hood

Cobolt uses **JGit** (the Java Git implementation) to:
- Read/write the Git object database
- Create Git commits with proper formatting
- Interact with Git remotes
- Maintain full Git compatibility

Your repository stays a standard Git repository - Cobolt is just a better interface!

## Future Enhancements

Planned features:
- Interactive rebase with Cobolt's UI
- Better merge conflict resolution
- Branch visualization
- Commit graph display
- Smart commit message templates

## Pure Cobolt Mode

If you prefer Cobolt's own storage (not Git):

```bash
cobolt init  # Creates .cobolt/ (no Git)
cobolt add file.txt
cobolt commit -m "Message"
# Works independently of Git
```

This is useful for:
- Learning VCS internals
- Private projects
- Experimenting with VCS features
