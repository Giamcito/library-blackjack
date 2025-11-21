# library-blackjack & Paginación de Memoria

Este repositorio combina:

1. Una librería en C para conteo de cartas (Blackjack).
2. Una aplicación Next.js que simula algoritmos de reemplazo de páginas (FIFO, LRU, Óptimo) con ejecución local o remota vía microservicio Java.

---

## Librería Blackjack (C)
Librería hecha en C para conteo de cartas (origen: rama `paginacion` remota previa). Se mantiene la historia original y se añade ahora la parte de simulación de memoria virtual.

---

## Paginación & Algoritmos de Reemplazo (Next.js)

Aplicación Next.js que simula los algoritmos FIFO, LRU y Óptimo de reemplazo de páginas. Puede ejecutarse con implementación local en TypeScript o delegando el cálculo a un microservicio Java (Spring Boot) vía Docker. Incluye mecanismos de resiliencia (fallback local) y selección dinámica de puertos para evitar colisiones en entornos compartidos.

## Modos de ejecución

1. Local (sin backend externo): Las rutas `/api/fifo`, `/api/lru`, `/api/optimo` usan funciones de `lib/algorithms.ts`. Más rápido y sin red.
2. Remoto (microservicio Java): Las rutas hacen `fetch` al servicio `algoritmos-service`. Útil si quieres comparar rendimiento nativo / Java.

Control: variable de entorno `LOCAL_ALGOS` (`true` para local, `false` para remoto). URL del backend: `MICROSERVICE_URL`.

Si el modo remoto falla (timeouts o conexión rechazada) se aplica automáticamente un fallback al cálculo local para no romper la experiencia del usuario.

## Fallback remoto y mapeo flexible
El helper `lib/remote.ts` intenta contactar el backend en una secuencia de puertos configurados (por defecto `8081,8080,8082`). Hace fetch con timeout breve; si todos fallan retorna el resultado local. Además normaliza respuestas con distintos nombres de campos (`fallos|faults|misses`, `tabla|pageTable|frames`, `pasos|steps`). Si el backend no envía pasos detallados se generan localmente.

## Selección dinámica de puertos Frontend
El script `start-with-fallback.js` prueba una lista de puertos (`3000,3001,3002` por defecto vía `FRONTEND_PORTS`) y ejecuta `next start` en el primero libre. Esto evita errores cuando el puerto 3000 está ocupado.

## Validación de referencias
La cadena de referencias ahora sólo acepta dígitos (0-9). Se permiten espacios que se eliminan antes de procesar.

## Desarrollo rápido

```powershell
pnpm install
pnpm dev
```

## Docker Compose

Requiere Docker y Docker Compose.

```powershell
docker compose build
docker compose up -d
```

Servicios:
- `algoritmos-service`: Spring Boot (puerto principal 8081; fallback 8080/8082).
- `frontend`: Next.js en primer puerto libre (3000/3001/3002).

Variables clave:
- `LOCAL_ALGOS=true|false` define fuente de cálculo inicial.
- `MICROSERVICE_URL` apunta al endpoint Java (ej: `http://algoritmos-service:8081/api/algoritmos/pagereplacement`).
- `FRONTEND_PORTS=3000,3001,3002` lista de prueba para el script de arranque.

El contenedor del frontend expone los puertos listados para facilitar el binding externo.

## Cambiar a modo local en runtime
Puedes definir `.env.local` en `paginacion/`:

```
LOCAL_ALGOS=true
MICROSERVICE_URL=http://algoritmos-service:8081/api/algoritmos/pagereplacement
FRONTEND_PORTS=3000,3001,3002
```

## Construcción de producción manual (sin Docker)

```powershell
pnpm build
pnpm start
```

## Estructura relevante

```
algoritmos-service/     # Backend Java (Spring Boot)
paginacion/             # Frontend Next.js
	lib/algorithms.ts     # Implementaciones FIFO/LRU/Óptimo en TS
	app/api/*/route.ts    # Rutas híbridas (local vs remoto)
```

## Página Blackjack Educativo
El botón en la página principal abre la aplicación de Blackjack educativo en una nueva pestaña (`http://18.221.196.116:3000/`) usando `window.open(..., "_blank")` con `noopener,noreferrer` para seguridad.

## Próximos pasos sugeridos
- Añadir pruebas unitarias para `lib/algorithms.ts` (incluyendo edge cases de memoria llena y referencias repetidas).
- Métricas (hit rate) similares a backend Java.
- Parametrizar lista de puertos backend vía variable en lugar de array fija.
- Pipeline CI para build + tests.

