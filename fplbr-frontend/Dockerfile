# ---------- build ----------
FROM node:20-alpine AS deps
WORKDIR /app

# Install dependencies first for better caching
COPY package.json package-lock.json ./
RUN npm install --only=production

# Copy the rest of the application
COPY . .

# Set the BFF URL for the build
ARG VITE_BFF_URL=http://bff:3001
ENV VITE_BFF_URL=${VITE_BFF_URL}

# Build the application
RUN npm run build

# ---------- serve ----------
FROM nginx:alpine

# Copy the built app from the build stage
COPY --from=deps /app/dist /usr/share/nginx/html

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
