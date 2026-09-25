# Agregar ffmpeg / dav1d / IAMF / MPEG-H (lo que trae Just Player de más)

Este entorno no tiene acceso a internet, así que esta parte no pude
completarla yo. Son los módulos de Media3 que le dan a Just Player su
soporte extra de codecs, y **no son un dependency de Gradle normal**:
Google los distribuye como código fuente dentro del propio repo de
Media3, para compilarlos vos con el NDK. Por eso Just Player los trae
como archivos `.so` sueltos (`libffmpegJNI.so`, `libdav1dJNI.so`,
`libiamfJNI.so`, `libmpeghJNI.so`) en vez de una dependencia de Maven.

## Pasos (con Android Studio + NDK instalado)

1. Cloná el repo oficial:
   `git clone https://github.com/androidx/media.git`
2. Cada extensión vive en `libraries/decoder_ffmpeg`,
   `libraries/decoder_av1` (dav1d), `libraries/decoder_iamf`,
   `libraries/decoder_mpegh`. Cada carpeta tiene su propio `README.md`
   con el script de build (usan `build_ffmpeg.sh`, etc.) — hay que
   correrlos para generar los `.so` para cada ABI que necesites
   (arm64-v8a como mínimo, si algún día también vas a distribuir para
   x86_64 agregalo).
3. Una vez compilados, se agregan como módulos locales del proyecto
   (`include(":lib-decoder-ffmpeg")` etc. en `settings.gradle.kts`) y
   como dependencia del módulo `:app`.
4. En el código, hay que decirle a ExoPlayer que los use, con un
   `RenderersFactory` personalizado en vez de la default:

   ```kotlin
   val renderersFactory = DefaultRenderersFactory(context)
       .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
   val player = ExoPlayer.Builder(context, renderersFactory).build()
   ```

   (`EXTENSION_RENDERER_MODE_PREFER` si querés que compita con el
   decoder de hardware y priorice el software cuando dé mejor calidad).

## Atajo (más rápido, menos prolijo)

Ya tenés los `.so` compilados dentro del APK de Just Player
(`lib/arm64-v8a/*.so`, etc.). Se pueden copiar tal cual a
`app/src/main/jniLibs/<abi>/` de este proyecto — son binarios Apache-2.0,
no hay problema de licencia. Lo que falta en ese caso son las clases
Java/Kotlin "wrapper" (`FfmpegAudioRenderer`, `Dav1dVideoRenderer`,
`IamfAudioRenderer`, `MpeghAudioRenderer`) que llaman a esos `.so` —
esas sí conviene sacarlas del código fuente oficial (paso 2 de arriba)
en vez de reconstruirlas a mano desde el DEX de Just Player.
