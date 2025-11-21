# Multi-stage build for Next.js app
FROM node:20-bookworm-slim AS builder
WORKDIR /app
COPY package.json ./
# Copia lockfile si existe (evita fallo si fue omitido)
COPY pnpm-lock.yaml* ./
RUN corepack enable && if [ -f pnpm-lock.yaml ]; then pnpm install --frozen-lockfile --shamefully-hoist; else echo 'pnpm-lock.yaml ausente, instalando sin --frozen-lockfile'; pnpm install --shamefully-hoist; fi
RUN ls -ld node_modules/next || (echo 'next package missing after install' && exit 1)
RUN ls node_modules/next/dist/bin || (echo 'next dist/bin missing' && exit 1)
COPY . .
ENV NEXT_TELEMETRY_DISABLED=1
RUN pnpm build

FROM node:20-bookworm-slim AS runner
WORKDIR /app
ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1
# Copy only necessary files
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/package.json ./package.json
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/start-with-fallback.js ./start-with-fallback.js

EXPOSE 3000 3001 3002
# Usa script que detecta y selecciona puerto libre
CMD ["node", "start-with-fallback.js"]
