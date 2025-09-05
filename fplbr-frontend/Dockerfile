# ---------- build ----------
FROM node:20-alpine AS deps
WORKDIR /app

# Build stage
FROM node:18-alpine as build
WORKDIR /app

# Set the BFF URL for the build
ARG VITE_BFF_URL=/api
ENV VITE_BFF_URL=${VITE_BFF_URL}

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy source code and build
COPY . .
RUN npm run build

# Production stage with Nginx
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Copy built files from build stage
COPY --from=build /app/dist ./

# Configure Nginx
RUN printf 'server {\n\
  listen 80;\n\
  server_name _;\n\
  root /usr/share/nginx/html;\n\
  location / {\n\
    try_files $uri $uri/ /index.html;\n\
  }\n\
  location /api/ {\n\
    proxy_pass http://bff:3001/;\n\
    proxy_set_header Host $host;\n\
    proxy_set_header X-Real-IP $remote_addr;\n\
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;\n\
    proxy_set_header X-Forwarded-Proto $scheme;\n\
  }\n\
  location /assets/ {\n\
    access_log off;\n\
    expires 1y;\n\
    add_header Cache-Control "public";\n\
  }\n\
}\n' > /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
