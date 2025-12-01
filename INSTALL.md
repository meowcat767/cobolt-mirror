# Installing Cobolt as an Executable

There are several ways to make Cobolt easier to run:

## Option 1: Shell Script Wrapper (Linux/Mac) - Recommended ✅

A wrapper script `cobolt` has been created for you. Use it like this:

```bash
# Run from the cobolt directory
./cobolt init
./cobolt add .
./cobolt commit -m "Test"

# Or install it globally
sudo cp cobolt /usr/local/bin/
sudo cp target/cobolt.jar /usr/local/bin/

# Then use it anywhere
cobolt init
cobolt status
```

## Option 2: Add to PATH

```bash
# Add cobolt directory to your PATH
echo 'export PATH="$PATH:/home/deck/.gemini/antigravity/scratch/cobolt"' >> ~/.bashrc
source ~/.bashrc

# Now run from anywhere
cobolt --help
```

## Option 3: Create System-wide Alias

```bash
# Add to ~/.bashrc or ~/.zshrc
echo 'alias cobolt="java -jar /home/deck/.gemini/antigravity/scratch/cobolt/target/cobolt.jar"' >> ~/.bashrc
source ~/.bashrc

# Use anywhere
cobolt init
```

## Option 4: Copy to ~/bin

```bash
# Create bin directory if it doesn't exist
mkdir -p ~/bin

# Copy both files
cp cobolt ~/bin/
cp target/cobolt.jar ~/bin/

# Make sure ~/bin is in PATH (usually is by default)
cobolt --version
```

## Option 5: GraalVM Native Image (Advanced)

For a true native executable with faster startup:

```bash
# Install GraalVM native-image
# Then build native executable
native-image -jar target/cobolt.jar cobolt-native

# Results in a single binary with no JVM required
./cobolt-native --help
```

**Note**: This requires GraalVM and additional configuration.

## For Windows Users

Use the `cobolt.bat` script:

```batch
# Run from cobolt directory
cobolt.bat init
cobolt.bat status

# Or add to PATH
set PATH=%PATH%;C:\path\to\cobolt
cobolt init
```

## Recommended Setup

For daily use, I recommend **Option 1** (wrapper script in PATH):

```bash
# One-time setup
sudo cp /home/deck/.gemini/antigravity/scratch/cobolt/cobolt /usr/local/bin/
sudo cp /home/deck/.gemini/antigravity/scratch/cobolt/target/cobolt.jar /usr/local/bin/

# Now use cobolt anywhere
cd ~/my-project
cobolt init
cobolt add .
cobolt commit -m "Initial commit"
```

This gives you a clean `cobolt` command without typing `java -jar` every time!
