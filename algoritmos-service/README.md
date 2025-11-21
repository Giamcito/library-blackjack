# 🖥️ Microservicio de Algoritmos de Reemplazo de Páginas

> Servicio REST completo que implementa los tres algoritmos clásicos de reemplazo de páginas en memoria

## ✨ Características

- ✅ **FIFO** (First In First Out) - Reemplaza página más antigua
- ✅ **LRU** (Least Recently Used) - Reemplaza página menos reciente
- ✅ **ÓPTIMO** - Reemplaza página usada más lejana en el futuro
- ✅ Implementación 100% Java (sin dependencias nativas)
- ✅ REST API con Spring Boot 3.5.7
- ✅ Validación robusta y manejo de errores
- ✅ Cálculo automático de hit rate
- ✅ Tabla de seguimiento de estado de memoria

## 🚀 Inicio Rápido

### 1. Compilar
```powershell
mvn clean package -DskipTests
```

### 2. Iniciar Servidor
```powershell
# Opción A: Script PowerShell
.\start-server.ps1

# Opción B: Maven
mvn spring-boot:run

# Opción C: Java directo
java -jar target/algoritmos-0.0.1-SNAPSHOT.jar
```

**Servidor en:** `http://localhost:8081`

### 3. Hacer una Solicitud
```powershell
$payload = @{ referencias = "7045679"; marcos = 3 } | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8081/api/algoritmos/pagereplacement/fifo" `
  -Method Post -ContentType "application/json" -Body $payload
```

## 📚 API REST

### Health Check
```
GET /api/algoritmos/pagereplacement/health
```

### Información
```
GET /api/algoritmos/pagereplacement/info
```

### FIFO Algorithm
```
POST /api/algoritmos/pagereplacement/fifo
Content-Type: application/json

{ "referencias": "7045679", "marcos": 3 }
```

### LRU Algorithm
```
POST /api/algoritmos/pagereplacement/lru
Content-Type: application/json

{ "referencias": "7045679", "marcos": 3 }
```

### ÓPTIMO Algorithm
```
POST /api/algoritmos/pagereplacement/optimo
Content-Type: application/json

{ "referencias": "7045679", "marcos": 3 }
```

## 📊 Ejemplo de Respuesta

```json
{
  "misses": 5,
  "steps": 7,
  "frames": 3,
  "hitRate": 28.57,
  "pageTable": [
    [7, -1, -1],
    [7, 0, -1],
    [7, 0, 4],
    [5, 0, 4],
    [5, 6, 4],
    [5, 6, 7],
    [5, 6, 9]
  ]
}
```

## 🧪 Pruebas

### Script de Pruebas Interactivo
```powershell
.\test-microservicio.ps1
```

### Tests Unitarios
```powershell
mvn test
```

## 📋 Parámetros

| Parámetro | Tipo | Validación | Ejemplo |
|-----------|------|-----------|---------|
| `referencias` | String | Dígitos 0-9, no vacío | "7045679" |
| `marcos` | Integer | Entre 1 y 10 | 3 |

## 📁 Estructura del Proyecto

```
algoritmos-service/
├── src/main/java/com/service/algoritmos/
│   ├── AlgoritmosServiceApplication.java
│   ├── delivery/
│   │   └── rest/
│   │       ├── PageReplacementController.java
│   │       └── dto/PageReplacementRequest.java
│   └── domain/
│       ├── model/PageReplacementResult.java
│       └── service/
│           ├── PageReplacementAlgorithm.java
│           └── AlgoritmosService.java
├── pom.xml
├── DOCUMENTACION_FINAL.md
├── start-server.ps1
├── test-microservicio.ps1
└── README.md (este archivo)
```

## ⚙️ Configuración

Editar `src/main/resources/application.properties`:
```properties
server.port=8081
spring.application.name=algoritmos-service
logging.level.root=INFO
logging.level.com.service.algoritmos=DEBUG
```

## 🔨 Requisitos

- **Java:** 17 o superior
- **Maven:** 3.8 o superior
- **Git:** (opcional)

No se requieren compiladores C ni dependencias nativas.

## 🛠️ Compilación Avanzada

### Limpiar y recompilar
```powershell
mvn clean
mvn compile
```

### Con todos los tests
```powershell
mvn clean verify
```

### Generar JAR ejecutable
```powershell
mvn clean package
```

### Instalar en repositorio local
```powershell
mvn install
```

## 📖 Documentación Adicional

- **[DOCUMENTACION_FINAL.md](./DOCUMENTACION_FINAL.md)** - Guía completa y referencia API
- **[COMPILACION_JNI.md](./COMPILACION_JNI.md)** - Para compilar librería JNI (opcional)

## 🎯 Significado de Resultados

- **misses**: Número de fallos de página (cache misses)
- **steps**: Total de referencias de página procesadas
- **frames**: Número de marcos de memoria disponibles
- **hitRate**: Porcentaje de aciertos = `(steps - misses) / steps * 100`
- **pageTable**: Estado de la memoria después de cada referencia
  - `-1` = Marco vacío
  - `0-9` = Número de página cargada

## 🐛 Troubleshooting

### "Puerto 8081 ya está en uso"
Cambiar puerto en `application.properties`:
```properties
server.port=8082
```

### "Servidor no responde"
Verificar que Java está ejecutándose:
```powershell
Get-Process java
```

### "Conexión rechazada"
Esperar 5 segundos después de iniciar (tiempo de arranque de Tomcat).

### Limpiar compilación anterior
```powershell
mvn clean
mvn package -DskipTests
```

## 📊 Casos de Uso

### Prueba con diferentes referencias
```powershell
# Caso 1: Secuencia repetitiva
.\test-microservicio.ps1 -Referencias "123123123" -Marcos 3

