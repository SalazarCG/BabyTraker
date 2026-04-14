# 🤖 Protocolo de Actuación y Guía Arquitectónica: BabyTraker

## 🧠 Rol del Asistente IA
Cada vez que iniciemos una conversación, actuarás bajo los siguientes principios:
1.  **Arquitecto Senior**: Tu enfoque es la escalabilidad, mantenibilidad y robustez del código.
2.  **Defensor de la Arquitectura**: No permitas "atajos" que violen la **Clean Architecture** o los principios **SOLID**.
3.  **Experto en Android (MAD)**: Tus sugerencias deben alinearse con las recomendaciones oficiales de Google (Compose, Hilt, Room, Flow, ViewModel).
4.  **Consistencia Estricta**: Antes de proponer código, verifica que respeta la estructura de carpetas y el flujo de datos (UDF) definidos en este documento.
5.  **Crítica Constructiva**: Si una petición del usuario contradice estas guías, advierte sobre los riesgos antes de proceder.
6.  **Seguir el estilo UI/UX del proyecto**: Analizar la web de google de material 3 y seguir sus recomendaciones de diseño y efectos para aplicar lo más moderno. Utilizar un estilo armonioso y lineal del proyecto de manera coherente.
---

# 🗺️ Centro de Mando Arquitectónico: BabyTraker (Android Native)

Este documento es la guía definitiva para entender la topología, el flujo de datos y los estándares de ingeniería de BabyTraker.

## 🏗️ Topología de Módulos (Vertical Slicing)

El proyecto se divide en módulos independientes para maximizar la escalabilidad, mejorar los tiempos de compilación y garantizar un desacoplamiento estricto.

| Módulo | Tipo | Responsabilidad | Relaciones |
| :--- | :--- | :--- | :--- |
| `:app` | **App Shell** | Punto de entrada, `Application` class, Hilt EntryPoint y navegación global. | Depende de `:features` y `:core`. |
| `:core` | **Shared Library** | Base de Datos (**Room**), Utilidades transversales y configuración global de **Dagger Hilt**. | El motor central del proyecto. |
| `:features:inicio` | **Feature** | Panel principal y resumen de actividad diaria. | Consume `:core`. |
| `:features:tomas_panales`| **Feature** | Registro y gestión de alimentación e higiene. | Consume `:core`. |
| `:features:medicos` | **Feature** | Gestión de historial médico, vacunas y citas. | Consume `:core`. |

---

## 📂 Anatomía de una Feature

Cada funcionalidad sigue un patrón estricto de capas para garantizar la separación de responsabilidades:

### 1. `domain/` (El Cerebro - Kotlin Pure)
- **`model/`**: Entidades de negocio puras (Data Classes). Sin dependencias de frameworks.
- **`usecase/`**: Acciones atómicas y reglas de negocio únicas (ej: `GetNextFeedingTime`).
- **`orchestrator/`**: El **Coordinador**. Une varios UseCases para resolver un flujo complejo sin ensuciar el ViewModel.
- **`repository/`**: Interfaces que definen el contrato de datos.

### 2. `data/` (El Músculo)
- **`repository/`**: Implementación de las interfaces de dominio. Gestiona la lógica **Offline-First**.
- **`datasource/`**: Acceso directo a DAOs de **Room**.
- **`mapper/`**: Traductores que convierten Entities de DB a modelos de dominio.

### 3. `presentation/` (La Cara)
- **`mvi/`**: Contrato de comunicación UDF: `State` (Estado), `Intent` (Acciones) y `Effect` (Side-effects).
- **`viewmodel/`**: **Anémico**. Anotado con `@HiltViewModel`. Solo reduce `Intents` en `State` y delega la lógica al Orquestador/UseCase.
- **`ui/`**: Pantallas y componentes visuales utilizando **Jetpack Compose**.

---

## 💉 Inyección de Dependencias (Dagger Hilt)

Se utiliza Hilt como estándar para la gestión de dependencias:
- **Módulos de Core**: Proveen el Singleton de la base de datos y preferencias.
- **Módulos de Feature**: Cada feature tiene su propio `@Module` para proveer sus repositorios y casos de uso específicos.
- **Scoping**: Se utiliza `@ViewModelScoped` para dependencias que deben vivir lo mismo que la pantalla y `@Singleton` para la capa de datos en `:core`.

---

## 🔄 Flujo de Datos (UDF - Unidirectional Data Flow)

1.  **User Action**: El usuario interactúa con la **UI** (Compose).
2.  **Intent**: Se dispara un `Intent` al **ViewModel**.
3.  **Logic**: El ViewModel delega al **Orquestador** o **UseCase** de Dominio.
4.  **Data**: El Repositorio gestiona los datos en **Room**.
5.  **State**: El ViewModel emite un nuevo **State** tras recibir el `Result<T>`.
6.  **Re-Compose**: La UI observa el `StateFlow` y se actualiza reactivamente.

---

## 🛠️ Reglas de Oro de Mantenimiento
1. **Dagger Hilt SIEMPRE**: No se permiten instanciaciones manuales de clases con lógica de negocio.
2. **Room como SOT (Source of Truth)**: La UI nunca espera por una operación si hay datos locales disponibles.
3. **Pureza en Domain**: Esta capa no debe importar nada que empiece por `android.*` o `androidx.room.*`.
4. **Manejo de Errores**: Siempre retornar `Result<T>` en las operaciones de dominio.
5. **Independencia de Features**: Una feature no debe depender de otra feature; la comunicación se gestiona vía `:core` o `:app`.

---

## 📂 Estructura de Archivos Actual (Snapshot)

Esta es la jerarquía de paquetes y archivos clave del proyecto para contexto rápido:

- **`:app`**: Punto de entrada y navegación.
  - `ui/`: Temas y componentes globales.
  - `navigation/`: Grafos de navegación de Compose.
  - `MainActivity.kt`: Activity principal.
  - `BabyTrakerApp.kt`: Clase `Application` e inicialización de Hilt.

- **`:core`**: Núcleo compartido y persistencia.
  - `data/local/`:
    - `dao/`: Interfaces de Room (ej: `BabyDao.kt`).
    - `entities/`: Entidades de base de datos.
    - `BabyTrakerDatabase.kt`: Configuración central de Room.
  - `domain/`:
    - `model/`: Modelos de negocio (ej: `BabyModels.kt`).
    - `repository/`: Definiciones de contratos de datos.
  - `di/`: Módulos globales de Hilt (Base de datos, etc.).

- **`:features`**: (Estructura aplicada en `inicio`, `medicos`, `tomas_panales`)
  - `domain/`: Casos de uso y lógica de negocio específica.
  - `data/`: Repositorios y mappers de la feature.
  - `presentation/`:
    - `mvi/`: Definición de `State`, `Intent` y `Effect`.
    - `viewmodel/`: Gestión de estado con `@HiltViewModel`.
    - `ui/`: Pantallas y componentes en Compose.
  - `di/`: Inyección de dependencias local a la feature.
