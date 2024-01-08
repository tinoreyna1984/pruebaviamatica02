# Etapa de construcción
FROM node:latest AS builder

WORKDIR /app
COPY package*.json ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build

# Etapa de producción
FROM nginx:latest

COPY --from=builder /app/dist/frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 82

CMD ["nginx", "-g", "daemon off;"]
