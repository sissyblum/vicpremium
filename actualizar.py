import os
import re
import sys

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
TARGET_FILE = os.path.join(BASE_DIR, "nico.txt")


def main():
    if len(sys.argv) < 2:
        print("ERROR: Falta la URL de Flow.")
        print("Uso: python actualizar.py \"https://servidor/tok_TOKEN/live/...\"")
        return 1

    url_nueva = sys.argv[1].strip()

    if not url_nueva.startswith(("http://", "https://")) or "/live/" not in url_nueva:
        print("ERROR: La URL no es válida o no contiene '/live/'.")
        return 1

    # Conservamos únicamente la base hasta /live/.
    base_nueva = url_nueva.split("/live/", 1)[0].rstrip("/")

    if not os.path.isfile(TARGET_FILE):
        print(f"ERROR: No existe el archivo: {TARGET_FILE}")
        return 1

    with open(TARGET_FILE, "r", encoding="utf-8-sig", errors="ignore") as f:
        contenido = f.read()

    # Reemplaza solamente URLs de Flow que tengan la estructura:
    # https://servidor/tok_TOKEN/live/...
    patron = re.compile(
        r'https?://[^\s"<>]+?/live/'
    )

    contenido_actualizado, cantidad = patron.subn(
        base_nueva + "/live/",
        contenido
    )

    if cantidad == 0:
        print("No se encontraron URLs con /live/ para actualizar.")
        return 2

    with open(TARGET_FILE, "w", encoding="utf-8", newline="\n") as f:
        f.write(contenido_actualizado)

    print(f"URL base actualizada correctamente.")
    print(f"Archivo: {TARGET_FILE}")
    print(f"Enlaces modificados: {cantidad}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
