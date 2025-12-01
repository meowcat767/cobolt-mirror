# Quick Installation Guide

## Automatic Installation (Recommended)

Run the install script after building:

```bash
# Build and install automatically
mvn clean install

# Or build first, then install
mvn clean package
./install.sh
```

The script will:
- ✅ Install `cobolt` to `~/bin` or `/usr/local/bin`
- ✅ Copy the JAR file
- ✅ Make the wrapper executable
- ✅ Optionally update your PATH

## Manual Installation

### Option 1: User Installation (~/bin)

```bash
# Create bin directory if needed
mkdir -p ~/bin

# Copy files
cp cobolt ~/bin/
cp target/cobolt.jar ~/bin/
chmod +x ~/bin/cobolt

# Add to PATH (if not already there)
echo 'export PATH="$PATH:$HOME/bin"' >> ~/.bashrc
source ~/.bashrc
```

### Option 2: System-wide Installation

```bash
# Requires sudo
sudo cp cobolt /usr/local/bin/
sudo cp target/cobolt.jar /usr/local/bin/
sudo chmod +x /usr/local/bin/cobolt
```

## Auto-Update After Build

### Method 1: Use Maven Install Phase

Just use `mvn install` instead of `mvn package`:

```bash
mvn clean install
# Automatically runs install.sh after build!
```

### Method 2: Add Alias

Add to your `~/.bashrc` or `~/.zshrc`:

```bash
alias cobolt-update='cd /home/deck/.gemini/antigravity/scratch/cobolt && mvn clean install'
```

Then just run:
```bash
cobolt-update
```

### Method 3: Git Hook

Create `.git/hooks/post-merge`:

```bash
#!/bin/bash
echo "Rebuilding and installing Cobolt..."
mvn clean install
```

```bash
chmod +x .git/hooks/post-merge
```

Now after `git pull`, Cobolt automatically rebuilds and installs!

## Verify Installation

```bash
# Check if cobolt is in PATH
which cobolt

# Test it
cobolt --version
cobolt --help
```

## Updating Cobolt

Whenever you make code changes:

```bash
# Quick update
mvn clean install

# Or manual
mvn clean package
./install.sh
```

## Uninstall

```bash
# If installed to ~/bin
rm ~/bin/cobolt ~/bin/cobolt.jar

# If installed to /usr/local/bin
sudo rm /usr/local/bin/cobolt /usr/local/bin/cobolt.jar

# Remove from PATH (edit ~/.bashrc or ~/.zshrc)
# Remove the line: export PATH="$PATH:$HOME/bin"
```

## Troubleshooting

**"cobolt: command not found"**
- Run `source ~/.bashrc` or restart terminal
- Check PATH: `echo $PATH`
- Verify installation: `ls ~/bin/cobolt`

**"Permission denied"**
- Make sure script is executable: `chmod +x ~/bin/cobolt`
- Or use sudo for system-wide install

**JAR not found**
- Build first: `mvn clean package`
- Check: `ls target/cobolt.jar`
