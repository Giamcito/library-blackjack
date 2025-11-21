###############################
# Multi-stage optimized build #
###############################

# Etapa deps: instala (dev + prod) para caché
FROM node:20-bookworm-slim AS deps
WORKDIR /app
ENV NEXT_TELEMETRY_DISABLED=1
COPY package.json pnpm-lock.yaml* ./
RUN corepack enable && \
	if [ -f pnpm-lock.yaml ]; then pnpm install --frozen-lockfile --shamefully-hoist; \
	else echo 'pnpm-lock.yaml ausente, instalando sin --frozen-lockfile'; pnpm install --shamefully-hoist; fi

# Etapa builder: copia fuentes, build y luego prune a prod
FROM node:20-bookworm-slim AS builder
WORKDIR /app
ENV NEXT_TELEMETRY_DISABLED=1
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN ls -ld node_modules/next || (echo 'next package missing after install' && exit 1)
RUN ls node_modules/next/dist/bin || (echo 'next dist/bin missing' && exit 1)
RUN pnpm build
RUN pnpm prune --prod && rm -rf .pnpm-store* || true

# Etapa runner: sólo artefactos + deps de producción
FROM node:20-bookworm-slim AS runner
WORKDIR /app
ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/package.json ./package.json
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/start-with-fallback.js ./start-with-fallback.js

EXPOSE 3000 3001 3002
CMD ["node", "start-with-fallback.js"]
