# Cobolt Version Control System

A modern version control system written in Java with improved CLI aesthetics and **full Git integration**.

## 🎯 Key Features

- ✅ **Git Compatible**: Use Cobolt on existing Git repositories
- ✅ **Beautiful CLI**: Colored output with Unicode symbols (✓, ✗, ⚠, ℹ)
- ✅ **Core VCS Operations**: init, add, commit, status, log, branch, checkout
<<<<<<< HEAD
- ✅ **Tag Management**: Create and manage version tags
- ✅ **Push/Pull**: Works with GitHub, GitLab, and any Git remote
- 🚧 **Advanced Merge**: Foundation for intelligent conflict resolution
- 🚧 **Diff Engine**: Syntax-aware diff visualization (coming soon)
=======
- ✅ **Rich CLI Output**: Colored terminal output with Unicode symbols
- ✅ **Git-like Architecture**: Blob, tree, and commit objects with SHA-1 hashing
- ✅ **Branch Management**: Create, list, delete, and switch branches
>>>>>>> 6e49c36af0984741e594433e2913811094ceea69

## Quick Start

### Using with Git (Recommended)

```bash
# Use Cobolt on any existing Git repository
cd ~/my-git-project

# Beautiful status output
cobolt status

# Add files with visual feedback
cobolt add .

# Commit with Cobolt's UX
cobolt commit -m "Updated features"

# Push to remote
cobolt push origin main

# Your commits are real Git commits!
git log
```

### Pure Cobolt Mode

```bash
# Build the project
mvn clean package

# Initialize a Cobolt-only repository
java -jar target/cobolt.jar init

# Add files
java -jar target/cobolt.jar add .

# Commit changes
java -jar target/cobolt.jar commit -m "Initial commit"

# View history
java -jar target/cobolt.jar log

# Create a branch
java -jar target/cobolt.jar branch feature
```

## Installation

```bash
# Copy to bin directory
cp target/cobolt.jar ~/bin/

# Add alias to ~/.bashrc
echo 'alias cobolt="java -jar ~/bin/cobolt.jar"' >> ~/.bashrc

# Use directly
cobolt init
```

## Commands

### Repository Management
- `cobolt init [directory]` - Initialize a new repository
- `cobolt status` - Show working tree status

### Working with Files
- `cobolt add <files...>` - Add files to staging area
- `cobolt add .` - Add all files
- `cobolt commit -m "<message>"` - Create a commit

### History and Branches
- `cobolt log` - Show commit history
- `cobolt log --oneline` - Compact log format
- `cobolt branch` - List branches
- `cobolt branch <name>` - Create new branch
- `cobolt branch -d <name>` - Delete branch
- `cobolt checkpoint <branch>` - Switch branches

### Coming Soon
- `cobolt diff` - Show file changes with syntax highlighting
- `cobolt merge <branch>` - Intelligent merge with interactive conflict resolution


### Core Components

## Requirements

- Java 17 or higher
- Maven 3.6+ (for building)

## Dependencies

- picocli 4.7.5 - CLI framework
- jansi 2.4.1 - Terminal colors
- commons-codec 1.16.0 - Hashing
- java-diff-utils 4.12 - Diff algorithms
- JUnit 5.10.1 - Testing

## Building from Source

```bash
# Clone repository
cd cobolt

# Compile
mvn clean compile

# Run tests
mvn test

# Package executable JAR
mvn package
```

## Why Cobolt?

### Better CLI Experience
- **Visual Clarity**: Color-coded output for different file states
- **Informative Symbols**: ✓, ✗, ⚠, ℹ for instant recognition
- **Clean Formatting**: Organized sections and tables

### Git Comparison

| Feature | Git | Cobolt |
|---------|-----|---------|
| Success messages | Plain text | ✓ + green color |
| Errors | Red text | ✗ + red + helpful hints |
| Status | Basic | Categorized + colorized |
| Branch list | Simple | Current highlighted |

### Future: Smarter Merges
Cobolt is designed from the ground up to support:
- Interactive 3-way conflict resolution UI
- Syntax-aware conflict detection
- Smart auto-resolution for simple conflicts
- Multiple merge strategies

## Contributing

Core areas for contribution:

1. **Diff Engine**: Implement Myers algorithm in `DiffEngine.java`
2. **Merge System**: Build interactive conflict resolver in `MergeEngine.java`
3. **Remote Support**: Add push/pull functionality
4. **Performance**: Optimize object storage and retrieval

## License

MIT License - Feel free to learn from and extend this code.