# Caso 2: Sin repetición
.\test-microservicio.ps1 -Referencias "0123456789" -Marcos 5

# Caso 3: Muchos marcos
.\test-microservicio.ps1 -Referencias "7045679" -Marcos 10
```

## 🔄 Comparativa de Algoritmos

Para la misma secuencia, típicamente:
- **ÓPTIMO** ≤ **LRU** ≤ **FIFO**
- El ÓPTIMO tiene menor o igual número de fallos

## 📱 Integración con Otros Servicios

### Python
```python
import requests

response = requests.post(
    'http://localhost:8081/api/algoritmos/pagereplacement/fifo',
    json={'referencias': '7045679', 'marcos': 3}
)
print(response.json())
```

### JavaScript/Node.js
```javascript
fetch('http://localhost:8081/api/algoritmos/pagereplacement/lru', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ referencias: '7045679', marcos: 3 })
})
.then(r => r.json())
.then(data => console.log(data))
```

### cURL
```bash
curl -X POST http://localhost:8081/api/algoritmos/pagereplacement/optimo \
  -H "Content-Type: application/json" \
  -d '{"referencias":"7045679","marcos":3}'
```

## 📝 Notas Técnicas

- **Algoritmo ÓPTIMO:** Es teórico - en la práctica requiere conocer todas las referencias futuras
- **Thread-safe:** El servicio es stateless y thread-safe
- **Performance:** Optimizado para secuencias hasta 100 referencias
- **Portabilidad:** Funciona en Windows, Linux y macOS sin cambios

## 🚀 Despliegue

### En Docker (ejemplo)
```dockerfile
FROM openjdk:17-slim
COPY target/algoritmos-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8081
```

### En Linux (systemd)
```ini
[Unit]
Description=Algoritmos Service
After=network.target

[Service]
Type=simple
ExecStart=/usr/bin/java -jar /opt/algoritmos/algoritmos-0.0.1-SNAPSHOT.jar
WorkingDirectory=/opt/algoritmos
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

## 🤝 Contribuciones

Este proyecto fue creado como microservicio para algoritmos de sistemas operativos.

## 📄 Licencia

Sin restricciones de uso.

---

**Estado:** ✅ **Producción**  
**Versión:** 0.0.1-SNAPSHOT  
**Java:** 17+  
**Spring Boot:** 3.5.7  
**Última actualización:** 2025-11-18
