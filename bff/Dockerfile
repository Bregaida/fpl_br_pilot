FROM node:20-alpine AS build
WORKDIR /app

# Install build dependencies
RUN apk add --no-cache python3 make g++

# Copy package files
COPY package*.json ./

# Install all dependencies including devDependencies
RUN npm install

# Copy source files
COPY tsconfig*.json ./
COPY src ./src

# Install missing production dependencies
RUN npm install zod metar-taf-parser morgan redis

# Build the application
RUN npm run build

# Production stage
FROM node:20-alpine
WORKDIR /app

ENV NODE_ENV=production
ENV PORT=3001
ENV BACKEND_BASE_URL=http://backend:8080

# Copy package files
COPY package*.json ./

# Install only production dependencies
RUN npm install --omit=dev

# Install production dependencies that are needed at runtime
RUN npm install zod metar-taf-parser morgan redis

# Copy built files from build stage
COPY --from=build /app/dist ./dist

# Expose the application port
EXPOSE 3001

# Start the application
CMD ["node", "dist/index.js"]
