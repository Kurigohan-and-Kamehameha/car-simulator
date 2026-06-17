# Build stage
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm install
COPY . .
RUN npm run build

# Serve stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
RUN printf "server { \n\
    listen 80; \n\
    location / { \n\
    root /usr/share/nginx/html; \n\
    index index.html index.htm; \n\
    try_files \$uri \$uri/ /index.html; \n\
    } \n\
    location /api/ { \n\
    proxy_pass http://backend:8080/api/; \n\
    proxy_set_header Host \$host; \n\
    proxy_set_header X-Real-IP \$remote_addr; \n\
    } \n\
    location /ws/ { \n\
    proxy_pass http://backend:8080/ws/; \n\
    proxy_http_version 1.1; \n\
    proxy_set_header Upgrade \$http_upgrade; \n\
    proxy_set_header Connection \"upgrade\"; \n\
    proxy_set_header Host \$host; \n\
    } \n\
    location /ws-native/ { \n\
    proxy_pass http://backend:8080/ws-native/; \n\
    proxy_http_version 1.1; \n\
    proxy_set_header Upgrade \$http_upgrade; \n\
    proxy_set_header Connection \"upgrade\"; \n\
    proxy_set_header Host \$host; \n\
    } \n\
    }" > /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
