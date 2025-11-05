#!/bin/bash

# Build script for Farmacia DEY Microservices
# This script builds all Docker images for the microservices

set -e  # Exit on any error

echo "🏗️  Building Farmacia DEY Microservices..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Clean previous builds
print_status "Cleaning previous Maven builds..."
./mvnw clean -q

# Build all services
print_status "Building all microservices with Docker..."

# Array of services to build
services=("auth" "usuario" "producto" "metodopago" "compra" "appgw")

for service in "${services[@]}"; do
    print_status "Building $service service..."
    
    if docker build -t farmacia-$service:latest -f businessdomain/$service/Dockerfile .; then
        print_success "$service service built successfully"
    else
        print_error "Failed to build $service service"
        exit 1
    fi
done

print_success "All microservices built successfully! 🎉"

echo ""
echo "📝 Available Docker images:"
docker images | grep farmacia-

echo ""
echo "🚀 To start all services with PostgreSQL:"
echo "   docker-compose up -d"
echo ""
echo "🔍 To check logs:"
echo "   docker-compose logs -f [service-name]"
echo ""
echo "🛑 To stop all services:"
echo "   docker-compose down"