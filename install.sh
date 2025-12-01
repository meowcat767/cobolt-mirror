#!/bin/bash
# Cobolt Automatic Installation Script

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
JAR_PATH="$SCRIPT_DIR/target/cobolt.jar"
WRAPPER_PATH="$SCRIPT_DIR/cobolt"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔧 Installing Cobolt VCS${NC}"
echo ""

# Check if JAR exists
if [ ! -f "$JAR_PATH" ]; then
    echo -e "${YELLOW}⚠ cobolt.jar not found. Building...${NC}"
    mvn clean package -DskipTests
fi

# Determine installation directory
if [ -w "/usr/local/bin" ] && [ -d "/usr/local/bin" ]; then
    INSTALL_DIR="/usr/local/bin"
    NEEDS_SUDO=""
elif [ "$EUID" -eq 0 ]; then
    INSTALL_DIR="/usr/local/bin"
    NEEDS_SUDO=""
else
    # Use user's bin directory
    INSTALL_DIR="$HOME/bin"
    NEEDS_SUDO=""
    
    # Create if doesn't exist
    mkdir -p "$INSTALL_DIR"
fi

echo -e "📦 Installing to: ${GREEN}$INSTALL_DIR${NC}"

# Copy files
if [ -n "$NEEDS_SUDO" ]; then
    sudo cp "$JAR_PATH" "$INSTALL_DIR/"
    sudo cp "$WRAPPER_PATH" "$INSTALL_DIR/"
    sudo chmod +x "$INSTALL_DIR/cobolt"
else
    cp "$JAR_PATH" "$INSTALL_DIR/"
    cp "$WRAPPER_PATH" "$INSTALL_DIR/"
    chmod +x "$INSTALL_DIR/cobolt"
fi

echo -e "${GREEN}✓${NC} Installed cobolt wrapper"
echo -e "${GREEN}✓${NC} Installed cobolt.jar"

# Update PATH if necessary
if [[ ":$PATH:" != *":$INSTALL_DIR:"* ]]; then
    echo ""
    echo -e "${YELLOW}⚠ $INSTALL_DIR is not in your PATH${NC}"
    
    # Detect shell
    if [ -n "$BASH_VERSION" ]; then
        RC_FILE="$HOME/.bashrc"
    elif [ -n "$ZSH_VERSION" ]; then
        RC_FILE="$HOME/.zshrc"
    else
        RC_FILE="$HOME/.profile"
    fi
    
    echo ""
    echo -e "Add the following to your ${BLUE}$RC_FILE${NC}:"
    echo ""
    echo -e "  ${GREEN}export PATH=\"\$PATH:$INSTALL_DIR\"${NC}"
    echo ""
    
    # Offer to add automatically
    read -p "Add to $RC_FILE automatically? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        if ! grep -q "export PATH=.*$INSTALL_DIR" "$RC_FILE" 2>/dev/null; then
            echo "" >> "$RC_FILE"
            echo "# Cobolt VCS" >> "$RC_FILE"
            echo "export PATH=\"\$PATH:$INSTALL_DIR\"" >> "$RC_FILE"
            echo -e "${GREEN}✓${NC} Added to $RC_FILE"
            echo -e "${YELLOW}⚠${NC} Run: ${BLUE}source $RC_FILE${NC} or restart your terminal"
        else
            echo -e "${GREEN}✓${NC} Already in $RC_FILE"
        fi
    fi
else
    echo -e "${GREEN}✓${NC} $INSTALL_DIR is already in PATH"
fi

echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✓ Installation complete!${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "Try: ${BLUE}cobolt --help${NC}"
echo ""
